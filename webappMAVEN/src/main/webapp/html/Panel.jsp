<%@page import="com.oramind.dao.ServiceDAO"%>
<%@page import="com.oramind.bean.StudentBean"%>
<%@page import="java.util.List"%>
<%@page import="com.oramind.bean.UserBean"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.oramind.com/taglib" prefix="f"%>


<t:genericnav>
	<jsp:attribute name="header">
	</jsp:attribute>

	<jsp:attribute name="footer">
	</jsp:attribute>

	<jsp:attribute name="pageSetup">
		<script type="text/javascript" src="html/js/jquery-1.9.1.js"></script>
		<script type="text/javascript">
			function checkAvailability() {
				var checkboxes = document.getElementsByName("stID");
				var allUnchecked = true;
				var i;
				for (i = 0; i < checkboxes.length; i++) {
					if (checkboxes[i].checked == true) {
						allUnchecked = false;
					}
				}
				var button = document.getElementById("messageSubmitButton");
				button.disabled = (allUnchecked == true ? true : false);
			};
			
			function stopRKey(evt) {
				var evt = (evt) ? evt : ((event) ? event : null);
				var node = (evt.target) ? evt.target
						: ((evt.srcElement) ? evt.srcElement : null);
				if ((evt.keyCode == 13) && (node.type == "text")) {
					return false;
				}
			}
	
			document.onkeypress = stopRKey;
		</script>
	</jsp:attribute>

	<jsp:body>
		<c:if test="${param.success !=null }">
			<div class="notificationSmall">Messages were sent successfully</div>
		</c:if>
		<div id="sendResult" style="display: none"></div>
		<table border="1" class="tableClass">
			<tr>
				<th>Select</th>
				<th>Name</th>
				<th>LastName</th>
				<th>Edit Student</th>
			</tr>
			<c:forEach items="${students}" var="bean">
				<tr>
					<td>
						<input type="checkbox" name="stID" value="${bean.studentID }" form="multipleSelectForm" id="studentSelectButton" onClick="checkAvailability();" />
						<input type="hidden" name="studentid" value="${bean.studentID }" form="editStudentForm${bean.studentID}" />
					</td>
					<td>${bean.name }</td>
					<td>${bean.lastName }</td>
					<td>
						<form method="POST" action="${pageContext.request.contextPath}/EditStudent" id="editStudentForm${bean.studentID}">
							<input type="submit" value="Edit" form="editStudentForm${bean.studentID}"/>
						</form>
					</td>
				</tr>
			</c:forEach>
		</table>
		
		<p></p>

		
		
		<form id="multipleSelectForm"
				 	action="${pageContext.request.contextPath}/SendMessage" method="post"
					enctype="application/x-www-form-urlencoded"
					accept-charset="UTF-8">
			<fieldset>
				<legend>Send message to selected students</legend>
				<input type="text" name="content" width="150" /> 
				<input type="submit" value="Send" id="messageSubmitButton" disabled="disabled" onmouseup="this.value='Sending Messages ..';this.disabled='disabled'; this.form.submit();" />
			</fieldset>
		</form>
		<p></p>
	</jsp:body>
	
</t:genericnav>
