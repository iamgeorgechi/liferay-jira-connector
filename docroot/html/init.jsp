<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %>

<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ page import="com.liferay.portlet.asset.model.AssetCategory" %>
<%@ page import="com.liferay.portlet.asset.model.AssetVocabulary" %>
<%@ page import="com.liferay.portlet.asset.service.AssetVocabularyLocalServiceUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>

<%@ page import="javax.portlet.PortletPreferences" %>
<%@ page import="com.atlassian.jira.rest.client.api.JiraRestClientFactory" %>
<%@ page import="com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory" %>
<%@ page import="com.atlassian.jira.rest.client.api.JiraRestClient" %>
<%@ page import="java.net.URI" %>
<%@ page import="com.atlassian.jira.rest.client.api.ProjectRestClient" %>
<%@ page import="com.atlassian.jira.rest.client.api.domain.Project" %>
<%@ page import="com.atlassian.util.concurrent.Promise" %>
<%@ page import="com.atlassian.jira.rest.client.api.domain.BasicProject" %>
<%@ page import="java.util.Iterator" %>

<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="com.liferay.portlet.PortletURLUtil" %>

<%@ page import="java.util.ArrayList" %>
<%@ page import="com.atlassian.jira.rest.client.api.domain.IssueType" %>
<%@ page import="com.atlassian.jira.rest.client.api.domain.BasicComponent" %>
<%@ page import="java.util.Iterator" %>


<portlet:defineObjects />
<liferay-theme:defineObjects />