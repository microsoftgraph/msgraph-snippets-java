// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.util.Objects;

import com.microsoft.graph.models.Calendar;
import com.microsoft.graph.models.GiphyRatingType;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Team;
import com.microsoft.graph.models.TeamFunSettings;
import com.microsoft.graph.models.User;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.EventCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.GroupCollectionPage;
import com.microsoft.graph.requests.MessageCollectionPage;

import okhttp3.Request;

public class CreateRequests {
    public static void runSamples(GraphServiceClient<Request> graphClient) {
        // Create a new message
        final Message tempMessage = new Message();
        tempMessage.subject = "Temporary";

        final Message createdMessage = Objects
            .requireNonNull(graphClient.me().messages().buildRequest().post(tempMessage));

        final String messageId = createdMessage.id;

        // Get a team to update
        final GroupCollectionPage teams = Objects
            .requireNonNull(graphClient.groups().buildRequest()
                .filter("resourceProvisioningOptions/Any(x:x eq 'Team')").get());

        final String teamId = teams.getCurrentPage().get(0).id;

        makeReadRequest(graphClient);
        makeSelectRequest(graphClient);
        makeListRequest(graphClient);
        makeItemByIdRequest(graphClient, messageId);
        makeExpandRequest(graphClient, messageId);
        makeDeleteRequest(graphClient, messageId);
        makeCreateRequest(graphClient);
        makeUpdateRequest(graphClient, teamId);
        makeHeadersRequest(graphClient);
        makeQueryParametersRequest(graphClient);
    }

    private static User makeReadRequest(GraphServiceClient<Request> graphClient) {
        // <ReadRequestSnippet>
        // GET https://graph.microsoft.com/v1.0/me
        final User user = graphClient.me().buildRequest().get();
        // </ReadRequestSnippet>

        return user;
    }

    private static User makeSelectRequest(GraphServiceClient<Request> graphClient) {
        // <SelectRequestSnippet>
        // GET https://graph.microsoft.com/v1.0/me?$select=displayName,jobTitle
        final User user = graphClient.me().buildRequest().select("displayName,jobTitle")
            .get();
        // </SelectRequestSnippet>

        return user;
    }

    private static MessageCollectionPage makeListRequest(
        GraphServiceClient<Request> graphClient) {
        // <ListRequestSnippet>
        // GET https://graph.microsoft.com/v1.0/me/messages?
        // $select=subject,sender&$filter=subject eq 'Hello world'
        final MessageCollectionPage messages = graphClient.me().messages().buildRequest()
            .select("subject,sender").filter("subject eq 'Hello world'").get();
        // </ListRequestSnippet>

        return messages;
    }

    private static Message makeItemByIdRequest(GraphServiceClient<Request> graphClient,
        String messageId) {
        if (null == messageId) {
            return null;
        }
        // <ItemByIdRequestSnippet>
        // GET https://graph.microsoft.com/v1.0/me/messages/{message-id}
        // messageId is a string containing the id property of the message
        final Message message = graphClient.me().messages(messageId).buildRequest().get();
        // </ItemByIdRequestSnippet>

        return message;
    }

    private static Message makeExpandRequest(GraphServiceClient<Request> graphClient,
        String messageId) {
        if (null == messageId) {
            return null;
        }
        // <ExpandRequestSnippet>
        // GET
        // https://graph.microsoft.com/v1.0/me/messages/{message-id}?$expand=attachments
        // messageId is a string containing the id property of the message
        final Message message = graphClient.me().messages(messageId).buildRequest()
            .expand("attachments").get();
        // </ExpandRequestSnippet>

        return message;
    }

    private static void makeDeleteRequest(GraphServiceClient<Request> graphClient,
        String messageId) {
        if (null == messageId) {
            return;
        }
        // <DeleteRequestSnippet>
        // DELETE https://graph.microsoft.com/v1.0/me/messages/{message-id}
        // messageId is a string containing the id property of the message
        graphClient.me().messages(messageId).buildRequest().delete();
        // </DeleteRequestSnippet>
    }

    private static Calendar makeCreateRequest(GraphServiceClient<Request> graphClient) {
        // <CreateRequestSnippet>
        // POST https://graph.microsoft.com/v1.0/me/calendars
        final Calendar calendar = new Calendar();
        calendar.name = "Volunteer";

        final Calendar newCalendar = graphClient.me().calendars().buildRequest()
            .post(calendar);
        // </CreateRequestSnippet>

        return newCalendar;
    }

    private static void makeUpdateRequest(GraphServiceClient<Request> graphClient,
        String teamId) {
        if (null == teamId) {
            return;
        }
        // <UpdateRequestSnippet>
        // PATCH https://graph.microsoft.com/v1.0/teams/{team-id}
        final Team team = new Team();
        final TeamFunSettings funSettings = new TeamFunSettings();
        funSettings.allowGiphy = true;
        funSettings.giphyContentRating = GiphyRatingType.STRICT;
        team.funSettings = funSettings;

        // teamId is a string containing the id property of the team
        graphClient.teams(teamId).buildRequest().patch(team);
        // </UpdateRequestSnippet>
    }

    private static EventCollectionPage makeHeadersRequest(
        GraphServiceClient<Request> graphClient) {
        // <HeadersRequestSnippet>
        // GET https://graph.microsoft.com/v1.0/me/events
        final EventCollectionPage events = graphClient.me().events()
            .buildRequest(
                new HeaderOption("Prefer", "outlook.timezone=\"Pacific Standard Time\""))
            .get();
        // </HeadersRequestSnippet>

        return events;
    }

    private static EventCollectionPage makeQueryParametersRequest(
        GraphServiceClient<Request> graphClient) {
        // <QueryParametersRequestSnippet>
        // GET https://graph.microsoft.com/v1.0/me/calendarView?
        // startDateTime=2023-06-14T00:00:00Z&endDateTime=2023-06-15T00:00:00Z
        final EventCollectionPage events = graphClient.me().events()
            .buildRequest(
                new QueryOption("startDateTime", "2023-06-14T00:00:00Z"),
                new QueryOption("endDateTime", "2023-06-15T00:00:00Z")
            ).get();
        // </QueryParametersRequestSnippet>

        return events;
    }
}
