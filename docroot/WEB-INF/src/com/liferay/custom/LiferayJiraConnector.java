package com.liferay.custom;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.ProjectRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicIssueType;
import com.atlassian.jira.rest.client.api.domain.BasicStatus;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class LiferayJiraConnector extends MVCPortlet {
	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		String tab = actionRequest.getParameter("tab");

		if (tab.equals("Search JIRA")) {
			String userInput = actionRequest.getParameter("userInput");

			searchJira(actionRequest, userInput);
		}
		else if (tab.equals("Select Project")) {
			selectProject(actionRequest, actionResponse);
		}
		else if (tab.equals("Create JIRA Issue")) {
			createIssue(actionRequest, actionResponse);
		}

		actionResponse.setRenderParameter("tabs1", tab);
		super.processAction(actionRequest, actionResponse);
	}

	private void searchJira(
		ActionRequest actionRequest, String key) {

		JiraRestClient restClient = jiraAuthenticate(actionRequest);
		IssueRestClient issueRestClient = restClient.getIssueClient();

		Promise<Issue> issuePromise = issueRestClient.getIssue(key);

		try {
			Issue issue = issuePromise.get();

			actionRequest.setAttribute("issueKey", issue.getKey());
			actionRequest.setAttribute("issueSummary", issue.getSummary());
			actionRequest.setAttribute(
				"issueAssignee", issue.getAssignee().getDisplayName());
			actionRequest.setAttribute(
				"issueReporter", issue.getReporter().getDisplayName());

			BasicStatus status = issue.getStatus();
			actionRequest.setAttribute("issueStatus", status.getName());

			Iterator<BasicComponent> compIterator =
				issue.getComponents().iterator();
			StringBundler components = new StringBundler();
			while (compIterator.hasNext()) {
				if (components.length() == 0) {
					components.append(compIterator.next().getName());
				}
				else {
					components.append(", " + compIterator.next().getName());
				}
			}
			actionRequest.setAttribute(
				"issueComponents", components.toString());

			BasicIssueType issueType = issue.getIssueType();
			actionRequest.setAttribute("issueType", issueType.getName());

			actionRequest.setAttribute(
				"issuePriority", issue.getPriority().getName());

			DateTimeFormatter builder = DateTimeFormat.forPattern(
				Constants.DATETIME_FORMAT);
			DateTime createDateTime = issue.getCreationDate().withZone(
				DateTimeZone.forTimeZone(TimeZone.getTimeZone(
					Constants.TIMEZONE_AMERICA_LOS_ANGELES)));
			DateTime updateDateTime = issue.getUpdateDate().withZone(
				DateTimeZone.forTimeZone(TimeZone.getTimeZone(
					Constants.TIMEZONE_AMERICA_LOS_ANGELES)));

			actionRequest.setAttribute(
				"issueCreateDateTime", createDateTime.toString(builder));
			actionRequest.setAttribute(
				"issueUpdateDateTime", updateDateTime.toString(builder));

			IssueField issueField = issue.getFieldByName(
				Constants.CUSTOM_FIELD_EPIC_LINK);
			actionRequest.setAttribute(
				"issueEpicLink", issueField.getValue().toString());

			actionRequest.setAttribute(
				"issueDescription", issue.getDescription());
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (ExecutionException e) {
			if (e.getMessage().contains(Constants.ERROR_403)) {
				actionRequest.setAttribute("userInput", key);
				SessionErrors.add(actionRequest, "forbidden");
			}
			else if (e.getMessage().contains(Constants.ERROR_404)) {
				actionRequest.setAttribute("userInput", key);
				SessionErrors.add(actionRequest, "issueDoesNotExist");
			}
			else if (e.getMessage().contains(Constants.ERROR_405)) {
				actionRequest.setAttribute("userInput", key);
				SessionErrors.add(actionRequest, "emptyUserInput");
			}
		}
	}

	private void selectProject(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String projectKey = actionRequest.getParameter("projectKey");
		String redirect = actionRequest.getParameter("redirect");

		JiraRestClient restClient = jiraAuthenticate(actionRequest);
		ProjectRestClient projectRestClient = restClient.getProjectClient();
		Promise<Project> projectPromise = projectRestClient.getProject(
			projectKey);

		Project jiraProject = projectPromise.claim();
		OptionalIterable<IssueType> issueTypesIterable =
			jiraProject.getIssueTypes();
		Iterator<IssueType> issueTypeIterator = issueTypesIterable.iterator();
		ArrayList<IssueType> issueTypes = new ArrayList<IssueType>();
		while (issueTypeIterator.hasNext()) {
			IssueType currIssueType = issueTypeIterator.next();
			if (!currIssueType.getName().contains(
					Constants.ISSUE_TYPE_SUB_TASK)) {
				issueTypes.add(currIssueType);
			}
		}

		Iterable<BasicComponent> compIterable = jiraProject.getComponents();
		Iterator<BasicComponent> compIterator = compIterable.iterator();

		actionRequest.setAttribute("compIterator", compIterator);
		actionRequest.setAttribute("issueTypes", issueTypes);
		actionRequest.setAttribute("projectName", jiraProject.getName());
		actionRequest.setAttribute("redirectUrl", redirect);
		actionRequest.setAttribute("projectKey", projectKey);
	}

	private void createIssue(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String projectKey = actionRequest.getParameter("projectKey");
		String summary = actionRequest.getParameter("summary");
		String description = actionRequest.getParameter("description");
		long issueComponent = Long.parseLong(actionRequest.getParameter(
			"issueComponent"));
		long issueTypeId = Long.parseLong(actionRequest.getParameter(
			"issueTypeId"));

		JiraRestClient restClient = jiraAuthenticate(actionRequest);
		ProjectRestClient projectRestClient = restClient.getProjectClient();
		Promise<Project> projectPromise = projectRestClient.getProject(
			projectKey);

		try {
			Project project = projectPromise.get();

			IssueInputBuilder issueInputBuilder = new IssueInputBuilder(
				projectKey, issueTypeId);

			issueInputBuilder.setProjectKey(project.getKey());
			issueInputBuilder.setSummary(summary);
			issueInputBuilder.setDescription(description);

			Iterable<BasicComponent> compIterable = project.getComponents();
			Iterator<BasicComponent> compIterator = compIterable.iterator();
			while (compIterator.hasNext()) {
				BasicComponent currComponent = compIterator.next();
				if (currComponent.getId().equals(issueComponent)) {
					issueInputBuilder.setComponents(currComponent);
				}
			}

			IssueInput issueInput = issueInputBuilder.build();
			Promise<BasicIssue> basicIssuePromise =
				restClient.getIssueClient().createIssue(issueInput);

			searchJira(actionRequest, basicIssuePromise.get().getKey());

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
		}

		SessionMessages.add(actionRequest, "success");
	}

	private JiraRestClient jiraAuthenticate(ActionRequest actionRequest) {

		PortletPreferences portletPreferences = actionRequest.getPreferences();

		String jiraUserName = GetterUtil.getString(portletPreferences.getValue(
			"jiraUserName", StringPool.BLANK));
		String jiraPassword = GetterUtil.getString(portletPreferences.getValue(
			"jiraPassword", StringPool.BLANK));
		String jiraServerUrl = GetterUtil.getString(portletPreferences.getValue(
			"jiraServerUrl", StringPool.BLANK));

		JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		JiraRestClient restClient = factory.createWithBasicHttpAuthentication(
			URI.create(jiraServerUrl), jiraUserName, jiraPassword);

		return restClient;
	}

	private static Log _log = LogFactoryUtil.getLog(
		LiferayJiraConnector.class);

}
