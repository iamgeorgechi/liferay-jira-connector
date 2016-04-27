<%@ include file="/html/init.jsp" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<%
String jiraUserName = GetterUtil.getString(portletPreferences.getValue("jiraUserName", StringPool.BLANK));
String jiraPassword = GetterUtil.getString(portletPreferences.getValue("jiraPassword", StringPool.BLANK));
String jiraProjectKey = GetterUtil.getString(portletPreferences.getValue("jiraProjectKey", StringPool.BLANK));
String jiraServerUrl = GetterUtil.getString(portletPreferences.getValue("jiraServerUrl", "https://issues.liferay.com"));

List<AssetVocabulary> assetVocabularyList = AssetVocabularyLocalServiceUtil.getAssetVocabularies(QueryUtil.ALL_POS, QueryUtil.ALL_POS);

long selectedVocabulary = 0;

if (Validator.isNotNull(GetterUtil.getString(portletPreferences.getValue("vocabularyId", StringPool.BLANK)))) {
	selectedVocabulary = Long.parseLong(GetterUtil.getString(portletPreferences.getValue("vocabularyId", StringPool.BLANK)));
}
%>

<aui:form action="<%= configurationURL %>" method="post">
	<aui:input
		name="<%= com.liferay.portal.kernel.util.Constants.CMD %>"
		type="hidden"
		value="<%= com.liferay.portal.kernel.util.Constants.UPDATE %>"
	/>

	<aui:select helpMessage="Select the vocabulary that contains the Knowledge Base categories" label="vocabulary" name="preferences--vocabularyId--" >
		<%
		for (AssetVocabulary currentAssetVocabulary : assetVocabularyList) {
		%>
			<aui:option
				label="<%= currentAssetVocabulary.getName() %>"
				selected='<%= (currentAssetVocabulary.getVocabularyId() == selectedVocabulary) ? true : false %>'
				value="<%= currentAssetVocabulary.getVocabularyId() %>"
			/>
		<%
		}
		%>
	</aui:select>
	<aui:input name="preferences--jiraUserName--" label="jira-username" value="<%= jiraUserName %>" />
	<aui:input type="password"  name="preferences--jiraPassword--" label="jira-password" value="<%= jiraPassword %>" />
	<aui:input name="preferences--jiraProjectKey--" label="jira-project-key" value="<%= jiraProjectKey %>" />
	<aui:input name="preferences--jiraServerUrl--" label="jira-server-url" value="<%= jiraServerUrl %>" placeholder="<%= jiraServerUrl %>" />

	<aui:button type="submit" />
</aui:form>