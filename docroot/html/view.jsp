<%@ include file="/html/init.jsp" %>

<liferay-ui:success key="success" message="Your request completed successfully." />

<%
String tabs1 = ParamUtil.getString(request, "tabs1", "Search JIRA");
String tabNames = "Search JIRA,Create JIRA Issue";
%>

<liferay-portlet:renderURL var="portletURL"/>

<liferay-ui:tabs
	names="<%= tabNames %>"
	url="<%= portletURL.toString() %>"
/>

<%
if (tabs1.equals("Search JIRA")) {
%>
<portlet:actionURL var="searchJiraURL">
	<portlet:param name="mvcPath" value="/html/result.jsp" />
</portlet:actionURL>

<h4>This portlet fetches information from JIRA.</h4><br />

<aui:form action="<%= searchJiraURL %>" method="post">
	<aui:input name="tab" type="hidden" value="<%= tabs1 %>" />
	<aui:input name="userInput" label="Issue Key" />
	<aui:button type="submit" />
</aui:form>

<%
}

if (tabs1.equals("Create JIRA Issue")) {
%>
<portlet:actionURL var="selectProjectURL">
	<portlet:param name="mvcPath" value="/html/createIssueForm.jsp" />
</portlet:actionURL>

<h4>This portlet creates an issue in JIRA</h4><br />

<%
PortletURL currentURLObj = PortletURLUtil.getCurrent(renderRequest, renderResponse);

String currentURL = currentURLObj.toString();
String project = "Project";

String jiraUserName = GetterUtil.getString(portletPreferences.getValue("jiraUserName", StringPool.BLANK));
String jiraPassword = GetterUtil.getString(portletPreferences.getValue("jiraPassword", StringPool.BLANK));
String jiraServerUrl = GetterUtil.getString(portletPreferences.getValue("jiraServerUrl", StringPool.BLANK));
JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
JiraRestClient restClient = factory.createWithBasicHttpAuthentication(URI.create(jiraServerUrl), jiraUserName, jiraPassword);
ProjectRestClient projectRestClient = restClient.getProjectClient();

Promise<Iterable<BasicProject>> allProjects = projectRestClient.getAllProjects();
Iterator<BasicProject> basicProjectIterator = allProjects.claim().iterator();
%>

<aui:form action="<%= selectProjectURL %>" method="post">
	<aui:input name="tab" type="hidden" value="Select Project" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:select id="projectSelect" name="projectKey" label="<%= project %>" >
		<aui:option label="" />
		<%
		while (basicProjectIterator.hasNext()) {
			BasicProject currProject = basicProjectIterator.next();
		%>
			<aui:option label="<%= currProject.getName() %>" value="<%= currProject.getKey() %>" />
		<%
		}
		%>
	</aui:select>
	<aui:button type="submit" />
</aui:form>

<%
}
%>