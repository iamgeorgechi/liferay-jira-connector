<%@ include file="/html/init.jsp" %>

<%
ArrayList<IssueType> issueTypes = (ArrayList<IssueType>)request.getAttribute("issueTypes");
Iterator<BasicComponent> compIterator = (Iterator<BasicComponent>)request.getAttribute("compIterator");
String projectName = (String)request.getAttribute("projectName");
String projectKey = (String)request.getAttribute("projectKey");
String redirect = (String)request.getAttribute("redirectUrl");
%>

<portlet:actionURL var="selectProjectURL">
	<portlet:param name="mvcPath" value="/html/createIssueForm.jsp" />
</portlet:actionURL>

<p><a  href="<%= redirect %>">&larr; Back</a></p>

<b>Project:</b> <%= projectName %><br /><br />

<portlet:actionURL var="createIssueURL">
	<portlet:param name="mvcPath" value="/html/result.jsp" />
</portlet:actionURL>

<aui:form action="<%= createIssueURL %>" method="post">
	<aui:input name="tab" type="hidden" value="Create JIRA Issue" />
	<aui:input name="projectKey" type="hidden" value="<%= projectKey %>" />
	<aui:select id="issueTypeSelect" name="issueTypeId" label="Issue Type" >
		<aui:option label="" />
		<%
		for (IssueType currIssueType : issueTypes) {
		%>
			<aui:option label="<%= currIssueType.getName() %>" value="<%= currIssueType.getId() %>" />
		<%
		}
		%>
	</aui:select>
	<aui:input name="summary" label="Summary" />
	<aui:select id="componentSelect" name="issueComponent" label="Component/s" >
		<aui:option label="" />
		<%
		while (compIterator.hasNext()) {
			BasicComponent currComponent = compIterator.next();
		%>
			<aui:option label="<%= currComponent.getName() %>" value="<%= currComponent.getId() %>" />
		<%
		}
		%>
	</aui:select>
	<aui:input type="textarea" name="description" label="Description" />
	<aui:button type="submit" />
</aui:form>