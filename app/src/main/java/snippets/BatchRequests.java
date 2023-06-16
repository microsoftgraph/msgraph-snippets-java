// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.gson.JsonElement;
import com.microsoft.graph.content.BatchRequestContent;
import com.microsoft.graph.content.BatchResponseContent;
import com.microsoft.graph.content.BatchResponseStep;
import com.microsoft.graph.http.HttpMethod;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.User;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.EventCollectionResponse;
import com.microsoft.graph.requests.GraphServiceClient;

import okhttp3.Request;

public class BatchRequests {
    public static void runSamples(GraphServiceClient<Request> graphClient) {
        simpleBatch(graphClient);
        dependentBatch(graphClient);
    }

    private static void simpleBatch(GraphServiceClient<Request> graphClient) {
        // <SimpleBatchSnippet>
        // Create the batch request content with the steps
        final BatchRequestContent batchRequestContent = new BatchRequestContent();

        // Use the Graph client to generate the request for GET /me
        final String meGetId = batchRequestContent
            .addBatchRequestStep(graphClient.me().buildRequest());

        final ZoneOffset localTimeZone = OffsetDateTime.now().getOffset();
        final OffsetDateTime today = OffsetDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT,
            localTimeZone);
        final OffsetDateTime tomorrow = today.plusDays(1);

        // Use the Graph client to generate the request URL for
        // GET /me/calendarView?startDateTime="start"&endDateTime="end"
        final List<Option> calendarViewOptions = Arrays.asList(
            new QueryOption("startDateTime", today.toString()),
            new QueryOption("endDateTime", tomorrow.toString()));
        final String calendarViewRequestStepId = batchRequestContent
            .addBatchRequestStep(graphClient.me().calendarView().buildRequest(calendarViewOptions));

        // Send the batch request content to the /$batch endpoint
        final BatchResponseContent batchResponseContent = Objects
            .requireNonNull(graphClient.batch().buildRequest().post(batchRequestContent));

        // Get the user response using the id assigned to the request
        final BatchResponseStep<JsonElement> userResponse = Objects
            .requireNonNull(batchResponseContent.getResponseById(meGetId));
        final User user = Objects.requireNonNull(userResponse.getDeserializedBody(User.class));
        System.out.println(String.format("Hello %s!", user.displayName));

        // Get the calendar view response by id
        final BatchResponseStep<JsonElement> eventsResponse = Objects
            .requireNonNull(batchResponseContent.getResponseById(calendarViewRequestStepId));
        final EventCollectionResponse events = Objects
            .requireNonNull(eventsResponse.getDeserializedBody(EventCollectionResponse.class));
        System.out.println(String.format("You have %d events on your calendar today",
            Objects.requireNonNull(events.value).size()));
        // </SimpleBatchSnippet>
    }

    private static void dependentBatch(GraphServiceClient<Request> graphClient) {
        // <DependentBatchSnippet>
        // Create the batch request content with the steps
        final BatchRequestContent batchRequestContent = new BatchRequestContent();

        final ZoneOffset localTimeZone = OffsetDateTime.now().getOffset();
        final OffsetDateTime today = OffsetDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT,
            localTimeZone);
        final OffsetDateTime tomorrow = today.plusDays(1);

        // Create a new event for today at 5:00 PM
        final Event newEvent = new Event();
        newEvent.subject = "File end-of-day report";
        newEvent.start = new DateTimeTimeZone();
        // 5:00 PM
        final DateTimeTimeZone start = new DateTimeTimeZone();
        start.dateTime = today.plusHours(17).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        start.timeZone = ZoneOffset.systemDefault().getId();
        newEvent.start = start;
        newEvent.end = new DateTimeTimeZone();
        // 5:30 PM
        final DateTimeTimeZone end = new DateTimeTimeZone();
        end.dateTime = today.plusHours(17).plusMinutes(30)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        end.timeZone = ZoneOffset.systemDefault().getId();
        newEvent.end = end;

        // Use the Graph client to generate the request URL for POST /me/events
        final String addEventRequestId = batchRequestContent.addBatchRequestStep(
            graphClient.me().events().buildRequest(), HttpMethod.POST, newEvent);

        // Use the Graph client to generate the request URL for
        // GET /me/calendarView?startDateTime="start"&endDateTime="end"
        final List<Option> calendarViewOptions = Arrays.asList(
            new QueryOption("startDateTime", today.toString()),
            new QueryOption("endDateTime", tomorrow.toString()));

        // Add the second request, passing addEventRequestId in the
        // 'dependsOnRequestsIds'
        final String calendarViewRequestStepId = batchRequestContent.addBatchRequestStep(
            graphClient.me().calendarView().buildRequest(calendarViewOptions), HttpMethod.GET, null,
            addEventRequestId);

        // Send the batch request content to the /$batch endpoint
        final BatchResponseContent batchResponseContent = Objects
            .requireNonNull(graphClient.batch().buildRequest().post(batchRequestContent));

        // Get the user response using the id assigned to the request
        final BatchResponseStep<JsonElement> addEventResponse = Objects
            .requireNonNull(batchResponseContent.getResponseById(addEventRequestId));
        final Event event = Objects.requireNonNull(addEventResponse)
            .getDeserializedBody(Event.class);
        System.out.println(
            String.format("New event created with ID: %s", Objects.requireNonNull(event).id));

        // Get the calendar view response by id
        final BatchResponseStep<JsonElement> eventsResponse = Objects
            .requireNonNull(batchResponseContent.getResponseById(calendarViewRequestStepId));
        final EventCollectionResponse events = Objects
            .requireNonNull(eventsResponse.getDeserializedBody(EventCollectionResponse.class));
        System.out.println(String.format("You have %d events on your calendar today",
            Objects.requireNonNull(events.value).size()));
        // </DependentBatchSnippet>
    }
}
