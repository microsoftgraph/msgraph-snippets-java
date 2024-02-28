// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.util.Objects;

import com.microsoft.graph.models.*;
import com.microsoft.graph.serviceclient.GraphServiceClient;

public class CreateRequests {
    public static void runSamples(GraphServiceClient graphClient) {
        // Create a new message
        final Message tempMessage = new Message();
        tempMessage.setSubject("Temporary");

        final Message createdMessage = Objects
            .requireNonNull(graphClient.me().messages().post(tempMessage));

        final String messageId = createdMessage.getId();

        // Get a team to update
        final GroupCollectionResponse teams = Objects.requireNonNull(graphClient.groups().get( requestConfiguration -> {
            requestConfiguration.queryParameters.filter = "resourceProvisioningOptions/Any(x:x eq 'Team')";
        }));

        final String teamId = teams.getValue().get(0).getId();

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

    private static User makeReadRequest(GraphServiceClient graphClient) {
        // <ReadRequestSnippet>
        // GET https://graph.microsoft.com/v1.0/me
        final User user = graphClient.me().get();
        // </ReadRequestSnippet>

        return user;
    }

    private static User makeSelectRequest(GraphServiceClient graphClient) {
        // <SelectRequestSnippet>
        // GET https://graph.microsoft.com/v1.0/me?$select=displayName,jobTitle
        final User user = graphClient.me().get( requestConfiguration -> {
            requestConfiguration.queryParameters.select = new String[] {"displayName", "jobTitle"};
        });
        // </SelectRequestSnippet>

        return user;
    }

    private static MessageCollectionResponse makeListRequest(
        GraphServiceClient graphClient) {
        // <ListRequestSnippet>
        // GET https://graph.microsoft.com/v1.0/me/messages?
        // $select=subject,sender&$filter=subject eq 'Hello world'
        final MessageCollectionResponse messages = graphClient.me().messages().get( requestConfiguration -> {
            requestConfiguration.queryParameters.select = new String[] {"subject", "sender"};
            requestConfiguration.queryParameters.filter = "subject eq 'Hello world'";
        });
        // </ListRequestSnippet>

        return messages;
    }

    private static Message makeItemByIdRequest(GraphServiceClient graphClient,
        String messageId) {
        if (null == messageId) {
            return null;
        }
        // <ItemByIdRequestSnippet>
        // GET https://graph.microsoft.com/v1.0/me/messages/{message-id}
        // messageId is a string containing the id property of the message
        final Message message = graphClient.me().messages().byMessageId(messageId).get();
        // </ItemByIdRequestSnippet>

        return message;
    }

    private static Message makeExpandRequest(GraphServiceClient graphClient,
        String messageId) {
        if (null == messageId) {
            return null;
        }
        // <ExpandRequestSnippet>
        // GET
        // https://graph.microsoft.com/v1.0/me/messages/{message-id}?$expand=attachments
        // messageId is a string containing the id property of the message
        final Message message = graphClient.me().messages().byMessageId(messageId).get( requestConfiguration -> {
            requestConfiguration.queryParameters.expand = new String[] {"attachments"};
        });
        // </ExpandRequestSnippet>

        return message;
    }

    private static void makeDeleteRequest(GraphServiceClient graphClient,
        String messageId) {
        if (null == messageId) {
            return;
        }
        // <DeleteRequestSnippet>
        // DELETE https://graph.microsoft.com/v1.0/me/messages/{message-id}
        // messageId is a string containing the id property of the message
        graphClient.me().messages().byMessageId(messageId).delete();
        // </DeleteRequestSnippet>
    }

    private static Calendar makeCreateRequest(GraphServiceClient graphClient) {
        // <CreateRequestSnippet>
        // POST https://graph.microsoft.com/v1.0/me/calendars
        final Calendar calendar = new Calendar();
        calendar.setName("Volunteer");

        final Calendar newCalendar = graphClient.me().calendars().post(calendar);
        // </CreateRequestSnippet>

        return newCalendar;
    }

    private static void makeUpdateRequest(GraphServiceClient graphClient,
        String teamId) {
        if (null == teamId) {
            return;
        }
        // <UpdateRequestSnippet>
        // PATCH https://graph.microsoft.com/v1.0/teams/{team-id}
        final Team team = new Team();
        final TeamFunSettings funSettings = new TeamFunSettings();
        funSettings.setAllowGiphy(true);
        funSettings.setGiphyContentRating(GiphyRatingType.Strict);
        team.setFunSettings(funSettings);

        // teamId is a string containing the id property of the team
        graphClient.teams().byTeamId(teamId).patch(team);
        // </UpdateRequestSnippet>
    }

    private static EventCollectionResponse makeHeadersRequest(
        GraphServiceClient graphClient) {
        // <HeadersRequestSnippet>
        // GET https://graph.microsoft.com/v1.0/me/events
        final EventCollectionResponse events = graphClient.me().events().get( requestConfiguration -> {
            requestConfiguration.headers.add("Prefer", "outlook.timezone=\"Pacific Standard Time\"");
        });
        // </HeadersRequestSnippet>

        return events;
    }

    private static EventCollectionResponse makeQueryParametersRequest(
        GraphServiceClient graphClient) {
        // <QueryParametersRequestSnippet>
        // GET https://graph.microsoft.com/v1.0/me/calendarView?
        // startDateTime=2023-06-14T00:00:00Z&endDateTime=2023-06-15T00:00:00Z
        final EventCollectionResponse events = graphClient.me().calendarView().get( requestConfiguration -> {
            requestConfiguration.queryParameters.startDateTime = "2023-06-14T00:00:00Z";
            requestConfiguration.queryParameters.endDateTime = "2023-06-15T00:00:00Z";
        });
        // </QueryParametersRequestSnippet>

        return events;
    }
}
