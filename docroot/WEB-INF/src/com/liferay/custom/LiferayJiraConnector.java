package com.liferay.custom;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.ProjectRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicIssueType;
import com.atlassian.jira.rest.client.api.domain.BasicStatus;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class LiferayJiraConnector extends MVCPortlet {
	@Override
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {

		String tab = actionRequest.getParameter("tab");

		if (tab.equals("Search JIRA")) {
			searchJira(actionRequest, actionResponse);
		}
		else if (tab.equals("Create Sites")) {
			createIssue(actionRequest, actionResponse);
		}

		actionResponse.setRenderParameter("tabs1", tab);
		super.processAction(actionRequest, actionResponse);
	}

	private void searchJira(ActionRequest actionRequest, ActionResponse actionResponse) {
		PortletPreferences portletPreferences = actionRequest.getPreferences();

		String jiraUserName = GetterUtil.getString(portletPreferences.getValue(
			"jiraUserName", StringPool.BLANK));
		String jiraPassword = GetterUtil.getString(portletPreferences.getValue(
			"jiraPassword", StringPool.BLANK));
		String jiraServerUrl = GetterUtil.getString(portletPreferences.getValue(
			"jiraServerUrl", StringPool.BLANK));

		String issueKey = actionRequest.getParameter("issueKey");

		JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		JiraRestClient restClient = factory.createWithBasicHttpAuthentication(
			URI.create(jiraServerUrl), jiraUserName, jiraPassword);
		IssueRestClient issueRestClient = restClient.getIssueClient();

		Promise<Issue> issuePromise = issueRestClient.getIssue(issueKey);

		try {
			Issue issue = issuePromise.get();

			actionRequest.setAttribute("issueKey", issue.getKey());
			actionRequest.setAttribute("issueSummary", issue.getSummary());
			actionRequest.setAttribute("issueAssignee", issue.getAssignee().getDisplayName());
			actionRequest.setAttribute("issueReporter", issue.getReporter().getDisplayName());

			BasicStatus status = issue.getStatus();
			actionRequest.setAttribute("issueStatus", status.getName());

			Iterable<BasicComponent> basicComponents = issue.getComponents();
			Iterator<BasicComponent> basicComponentIterator = basicComponents.iterator();
			StringBundler issueComponents = new StringBundler();
			while (basicComponentIterator.hasNext()) {
				if (issueComponents.length() == 0) {
					issueComponents.append(basicComponentIterator.next().getName());
				}
				else {
					issueComponents.append(", " + basicComponentIterator.next().getName());
				}
			}
			actionRequest.setAttribute("issueComponents", issueComponents.toString());

			BasicIssueType issueType = issue.getIssueType();
			actionRequest.setAttribute("issueType", issueType.getName());

			actionRequest.setAttribute("issuePriority", issue.getPriority().getName());

			DateTimeFormatter builder = DateTimeFormat.forPattern(Constants.DATETIME_FORMAT);
			DateTime createDateTime = issue.getCreationDate().withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone(Constants.TIMEZONE_AMERICA_LOS_ANGELES)));
			DateTime updateDateTime = issue.getUpdateDate().withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone(Constants.TIMEZONE_AMERICA_LOS_ANGELES)));

			actionRequest.setAttribute("issueCreateDateTime", createDateTime.toString(builder));
			actionRequest.setAttribute("issueUpdateDateTime", updateDateTime.toString(builder));

			IssueField issueField = issue.getFieldByName(Constants.CUSTOM_FIELD_EPIC_LINK);
			actionRequest.setAttribute("issueEpicLink", issueField.getValue().toString());

			actionRequest.setAttribute("issueDescription", issue.getDescription());
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	private void createIssue(ActionRequest actionRequest, ActionResponse actionResponse) {

		PortletPreferences portletPreferences = actionRequest.getPreferences();

		String jiraUserName = GetterUtil.getString(portletPreferences.getValue(
			"jiraUserName", StringPool.BLANK));
		String jiraPassword = GetterUtil.getString(portletPreferences.getValue(
			"jiraPassword", StringPool.BLANK));
		String jiraServerUrl = GetterUtil.getString(portletPreferences.getValue(
			"jiraServerUrl", "https://issues.liferay.com"));
		String jiraProjectKey = GetterUtil.getString(
			portletPreferences.getValue("jiraProjectKey", StringPool.BLANK));

		String guestName = actionRequest.getParameter("guestName");
		String guestEmail = actionRequest.getParameter("guestEmail");
		long companyId = Long.parseLong(actionRequest.getParameter(
			"companyId"));
		String reporterUserName = actionRequest.getParameter(
			"reporterUserName");
		String reporterUserEmail = actionRequest.getParameter(
			"reporterUserEmail");
		String summary = actionRequest.getParameter("summary");
		String description = actionRequest.getParameter("description");
		long categoryId = Long.parseLong(actionRequest.getParameter(
			"categoryId"));
		User reporter = null;
		AssetCategory category = null;

		JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		JiraRestClient restClient = factory.createWithBasicHttpAuthentication(
			URI.create(jiraServerUrl), jiraUserName, jiraPassword);
		ProjectRestClient projectRestClient = restClient.getProjectClient();
		Promise<Project> projectPromise = projectRestClient.getProject(
			jiraProjectKey);

		try {
			Project project = projectPromise.get();

			IssueInputBuilder issueInputBuilder = new IssueInputBuilder(
				project.getKey(), Constants.ISSUE_TYPE_ID_IDEA);

			if (Validator.isNotNull(reporterUserEmail)) {
				reporter = UserLocalServiceUtil.fetchUserByEmailAddress(
					companyId, reporterUserEmail);
			}

			if (Validator.isNotNull(categoryId)) {
				category = AssetCategoryLocalServiceUtil.fetchAssetCategory(
					categoryId);
			}

			Iterator<BasicComponent> basicComponentIterator =
				project.getComponents().iterator();

			StringBundler sbSummary = new StringBundler(2);
			sbSummary.append("New Article Idea: ");
			sbSummary.append(summary);

			StringBundler sbDescription = new StringBundler(4);
			sbDescription.append("Reporter: " +
				(reporter == null ? guestName : reporter.getFullName()));
			sbDescription.append("\n");
			sbDescription.append("\n");
			sbDescription.append(description);

			issueInputBuilder.setProjectKey(project.getKey());
			issueInputBuilder.setSummary(sbSummary.toString());
			issueInputBuilder.setDescription(sbDescription.toString());

			while (basicComponentIterator.hasNext()) {
				BasicComponent component = basicComponentIterator.next();

				if (component.getName().equals(category.getName())) {
					issueInputBuilder.setComponents(component);
				}
			}

			IssueInput issueInput = issueInputBuilder.build();
			Promise<BasicIssue> basicIssuePromise =
				restClient.getIssueClient().createIssue(issueInput);

			if (_log.isInfoEnabled()) {
				_log.info("Created issue: " + basicIssuePromise.get().getKey());
			}
		}
		catch (InterruptedException e) {
			SessionErrors.add(actionRequest, "error");

			if (_log.isErrorEnabled()) {
				_log.error("InterruptedException:");
				_log.error(e);
			}
		}
		catch (ExecutionException e) {
			SessionErrors.add(actionRequest, "error");

			if (_log.isErrorEnabled()) {
				_log.error("ExecutionException:");
				_log.error(e);
			}
		} catch (SystemException e) {
			SessionErrors.add(actionRequest, "error");

			if (_log.isErrorEnabled()) {
				_log.error("SystemException:");
				_log.error(e);
			}
		}

		SessionMessages.add(actionRequest, "success");
	}

	private static Log _log = LogFactoryUtil.getLog(
		LiferayJiraConnector.class);

}
