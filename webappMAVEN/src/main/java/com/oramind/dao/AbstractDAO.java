/*******************************************************************************
 * Copyright (c) 2013, Christos Sotiriou
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 * -- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * -- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package com.oramind.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.oramind.abs.BeanInterface;
import com.oramind.db.DBProperties;
import com.oramind.db.DataPoolBoneCP;
import com.oramind.util.Util;

/**
 * An abstract DAO implementation, ready to be subclassed and used in any context
 * requiring access to data objects, and bean acquisition from an SQL database.
 * Implements the {@link DAOInterface} protocol, which specifies the basic
 * functionality of the DAO. It also provides facilities for getting a new
 * {@link Statement} from a {@link Connection}. If the getTracked*****() methods
 * are used, then any resource created because of these functions will be
 * tracked, which means that it can be closed by a call to the
 * {@link #cleanup()} function
 * <br><br>
 * General Rules of Thumb for {@link AbstractDAO}s subclasses and developers for proper use:
 * <ul>
 * <li>Try to use the Data Pool when running on a web server</li>
 * <li>Set a reusable connection only when you believe you are going to need to share a connection between functions</li>
 * <li>If you need to make many queries at once in a function, it's best to use {@link #getConnection()} once, and work with that
 * {@link Connection} (create your statements using the {@link #getTrackedCallableStatement(String, Connection)}, and then call {@link #cleanup()}).
 * <li>Never, ever, ever call {@link #cleanupReusableResources()} when you believe your reusable connection is being owned by another object that the
 * present object did not create</li>
 * <li>call {@link #cleanupReusableResources()} from the object that also called {@link #setReusableConnection(Connection)}.</li>
 *  
 * </ul>
 * <br>
 * If you have dying connections or timed out connections somewhere, you probably did not follow these rules properly.
 * <br>
 * <br>
 * 
 * Any {@link AbstractDAO} subclass can be assigned a reusable
 * {@link Connection}, to save time, resources and threads. If assigned, the
 * {@link Connection} will be reused for any subsequent transactions and
 * {@link Statement} creation.
 * <br><br>
 * <b>CONVENTIONS</b>
 * <ul>
 * <li>If a function of a subclass of {@link AbstractDAO} is about to return an object compliant to {@link BeanInterface}, then it will either return the object, or null if no
 * object is found matching the criteria in the arguments</li>
 * <li>If a function is about to return a {@link List} of objects, then the list will either be filled with objects, or empty. It will not be null. </li>
 * </ul>
 * 
 * @author Christos Sotiriou
 * 
 */
public abstract class AbstractDAO implements DAOInterface {

	private static boolean FORCE_DONT_USE_DATAPOOL = false;

	static {
		String dataPoolProperty = DBProperties.getInstance().getProperty("doNotUseDataPool");
		FORCE_DONT_USE_DATAPOOL = (dataPoolProperty.equalsIgnoreCase("true") ? true : false);
	}

	/**
	 * The active {@link Connection}s we have open. These connections are
	 * created any time the {@link #getConnection()} method is called, IF there
	 * is not any assigned reusable resource as a connection. Can be closed and
	 * emptied by a call to the {@link #cleanup()} function.
	 */
	private final List<Connection> activeConnections = new ArrayList<Connection>();

	/**
	 * The active statements we have. These statements are created any time a
	 * "getTracked***()" function is called. Can be closed and emptied by a call
	 * to the {@link #cleanup()} function.
	 */
	private final List<Statement> activeStatements = new ArrayList<Statement>();

	/**
	 * The active result sets we have. These resultSets are created any time a
	 * "getTracked***()" function is called. Can be closed and emptied by a call
	 * to the {@link #cleanup()} function.
	 */
	private final List<ResultSet> activeResultSets = new ArrayList<ResultSet>();

	/**
	 * In case where we have a reusable connection, the {@link #getConnection()}
	 * function will return this connection instead of looking into a datapool
	 * or creating a new connection. Note that in order to assign a new
	 * connection or close this resource, you must explicitly call
	 * {@link #cleanupReusableResources()}
	 */
	private Connection reusableConn = null;



	public AbstractDAO() {
		super();
	}

	/**
	 * Initializes the {@link AbstractDAO} with a reusable {@link Connection}.
	 * This connection will be used throughout the lifetime of all transactions
	 * of the Dao. NOTE: when you don't need the {@link Connection} you must
	 * call the {@link #cleanupReusableResources()} to release this connection.
	 * 
	 * @param reusableConnection
	 */
	public AbstractDAO(Connection reusableConnection) {
		this();
		try {
			setReusableConnection(reusableConnection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public Connection getConnection() {
		Connection result = null;
		/*
		 * If there is a reusable connection, prefer this instead.
		 */
		if (reusableConn != null) {
			Util.println("getting connection from reusable source");
			return reusableConn;
		}
		/*
		 * If the flag is set, always get a new connection
		 */
		if (FORCE_DONT_USE_DATAPOOL) {
			Util.println("creating new connection...");
			result = getPlainConnection();
			activeConnections.add(result);
		} else {
			/*
			 * If there is not any reusable connection, and we want to use
			 * the datapool, ask the datapool for a connection, and add it to
			 * the list of tracked connections
			 */
			try {
				Util.println("getting connection from datapool");
				result = DataPoolBoneCP.getOracleConnection();
				activeConnections.add(result);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Gets a new {@link PreparedStatement} and keeps track of it. The
	 * connection used will be created by the {@link #getConnection()} function.
	 * 
	 * @param sql
	 *            the SQL script to run.
	 * @return a new {@link PreparedStatement}, which can be closed with a call
	 *         to {@link #cleanup()}
	 * @throws SQLException
	 */
	protected PreparedStatement getTrackedPreparedStatement(String sql)
			throws SQLException {
		return getTrackedPreparedStatement(sql, false);
	}

	/**
	 * Gets a new {@link PreparedStatement} and keeps track of it. The
	 * connection used will be created by the {@link #getConnection()} function.
	 * 
	 * @param sql
	 *            the SQL script to run.
	 * @param returnGeneratedKeys
	 *            In case where this is set to true, the
	 *            {@link PreparedStatement} will return the generated keys after
	 *            the execution.
	 * @return a new {@link PreparedStatement}, which can be closed with a call
	 *         to {@link #cleanup()}
	 * @throws SQLException
	 */
	protected PreparedStatement getTrackedPreparedStatement(String sql, boolean returnGeneratedKeys) throws SQLException {
		return getTrackedPreparedStatement(sql, getConnection(), returnGeneratedKeys);
	}

	/**
	 * Gets a new {@link PreparedStatement} and keeps track of it. The
	 * connection used will be created by the {@link #getConnection()} function.
	 * 
	 * @param sql
	 *            the SQL script to run.
	 * @param returnGeneratedKeys
	 *            In case where this is set to true, the
	 *            {@link PreparedStatement} will
	 * @param connection
	 *            The connection to use. return the generated keys after the
	 *            execution.
	 * @return a new {@link PreparedStatement}, which can be closed with a call
	 *         to {@link #cleanup()}
	 * @throws SQLException
	 */
	protected PreparedStatement getTrackedPreparedStatement(String sql,
			Connection connection, boolean returnGeneratedKeys) throws SQLException {
		PreparedStatement result = (returnGeneratedKeys ? connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
				: connection.prepareStatement(sql));
		activeStatements.add(result);
		return result;
	}

	/**
	 * Gets a new {@link CallableStatement} that will be used to execute a
	 * Stored Procedure in the database.
	 * 
	 * @param sql
	 *            The SQL to run.
	 * @return a {@link CallableStatement}
	 * @throws SQLException
	 */
	protected CallableStatement getTrackedCallableStatement(String sql)
			throws SQLException {
		return getTrackedCallableStatement(sql, getConnection());
	}

	/**
	 * Gets a new {@link CallableStatement} that will be used to execute a
	 * Stored Procedure in the database. Can be closed with a call to
	 * {@link #cleanup()}
	 * 
	 * @param sql
	 *            The SQL to run.
	 * @param connection
	 *            The connection that will be used to create this statement
	 * @return a {@link CallableStatement}
	 * @throws SQLException
	 */
	protected CallableStatement getTrackedCallableStatement(String sql,
			Connection connection) throws SQLException {
		CallableStatement result = connection.prepareCall(sql);
		activeStatements.add(result);
		return result;
	}

	/**
	 * Gets a new plain {@link Statement}, tracked so that it can be closed with
	 * a call to {@link #cleanup()};
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected Statement getTrackedStatement() throws SQLException {
		Statement result = getConnection().createStatement();
		activeStatements.add(result);
		return getTrackedStatement(getConnection());
	}

	/**
	 * 
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	protected Statement getTrackedStatement(Connection con) throws SQLException{
		Connection connection = con != null? con : getConnection();
		Statement st = connection.createStatement();
		activeStatements.add(st);
		return st;
	}


	/**
	 * Executes a {@link PreparedStatement} and returns a {@link ResultSet}. If the prepared
	 * statement performs an update and does not return anything, the returned value is null
	 * @param st
	 * @param queryType
	 * @return a result set with the rows selected, or null if the {@link PreparedStatement} given as
	 * argument does not select anything
	 * @throws SQLException
	 */
	protected ResultSet getTrackedResultSet(PreparedStatement st,
			QueryType queryType) throws SQLException {
		Util.println(st.toString());
		ResultSet result = null;
		switch (queryType) {
		case update:
			st.executeUpdate();
			break;
		case select:
			result = st.executeQuery();
			break;
		case updateReturnGeneratedKeys:
			st.executeUpdate();
			result = st.getGeneratedKeys();
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * Gets a plain JDBC connection, based on the properties read from the
	 * {@link DBProperties} class. The connection is not tracked, and it is
	 * up to the user to close it and clean it up
	 * 
	 * @return
	 */
	protected Connection getPlainConnection() {
		Connection con = null;
		String dbName = DBProperties.getInstance().getProperty("dbname");
		String dbHost = DBProperties.getInstance().getProperty("dbhost");
		String dbPort = DBProperties.getInstance().getProperty("dbport");
		String otherArguments = DBProperties.getInstance().getProperty("mysqlPortURLArguments");
		
		String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + otherArguments;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, DBProperties.getInstance()
					.getProperty("dbuser"), DBProperties.getInstance()
					.getProperty("dbpass"));

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}



	protected enum QueryType {
		update, select, updateReturnGeneratedKeys
	}

	@Override
	public void cleanup() {
		for (Statement st : activeStatements) {
			try {
				st.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (Connection conn : activeConnections) {
			try {
				/**
				 * only clean up the connection if it is not the reusable one. the #getConnection() function will
				 * return a tracked connection that, if set to the reusable one, can provoque serious errors.
				 */
				if (reusableConn != conn) {
					conn.close();	
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (ResultSet rs : activeResultSets) {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Util.println("CLEANUP: closed " + activeStatements.size()
				+ " statements, " + activeResultSets.size() + " sets and "
				+ activeConnections.size() + " connections");

		activeStatements.clear();
		activeConnections.clear();
		activeResultSets.clear();
	}



	@Override
	public void setReusableConnection(Connection newReusableConnection)
			throws Exception {
		/**
		 * If the old reusable connection is not closed and it is different than the current one, throw an exception
		 * for violating the rules of the reusable connections.
		 */
        if (reusableConn != null && !reusableConn.isClosed() && newReusableConnection != reusableConn) {
        	throw new Exception(
					"MAJOR ERROR: Tried to set a new reusable connection, "
							+ "without closing the old one first. Try calling cleanupReusableResources() before setting a new reusable resource.");
		} else {
			this.reusableConn = newReusableConnection;
			/**
			 * Remove this connection from the list of the tracked ones. We want it to be independent.
			 */
			if (newReusableConnection != null && this.activeConnections.contains(newReusableConnection)) {
				Util.println("removeing reusable connections from the list of tracked connections...");
				this.activeConnections.remove(newReusableConnection);
			}
		}
	}
	
	
	/**
	 * Makes the DAO reuse a single connection for future requests.
	 * NOTE: if you call this function, you MUST call {@link #cleanupReusableResources()} when you are done
	 * with this DAO, otherwise there will be one connection open indefinitely.
	 */
	public void setupForReusingConnections(){
		/*
		 * We enclose it to a "try" block to make this function more "lenient" to mistakes such as setting it a second time.
		 * This is a choice based on design and intuitiveness. If the function is called at an appropriate time, then
		 * the 'setReusableConection' function will handle all errors, and will leave the connections unaltered if an error
		 * is found. Because the objects outside the DAO do not know the internals of the DAO completely, it's best to have
		 * some leniency when calling #setupForReusableConnections and handle errors in the "try" block.
		 */
		try{
			setReusableConnection(getConnection());	
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns whether this DAO is reusing a single connections for all future requests. 
	 * @return
	 */
	public boolean isSetupToReuseConnections(){
		return this.reusableConn != null;
	}
	
	
	/**
	 * Get the reusable connection for the object, if any. This method is protected because
	 * it is best from a design and safety perspective to not allow a DAO to mess with another DAO's
	 * reusable connection or to see anything about it, after the latter's initialization.
	 * @return
	 */
	protected Connection getReusableConn() {
		return reusableConn;
	}
	
	

	@Override
	public void cleanupReusableResources() throws SQLException {
		if (reusableConn != null) {
			if (!reusableConn.isClosed()) {
				reusableConn.close();
			}
			Util.println("setting to null 1 reusable connection (and closed it)");
			reusableConn = null;
		}
	}
}
