<%@page import="com.oramind.dao.ServiceDAO"%>
<%@page import="com.oramind.bean.StudentBean"%>
<%@page import="java.util.List"%>
<%@page import="com.oramind.bean.UserBean"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<t:genericnav>
	<jsp:attribute name="header">
	</jsp:attribute>
	
	<jsp:attribute name="footer">
	</jsp:attribute>
	
	<jsp:attribute name="pageSetup"></jsp:attribute>
	
	<jsp:body>

		<h1>${student.name } ${student.lastName }</h1>
		
		<form method="post"
			action="${pageContext.request.contextPath}/DeleteStudent">
			<fieldset>
				<legend>Student Info</legend>
				<table>
					<tr>
						<td width="200px">Name</td>
						<td width="*">${student.name }</td>
					</tr>
					<tr>
						<td width="200px">Surname</td>
						<td width="*">${student.lastName }</td>
					</tr>
				</table>
				<input type="hidden" value="${student.studentID}" name="studentid" />
			</fieldset>
			<input type="submit" value="DELETE STUDENT" />
		</form>
		
		
		<fieldset>
			<legend>Devices information</legend>
			<table border="1" class="tableClass">
				<tr>
					<th>APNS Identifier</th>
					<th>MAC Address</th>
					<th>Action</th>
				</tr>
				<c:forEach items="${student.devices}" var="device">
					<tr>
						<td>${device.apnsIdentifier }</td>
						<td>${device.macAddress }</td>
						<td><form method="post" action="${pageContext.request.contextPath}/DeleteDevice">
							<input type="hidden" name="deviceid" value="${device.deviceID }"/>
							<input type="hidden" name="studentid" value="${student.studentID }"/>
							<input type="submit" value="Delete this Device" />
						</form></td>
					</tr>
				</c:forEach>
			</table>
		</fieldset>
	</jsp:body>
</t:genericnav>