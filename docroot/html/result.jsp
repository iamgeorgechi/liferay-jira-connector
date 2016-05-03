<%@ include file="/html/init.jsp" %>

<%
String issueKey = (String)request.getAttribute("issueKey");
String issueSummary = (String)request.getAttribute("issueSummary");
String issueType = (String)request.getAttribute("issueType");
String issueStatus = (String)request.getAttribute("issueStatus");
String issuePriority = (String)request.getAttribute("issuePriority");
String issueComponents = (String)request.getAttribute("issueComponents");
String issueAssignee = (String)request.getAttribute("issueAssignee");
String issueReporter = (String)request.getAttribute("issueReporter");
String issueCreateDateTime = (String)request.getAttribute("issueCreateDateTime");
String issueUpdateDateTime = (String)request.getAttribute("issueUpdateDateTime");
String issueEpicLink = (String)request.getAttribute("issueEpicLink");
String issueDescription = (String)request.getAttribute("issueDescription");
String userInput = (String)request.getAttribute("userInput");
String jiraServerUrl = GetterUtil.getString(portletPreferences.getValue("jiraServerUrl", StringPool.BLANK));
%>

<liferay-ui:error
	key="forbidden"
	message="You do not have the permission to view ${userInput}."
/>
<liferay-ui:error
	key="issueDoesNotExist"
	message="${userInput} does not exist."
/>
<liferay-ui:error
	key="emptyUserInput"
	message="Please enter a valid issue key."
/>

<portlet:renderURL var="searchJiraURL">
	<portlet:param name="mvcPath" value="/html/view.jsp" />
</portlet:renderURL>

<p><a href="<%= searchJiraURL %>">&larr; Back</a></p>

<%
if (userInput == null) {
%>

	<b>Key:</b> <a href="<%= jiraServerUrl %>/browse/<%= issueKey %>" target="_blank" ><%= issueKey %></a><br />
	<b>Summary:</b> <%= issueSummary %><br />
	<br />
	<b>Type:</b> <%= issueType %><br />
	<b>Status:</b> <%= issueStatus %><br />
	<b>Priority:</b> <%= issuePriority %><br />
	<b>Component/s:</b> <%= issueComponents %><br />
	<b>Epic Link:</b> <%= issueEpicLink %><br />
	<br />
	<b>Assigned to:</b> <%= issueAssignee %><br />
	<b>Reported By:</b> <%= issueReporter %><br /><br />
	<b>Created:</b> <%= issueCreateDateTime %><br />
	<b>Last Updated:</b> <%= issueUpdateDateTime %><br />
	<br />
	<b>Description:</b><br /><%= issueDescription %>

<%
}
%>