package com.epam.gepard.rest.jira;
/*==========================================================================
 Copyright 2004-2015 EPAM Systems

 This file is part of Gepard.

 Gepard is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Gepard is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Gepard.  If not, see <http://www.gnu.org/licenses/>.
===========================================================================*/

import com.epam.gepard.common.Environment;
import com.epam.gepard.exception.SimpleGepardException;
import com.epam.gepard.generic.GepardTestClass;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import org.apache.xerces.impl.dv.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This helper class provides JIRA Rest API connectivity features to Gepard.
 *
 * @author Tamas_Kohegyi
 */
public class JiraSiteHandler {

    private static final int HTTP_RESPONSE_OK = 200;
    private static final String STATUS_CHANGE_TEXT = "\"field\":\"status\"";
    private final Environment environment;
    private final WebClient webClient;

    /**
     * Constructor of the Jira Site Handler object.
     *
     * @param tc          is the caller test case
     * @param environment provide the Environment object of Gepard, it must contain connectivity info to JIRA.
     */
    public JiraSiteHandler(final GepardTestClass tc, final Environment environment) {
        this.environment = environment;
        webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setUseInsecureSSL(true);

        String phrase = Base64.encode((environment.getProperty(Environment.JIRA_SITE_USERNAME) + ":" + environment.getProperty(Environment.JIRA_SITE_PASSWORD)).getBytes());
        webClient.addRequestHeader("Authorization", "Basic " + phrase);

        try {
            JSONObject serverInfo = getJiraServerInfo();
            String serverTitle = serverInfo.get("serverTitle").toString();
            String version = serverInfo.get("version").toString();
            tc.logComment("Connected to JIRA Server: \"" + serverTitle + "\", version: " + version);
        } catch (FailingHttpStatusCodeException | IOException | JSONException e) {
            throw new SimpleGepardException("Cannot connect to JIRA properly, pls check the settings, reason: " + e.getMessage(), e);
        }
    }

    private String getJiraBaseUrl() {
        //in general: http://jira.blah.com
        return environment.getProperty(Environment.JIRA_SITE_URL);
    }

    private String getServerInfoUrl() {
        //http://jira.blah.com/rest/api/2/serverInfo
        return getJiraBaseUrl() + "/rest/api/2/serverInfo";
    }

    /**
     * Build a URL to jira to get change history of a ticket.
     *
     * @param jiraTicket is the jira ticket id, like : BLAH-2345
     * @return with the rest api url to jira
     */
    private String getIssueChangeLogUrl(final String jiraTicket) {
        //in general: http://jira.blah.com/rest/api/2/issue/ISSUE-ID?expand=changelog
        return getJiraBaseUrl() + "/rest/api/2/issue/" + jiraTicket + "?expand=changelog";
    }

    private String getIssueFieldValueUrl(final String jiraTicket) {
        //in general: http://jira.blah.com/rest/api/2/issue/ISSUE-ID?expand=projects.issuetypes.fields
        return getJiraBaseUrl() + "/rest/api/2/issue/" + jiraTicket + "?expand=projects.issuetypes.fields";
    }

    private String getIssueFieldSetValueUrl(String ticket) {
        return getJiraBaseUrl() + "/rest/api/2/issue/" + ticket;
    }

    private String getIssueTransitionsUrl(String ticket) {
        return getJiraBaseUrl() + "/rest/api/2/issue/" + ticket + "/transitions";
    }

    /**
     * URL --> https://jira.xxxxxx.com/rest/api/2/issue/TEST-53/transitions.
     */
    private String getIssueSetTransitionsUrl(String ticket) {
        return getJiraBaseUrl() + "/rest/api/2/issue/" + ticket + "/transitions?expand=transitions.fields";
    }

    private String getListOfIssuesByQueryUrl(final String query) {
        return getJiraBaseUrl() + "/rest/api/2/search?jql=" + makeJiraUrlFriendly(query) + "&maxResults=1000";
    }

    private String makeJiraUrlFriendly(final String query) {
        try {
            return URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new SimpleGepardException("Specified JIRA Query: \"" + query + "\" cannot be encoded to URL.", e);
        }
    }

    private String getTicketHistory(final GepardTestClass tc, final String ticketID) throws IOException {
        tc.logComment("Get JIRA history info on ticket: " + ticketID);
        String jiraInfoPage = getIssueChangeLogUrl(ticketID);
        UnexpectedPage infoPage = webClient.getPage(jiraInfoPage);
        String ticketInfo = infoPage.getWebResponse().getContentAsString();
        return ticketInfo;
    }

    /**
     * Gets the main Jira Server Information.
     *
     * @return with JSON object with Jira Server Information
     * @throws IOException   in case of problem
     * @throws JSONException in case of problem
     */
    public JSONObject getJiraServerInfo() throws IOException, JSONException {
        String jiraServerInfoPage = getServerInfoUrl();
        UnexpectedPage infoPage = webClient.getPage(jiraServerInfoPage);
        String serverInfo = infoPage.getWebResponse().getContentAsString();
        JSONObject obj = new JSONObject(serverInfo);
        return obj;
    }

    /**
     * Determine the date when a ticket status reached a specific status.
     *
     * @param tc         is the caller tc
     * @param ticket     is the JIRA ticket id
     * @param statusName is the status we are looking for
     * @return with date string - from JIRA - when the expected status change has happened
     * @throws IOException   in case of problem
     * @throws JSONException in case of problem
     */
    public String getTicketStatusChangeDate(final GepardTestClass tc, final String ticket, final String statusName) throws IOException, JSONException {
        String newTicketInfo = ticket;
        String ticketHistory = getTicketHistory(tc, ticket);
        JSONObject obj = new JSONObject(ticketHistory);
        obj = obj.getJSONObject("changelog");
        String startedDate = null;
        JSONArray arr = obj.getJSONArray("histories");
        for (int l = 0; l < arr.length(); l++) {
            String changeDate = arr.getJSONObject(l).getString("created");
            JSONArray changes = arr.getJSONObject(l).getJSONArray("items");
            for (int m = 0; m < changes.length(); m++) {
                String change = changes.getString(m);
                // example: "\"toString\":\"In Progress\""
                String statusChangeToName = "\"toString\":\"" + statusName + "\"";
                if (change.contains(STATUS_CHANGE_TEXT) && change.contains(statusChangeToName)) {
                    if (startedDate == null) {
                        startedDate = changeDate;
                        tc.logComment("Ticket: " + ticket + " was changed to \"" + statusName + "\" at " + changeDate);
                    }
                }
            }
        }
        if (startedDate == null) {
            tc.logComment("Ticket: " + ticket + " change to \"" + statusName + "\" status cannot be identified.");
        }
        return startedDate;
    }

    /**
     * Update a field of a JIRA ticket to a specified value.
     *
     * @param tc        is the caller test.
     * @param ticket    is the jira ticket id
     * @param fieldName is the name of the field
     * @param value     is the new value
     * @throws JSONException in case of problem
     * @throws IOException   in case of problem
     */
    public void updateTicketFieldValue(final GepardTestClass tc, final String ticket, final String fieldName, final String value) throws JSONException, IOException {
        String ticketFields = getTicketFields(ticket);
        JSONObject obj = new JSONObject(ticketFields);
        obj = obj.getJSONObject("fields");
        if (obj.has(fieldName)) {
            Object o = obj.get(fieldName);
            setTicketFieldValue(tc, ticket, fieldName, value);
        } else {
            throw new SimpleGepardException("Ticket: " + ticket + " field: " + fieldName + " cannot find.");
        }
    }

    /**
     * Get a field value of a JIRA ticket.
     *
     * @param tc        is the caller test.
     * @param ticket    is the jira ticket id
     * @param fieldName is the name of the field
     * @return with the field value
     * @throws JSONException in case of problem
     * @throws IOException   in case of problem
     */
    public String getTicketFieldValue(final GepardTestClass tc, final String ticket, final String fieldName) throws JSONException, IOException {
        String ticketFields = getTicketFields(ticket);
        JSONObject obj = new JSONObject(ticketFields);
        obj = obj.getJSONObject("fields");
        if (obj.has(fieldName)) {
            return obj.get(fieldName).toString();
        } else {
            throw new SimpleGepardException("Ticket: " + ticket + " field: " + fieldName + " cannot find.");
        }
    }

    private void setTicketFieldValue(final GepardTestClass tc, final String ticket, final String fieldName, final String value) throws IOException {
        String jiraSetFieldPage = getIssueFieldSetValueUrl(ticket);
        WebRequest requestSettings = new WebRequest(new URL(jiraSetFieldPage), HttpMethod.PUT);
        requestSettings.setAdditionalHeader("Content-type", "application/json");
        requestSettings.setRequestBody("{ \"fields\": {\"" + fieldName + "\": \"" + value + "\"} }");
        try {
            UnexpectedPage infoPage = webClient.getPage(requestSettings);
            if (infoPage.getWebResponse().getStatusCode() == HTTP_RESPONSE_OK) {
                tc.logComment("Field: '" + fieldName + "' of Ticket: " + ticket + " was updated to value: '" + value + "' successfully.");
            } else {
                throw new SimpleGepardException("Ticket: " + ticket + " field: " + fieldName + " update failed, status code: " + infoPage.getWebResponse().getStatusCode());
            }
        } catch (FailingHttpStatusCodeException e) {
            throw new SimpleGepardException("Ticket: " + ticket + " field: " + fieldName + " update failed.", e);
        }
    }

    private String getTicketFields(String ticket) throws IOException {
        String jiraInfoPage = getIssueFieldValueUrl(ticket);
        UnexpectedPage infoPage = webClient.getPage(jiraInfoPage);
        String ticketInfo = infoPage.getWebResponse().getContentAsString();
        return ticketInfo;
    }

    /**
     * Get a Jira query result in a map form.
     *
     * @param query is the jira query
     * @return with the map of JIRA_ID - ticketInfo in JSONObject pairs as result of a Jira query.
     * @throws IOException   in case of problem
     * @throws JSONException in case of problem
     */
    public ConcurrentHashMap<String, JSONObject> collectIssues(final String query) throws IOException, JSONException {
        String jqlURL = getListOfIssuesByQueryUrl(query);
        WebRequest requestSettings = new WebRequest(new URL(jqlURL), HttpMethod.GET);
        requestSettings.setAdditionalHeader("Content-type", "application/json");
        UnexpectedPage infoPage = webClient.getPage(requestSettings);
        if (infoPage.getWebResponse().getStatusCode() == HTTP_RESPONSE_OK) {
            String ticketList = infoPage.getWebResponse().getContentAsString();
            JSONObject obj = new JSONObject(ticketList);
            String maxResults = obj.getString("maxResults");
            String total = obj.getString("total");
            if (Integer.valueOf(maxResults) < Integer.valueOf(total)) {
                throw new SimpleGepardException("ERROR: Too many issues belong to given query (" + total + "), please change the query to shorten the result list.");
            }
            JSONArray array = obj.getJSONArray("issues");
            ConcurrentHashMap<String, JSONObject> map = new ConcurrentHashMap<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = (JSONObject) array.get(i);
                map.put(o.getString("key"), o);
            }
            return map;
        }
        throw new SimpleGepardException("ERROR: Cannot fetch Issue list from JIRA, status code:" + infoPage.getWebResponse().getStatusCode());
    }

    /**
     * Detect the actual workflow possibilities of the ticket, from its actual status.
     *
     * @param ticket is the id
     * @return with String representation of the list of possibilities, in form of statusTransferId;newStatusName strings
     * @throws IOException   in case of problem
     * @throws JSONException in case of problem
     */
    public String detectWorkflow(final String ticket) throws IOException, JSONException {
        String jqlURL;

        //first detect status
        String ticketFields = getTicketFields(ticket);
        JSONObject fieldObj = new JSONObject(ticketFields);
        fieldObj = fieldObj.getJSONObject("fields");
        fieldObj = fieldObj.getJSONObject("status");
        String status = "@" + fieldObj.get("name").toString();

        //then collect possible transactions
        jqlURL = getIssueTransitionsUrl(ticket);
        WebRequest requestSettings = new WebRequest(new URL(jqlURL), HttpMethod.GET);
        requestSettings.setAdditionalHeader("Content-type", "application/json");
        UnexpectedPage infoPage = webClient.getPage(requestSettings);
        if (infoPage.getWebResponse().getStatusCode() == HTTP_RESPONSE_OK) {
            String ticketList = infoPage.getWebResponse().getContentAsString();
            JSONObject obj = new JSONObject(ticketList);
            JSONArray array = obj.getJSONArray("transitions");
            List<String> toList = new ArrayList<>();
            toList.add(status);
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = (JSONObject) array.get(i);
                JSONObject o2 = o.getJSONObject("to");
                String toPossibility = o2.get("name").toString();
                String toPossibilityID = o.get("id").toString(); //it is the transition id not the status id
                toList.add(toPossibilityID + ";" + toPossibility);
            }
            return toList.toString();
        }
        throw new SimpleGepardException("ERROR: Cannot fetch Issue transition possibilities from JIRA, for ticket: "
                + ticket + ", Status code:" + infoPage.getWebResponse().getStatusCode());
    }

    /**
     * Transition a ticket to next status in its workflow.
     * URL --> https://jira.xxxxxx.com/rest/api/2/issue/TEST-53/transitions?expand=transitions.fields
     * POST data --> {"transition":{"id":91}}
     * Returned HTTP response --> 204
     *
     * @param ticket             is the specific ticket
     * @param statusTransferId   is the id of the transaction that should be used to transfer the ticket
     * @param expectedStatusName is the name of the expected new status
     * @param comment            that will be attached to the ticket as comment of the transaction
     * @return with the new status
     */
    private String transferTicketToStatus(final GepardTestClass tc, final String ticket, final String statusTransferId, final String expectedStatusName, final String comment)
            throws IOException, JSONException {
        String updateString = "{ \"update\": { \"comment\": [ { \"add\": { \"body\": \"" + comment + "\" } } ] }, \"transition\": { \"id\": \"" + statusTransferId + "\" } }";
        String oldStatus = detectActualStatus(ticket);
        String newStatus;
        String jiraSetFieldPage = getIssueSetTransitionsUrl(ticket);
        WebRequest requestSettings = new WebRequest(new URL(jiraSetFieldPage), HttpMethod.POST);
        requestSettings.setAdditionalHeader("Content-type", "application/json");
        requestSettings.setRequestBody(updateString);
        try {
            UnexpectedPage infoPage = webClient.getPage(requestSettings);
            if (infoPage.getWebResponse().getStatusCode() == HTTP_RESPONSE_OK) {
                newStatus = detectActualStatus(ticket);
                Assert.assertEquals("Transferring ticket: " + ticket + " to new status failed,", expectedStatusName, newStatus);
                tc.logComment("Ticket: " + ticket + " was transferred from status: \"" + oldStatus + "\" to status: \"" + newStatus + "\" successfully.");
            } else {
                throw new SimpleGepardException("ERROR: Status update failed for ticket: " + ticket + ", Status code:" + infoPage.getWebResponse().getStatusCode());
            }
        } catch (FailingHttpStatusCodeException e) {
            throw new SimpleGepardException("ERROR: Status update failed for ticket: " + ticket + ".", e);
        }
        return newStatus;
    }

    private String detectActualStatus(final String ticket) throws IOException, JSONException {
        String ticketFields = getTicketFields(ticket);
        JSONObject fieldObj = new JSONObject(ticketFields);
        fieldObj = fieldObj.getJSONObject("fields");
        fieldObj = fieldObj.getJSONObject("status");
        String status = "@" + fieldObj.get("name").toString();
        return status;
    }

}
