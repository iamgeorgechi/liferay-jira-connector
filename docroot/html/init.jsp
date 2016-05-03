<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.atlassian.jira.rest.client.api.JiraRestClient" %>
<%@ page import="com.atlassian.jira.rest.client.api.JiraRestClientFactory" %>
<%@ page import="com.atlassian.jira.rest.client.api.ProjectRestClient" %>
<%@ page import="com.atlassian.jira.rest.client.api.domain.BasicComponent" %>
<%@ page import="com.atlassian.jira.rest.client.api.domain.BasicProject" %>
<%@ page import="com.atlassian.jira.rest.client.api.domain.IssueType" %>
<%@ page import="com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory" %>
<%@ page import="com.atlassian.util.concurrent.Promise" %>

<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>
<%@ page import="com.liferay.portlet.PortletURLUtil" %>

<%@ page import="javax.portlet.PortletURL" %>

<%@ page import="java.net.URI" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>


<portlet:defineObjects />
<liferay-theme:defineObjects />