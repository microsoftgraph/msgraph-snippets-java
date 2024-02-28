// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.microsoft.graph.core.tasks.PageIterator;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.MessageCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;

public class Paging {
    public static void runAllSamples(GraphServiceClient graphClient) throws ReflectiveOperationException {
        iterateAllMessages(graphClient);
        resumePaging(graphClient);
        manuallyIterateAllPages(graphClient);
    }


    public static void iterateAllMessages(GraphServiceClient graphClient) throws ReflectiveOperationException {
        // <PagingSnippet>
        ArrayList<Message> messages = new ArrayList<>();

        MessageCollectionResponse messageResponse = graphClient.me().messages().get( requestConfiguration -> {
            requestConfiguration.headers.add("Prefer", "outlook.body-content-type=\"text\"");
            requestConfiguration.queryParameters.select = new String[] {"sender, subject, body"};
            requestConfiguration.queryParameters.top = 10;
        });

        PageIterator<Message, MessageCollectionResponse> pageIterator =
                new PageIterator.Builder<Message, MessageCollectionResponse>()
                .client(graphClient)
                // Response from the first request
                .collectionPage(Objects.requireNonNull(messageResponse))
                // Factory to create a new collection response
                .collectionPageFactory(MessageCollectionResponse::createFromDiscriminatorValue)
                // Used to configure subsequent requests
                .requestConfigurator( requestInfo -> {
                    // Re-add the header and query parameters to subsequent requests
                    requestInfo.headers.add("Prefer", "outlook.body-content-type=\"text\"");
                    requestInfo.addQueryParameter("%24select", new String[] {"sender, subject, body"});
                    requestInfo.addQueryParameter("%24top", 10);
                    return requestInfo;
                })
                // Callback executed for each item in the collection
                .processPageItemCallback( message -> {
                    messages.add(message);
                    return true;
                }).build();

        pageIterator.iterate();
        // </PagingSnippet>
    }

    private static void resumePaging(GraphServiceClient graphClient) throws ReflectiveOperationException {
        // <ResumePagingSnippet>
        int iterations = 1;
        ArrayList<Message> messages = new ArrayList<>();
        int pauseAfter = iterations*25;

        MessageCollectionResponse messageResponse = graphClient.me().messages().get( requestConfiguration -> {
            requestConfiguration.queryParameters.top = 10;
            requestConfiguration.queryParameters.select = new String[] {"sender, subject"};
        });

        PageIterator<Message, MessageCollectionResponse> pageIterator =
                new PageIterator.Builder<Message, MessageCollectionResponse>()
                .client(graphClient)
                .collectionPage(Objects.requireNonNull(messageResponse))
                .collectionPageFactory(MessageCollectionResponse::createFromDiscriminatorValue)
                .requestConfigurator( requestInfo -> {
                    requestInfo.addQueryParameter("%24select", new String[] {"sender, subject"});
                    requestInfo.addQueryParameter("%24top", 10);
                    return requestInfo;
                })
                .processPageItemCallback( message -> {
                    messages.add(message);
                    // Pause paging by returning false after 25 messages
                    return messages.size() < pauseAfter;
                }).build();

        pageIterator.iterate();

        // Resume paging
        while (pageIterator.getPageIteratorState() != PageIterator.PageIteratorState.COMPLETE) {
            iterations+=1;
            pageIterator.resume();
        }
        // </ResumePagingSnippet>
    }

    private static void manuallyIterateAllPages(GraphServiceClient graphClient) {
        // <ManualPagingSnippet>
        MessageCollectionResponse messagesPage = graphClient.me().messages().get( requestConfiguration -> {
            requestConfiguration.headers.add("Prefer", "outlook.body-content-type=\"text\"");
            requestConfiguration.queryParameters.select = new String[] {"sender, subject, body"};
            requestConfiguration.queryParameters.top = 10;
        });

        while (messagesPage != null) {
            final List<Message> messages = messagesPage.getValue();
            for (Message message : messages) {
                System.out.println(message.getSubject());
            }

            // Get the next page
            final String odataNextLink = messagesPage.getOdataNextLink();
            if (odataNextLink == null || odataNextLink.isEmpty()) {
                break;
            } else {
                messagesPage = graphClient.me().messages().withUrl(odataNextLink).get();
            }
        }
        // </ManualPagingSnippet>
    }
}
