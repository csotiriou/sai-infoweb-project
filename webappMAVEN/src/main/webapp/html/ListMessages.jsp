<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<t:genericnav>
	<jsp:attribute name="header">
	</jsp:attribute>
	
	<jsp:attribute name="footer">
	</jsp:attribute>
	
	<jsp:attribute name="pageSetup"></jsp:attribute>
	
	<jsp:body>
	<div align="center">Messages sent to students</div>
		<table border="1" class="tableClass">
			<tr>
				<th>Student Name</th>
				<th>Student Surname</th>
				<th>Message Content</th>
				<th>Date</th>
			</tr>
			<c:forEach items="${beans}" var="bean">
				<tr>
					<td>${bean.studentName }</td>
					<td>${bean.studentLastName }</td>
					<td>${bean.content }</td>
					<td>${bean.dateSent }</td>
				</tr>
			</c:forEach>
		</table>
	</jsp:body>
</t:genericnav>
</body>
</html>