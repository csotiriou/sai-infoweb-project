<%@tag description="Overall Page template" pageEncoding="UTF-8"%>
<%@attribute name="header" fragment="true"%>
<%@attribute name="footer" fragment="true"%>
<%@attribute name="pageSetup" fragment="true"%>

<html>
<head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/html/css/general.css">
<jsp:invoke fragment="pageSetup" />
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<body>
	<div id="pageheader">
		<div class="headerRounded">NotCenter Control Panel</div>
		<jsp:invoke fragment="header" />
	</div>
	<table style="width: 100%; background: #009ec3; border: 1px solid black;">
		<tr>
			<td align="center" width="50%"><a href="${pageContext.request.contextPath}/Panel">Panel</a></td>
			<td align="center" width="50%" ><a href="${pageContext.request.contextPath}/ListMessages"> Messages</a></td>
		</tr>
	</table>
	<div id="body" class="bodyBackground">
		<jsp:doBody />
	</div>

	<div id="pagefooter" class="footerRounded">
		<span>Copyright 2013 Telecom SudParis</span> <span class="floatRight"><a
			href="${pageContext.request.contextPath}/LogoutServlet">Logout</a></span>
		<jsp:invoke fragment="footer" />
	</div>

</body>
</html>