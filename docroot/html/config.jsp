<%@ include file="/html/init.jsp" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<%
String jiraUserName = GetterUtil.getString(portletPreferences.getValue("jiraUserName", StringPool.BLANK));
String jiraPassword = GetterUtil.getString(portletPreferences.getValue("jiraPassword", StringPool.BLANK));
String jiraServerUrl = GetterUtil.getString(portletPreferences.getValue("jiraServerUrl", StringPool.BLANK));
%>

<aui:form action="<%= configurationURL %>" method="post">
	<aui:input
		name="<%= com.liferay.portal.kernel.util.Constants.CMD %>"
		type="hidden"
		value="<%= com.liferay.portal.kernel.util.Constants.UPDATE %>"
	/>

	<aui:input name="preferences--jiraUserName--" label="jira-username" value="<%= jiraUserName %>" />
	<aui:input type="password"  name="preferences--jiraPassword--" label="jira-password" value="<%= jiraPassword %>" />
	<aui:input name="preferences--jiraServerUrl--" label="jira-server-url" value="<%= jiraServerUrl %>" placeholder="<%= jiraServerUrl %>" />

	<aui:button type="submit" />
</aui:form>