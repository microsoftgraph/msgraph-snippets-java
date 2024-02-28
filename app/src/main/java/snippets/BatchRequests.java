// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.microsoft.graph.core.content.BatchResponseContent;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.EventCollectionResponse;
import com.microsoft.graph.models.User;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.core.content.BatchRequestContent;

import com.microsoft.kiota.RequestInformation;

public class BatchRequests {
    public static void runSamples(GraphServiceClient graphClient) throws IOException {
        simpleBatch(graphClient);
        dependentBatch(graphClient);
    }

    private static void simpleBatch(GraphServiceClient graphClient) throws IOException {
        // <SimpleBatchSnippet>
        // Create the batch request content with the steps
        final BatchRequestContent batchRequestContent = new BatchRequestContent(
            graphClient);

        // Use the Graph client to generate the requestInformation object for GET /me
        final RequestInformation meRequestInformation = graphClient.me()
            .toGetRequestInformation();

        final ZoneOffset localTimeZone = OffsetDateTime.now().getOffset();
        final OffsetDateTime today = OffsetDateTime.of(LocalDate.now(),
            LocalTime.MIDNIGHT, localTimeZone);
        final OffsetDateTime tomorrow = today.plusDays(1);

        // Use the Graph client to generate the requestInformation for
        // GET /me/calendarView?startDateTime="start"&endDateTime="end"
        RequestInformation calenderViewRequestInformation = graphClient.me()
            .calendarView().toGetRequestInformation(requestConfiguration -> {
                requestConfiguration.queryParameters.startDateTime = today.toString();
                requestConfiguration.queryParameters.endDateTime = tomorrow.toString();
            });

        // Add the requestInformation objects to the batch request content
        final String meRequestId = batchRequestContent
            .addBatchRequestStep(meRequestInformation);
        final String calendarViewRequestStepId = batchRequestContent
            .addBatchRequestStep(calenderViewRequestInformation);

        // Send the batch request content to the /$batch endpoint
        final BatchResponseContent batchResponseContent = Objects.requireNonNull(
            graphClient.getBatchRequestBuilder().post(batchRequestContent, null));

        // Get the user response using the id assigned to the request
        final User me = batchResponseContent.getResponseById(meRequestId,
            User::createFromDiscriminatorValue);
        System.out.println(String.format("Hello %s!", me.getDisplayName()));

        // Get the calendar view response by id
        final EventCollectionResponse eventsResponse = Objects.requireNonNull(
            batchResponseContent.getResponseById(calendarViewRequestStepId,
                EventCollectionResponse::createFromDiscriminatorValue));
        System.out.println(String.format("You have %d events on your calendar today",
            Objects.requireNonNull(eventsResponse.getValue()).size()));
        // </SimpleBatchSnippet>
    }

    private static void dependentBatch(GraphServiceClient graphClient)
        throws IOException {
        // <DependentBatchSnippet>
        // Create the batch request content with the steps
        final BatchRequestContent batchRequestContent = new BatchRequestContent(
            graphClient);

        final ZoneOffset localTimeZone = OffsetDateTime.now().getOffset();
        final OffsetDateTime today = OffsetDateTime.of(LocalDate.now(),
            LocalTime.MIDNIGHT, localTimeZone);
        final OffsetDateTime tomorrow = today.plusDays(1);

        // Create a new event for today at 5:00 PM
        final Event newEvent = new Event();
        newEvent.setSubject("File end-of-day report");
        // 5:00 PM
        final DateTimeTimeZone start = new DateTimeTimeZone();
        start.setDateTime(
            today.plusHours(17).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        start.setTimeZone(ZoneOffset.systemDefault().getId());
        newEvent.setStart(start);
        // 5:30 PM
        final DateTimeTimeZone end = new DateTimeTimeZone();
        end.setDateTime(today.plusHours(17).plusMinutes(30)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        end.setTimeZone(ZoneOffset.systemDefault().getId());
        newEvent.setEnd(end);

        // Use the Graph client to add the requestInformation for POST /me/events
        RequestInformation postEventRequestInformation = graphClient.me().events()
            .toPostRequestInformation(newEvent);

        // Get the id assigned to the request
        String postEventRequestId = batchRequestContent
            .addBatchRequestStep(postEventRequestInformation);

        // Use the Graph client to generate the requestInformation
        // GET /me/calendarView?startDateTime="start"&endDateTime="end"
        final RequestInformation calendarViewRequestInformation = graphClient.me()
            .calendarView().toGetRequestInformation(requestConfiguration -> {
                requestConfiguration.queryParameters.startDateTime = today.toString();
                requestConfiguration.queryParameters.endDateTime = tomorrow.toString();
            });

        final String calendarViewRequestId = batchRequestContent
            .addBatchRequestStep(calendarViewRequestInformation);
        // Set the dependsOnId to 'postEventRequestId'
        batchRequestContent.getBatchRequestSteps().get(calendarViewRequestId)
            .addDependsOnId(postEventRequestId);

        // Send the batch request content to the /$batch endpoint
        final BatchResponseContent batchResponseContent = Objects.requireNonNull(
            graphClient.getBatchRequestBuilder().post(batchRequestContent, null));

        // Get the event response using the id assigned to the request
        final Event postedEvent = batchResponseContent.getResponseById(postEventRequestId,
            Event::createFromDiscriminatorValue);
        System.out.println(String.format("New event created with ID: %s",
            Objects.requireNonNull(postedEvent.getId())));

        // Get the calendar view response by id
        final EventCollectionResponse eventsResponse = Objects
            .requireNonNull(batchResponseContent.getResponseById(calendarViewRequestId,
                EventCollectionResponse::createFromDiscriminatorValue));
        System.out.println(String.format("You have %d events on your calendar today",
            Objects.requireNonNull(eventsResponse.getValue().size())));
        // </DependentBatchSnippet>
    }
}
