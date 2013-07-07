<%@page import="com.oramind.bean.UserBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<jsp:include page="./Include/Header.jspf"></jsp:include>

<form method="post">
	<fieldset title="Send message">
	<input type="text" name="content" />
	<input type="submit" />
	</fieldset>
</form>

<jsp:include page="./Include/Footer.jspf"></jsp:include>