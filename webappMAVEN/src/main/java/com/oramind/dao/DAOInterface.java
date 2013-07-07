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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Defines the behavior of a DAO to be used by the service.
 * @author Christos Sotiriou
 *
 */
public interface DAOInterface {
	
	/**
	 * Gets a connection ready to be used for transactions. The connection can be either a reusable one,
	 * an old connection from a datapool, or a newly-created {@link Connection}. This is implementation-depended.
	 * The {@link Connection} returned by this object must be already open. 
	 * @return a new {@link Connection} for SQL transactions.
	 */
	public Connection getConnection();
	
	
	/**
	 * Set a new reusable {@link Connection}. That means that any further request by this DAO will
	 * use this connection instead of trying to create a new one or getting one from a data pool.
	 * Note that you are responsible of cleaning up this reusable resource by calling {@link #cleanupReusableResources()}
	 * when you no longer need the dao. In case where the new reusable connection is one taken by {@link #getConnection()} of
	 * the same object, then, after setting the new reusable connection object, it will remove itself from the tracked connections
	 * to avoid closing the connection prematurely. Once you set a reusable connection, you cannot set another one in the same object.
	 * If you want to set another one, you must close the reusable connection first.
	 * @param newReusableConnection
	 * @throws Exception Will trigger an exception in case where there is an old reusable resource who has not been closed.
	 */
	public void setReusableConnection(Connection newReusableConnection) throws Exception;
	
	
	
	/**
	 * Any {@link Connection}s {@link Statement}s, and {@link ResultSet}s created automatically
	 * by the DAO will be closed here. NOTE: this does NOT close the reusable components that are set
	 * manually by the user. For closing manually set reusable connections, use {@link #cleanupReusableResources()}
	 * instead.
	 */
	public void cleanup();

	/**
	 * Closes any outstanding connections by the reusable resources in the DAO. NOTE: this will not alter
	 * any automatically created resources (by the "getTracked*****()" functions. BE CAREFUL. This will close the
	 * connection. If this reusable connection is being also used by another object, it will be invalidated. Therefore, make
	 * sure that you <b>do not call this function if the reusable connection is being delivered by another object</b>.<br>
	 * <b>General rule of thumb<b>: Only call {@link #cleanupReusableResources()} in the object who initially created the connection. 
	 * @throws SQLException
	 */
	public void cleanupReusableResources() throws SQLException;
}
