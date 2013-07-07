<%@page import="com.oramind.bean.UserBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="header">
	</jsp:attribute>
	
	<jsp:attribute name="footer">
	</jsp:attribute>
	
	<jsp:attribute name="pageSetup"></jsp:attribute>
	
	<jsp:body>
		<c:if test="${sessionScope.user != null }">
			<p> You are logged in as <span><c:out value="${sessionScope.user.name }"></c:out></span></p>
			<a href="${pageContext.request.contextPath}/Panel">Proceed to the Administration panel</a>
		</c:if> 
	
	<c:if test="${sessionScope.user == null }">
		<p>Please login in order to use the administrator control panel</p>
		<form method="POST" action="${pageContext.request.contextPath}/LoginServlet">
			<input type="text" name="user"> <input type="password" name="pass">
			<button type="submit" value="Login">Login</button>
		</form>
	</c:if>
	
	</jsp:body>
</t:genericpage>