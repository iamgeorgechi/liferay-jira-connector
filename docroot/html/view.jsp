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

<%
String userInput = "Enter the issue key";
%>

<aui:form action="<%= searchJiraURL %>" method="post">
	<aui:input name="companyId" type="hidden" value="<%= company.getCompanyId() %>" />
	<aui:input name="tab" type="hidden" value="<%= tabs1 %>" />
	<aui:input name="userInput" label="<%= userInput %>" />
	<aui:button type="submit" />
</aui:form>

<%
}

if (tabs1.equals("Create JIRA Issue")) {
%>
<portlet:actionURL var="createIssueURL">
	<portlet:param name="mvcPath" value="/html/view.jsp" />
</portlet:actionURL>

<h4>This portlet creates an issue in JIRA</h4><br />

<%
String numberOfSitesLabel = "Enter the number of sites you would like to create";
String baseSiteNameLabel = "Enter the base name for the sites";
%>

<aui:form action="<%= createIssueURL %>" method="post">
	<aui:input name="companyId" type="hidden" value="<%= company.getCompanyId() %>" />
	<aui:input name="tab" type="hidden" value="<%= tabs1 %>" />
	<aui:input name="numberOfSites" label="<%= numberOfSitesLabel %>" /><br />
	<aui:input name="baseSiteName" label="<%= baseSiteNameLabel %>" /><br />

	<aui:button type="submit" />
</aui:form>

<%
}
%>