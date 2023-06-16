// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.util.List;

import com.microsoft.graph.models.Message;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;
import com.microsoft.graph.requests.MessageCollectionRequestBuilder;

import okhttp3.Request;

public class Paging {
    public static void runAllSamples(GraphServiceClient<Request> graphClient) {
        iterateAllMessages(graphClient);
    }

    private static void iterateAllMessages(GraphServiceClient<Request> graphClient) {
        // <PagingSnippet>
        MessageCollectionPage messagesPage = graphClient.me().messages()
            .buildRequest(
                new HeaderOption("Prefer", "outlook.body-content-type=\"text\""))
            .select("sender,subject,body").top(10).get();

        while (messagesPage != null) {
            final List<Message> messages = messagesPage.getCurrentPage();
            for (Message message : messages) {
                System.out.println(message.subject);
            }

            // Get the next page
            final MessageCollectionRequestBuilder nextPage = messagesPage.getNextPage();
            if (nextPage == null) {
                break;
            } else {
                messagesPage = nextPage.buildRequest(
                    // Re-add the header to subsequent requests
                    new HeaderOption("Prefer", "outlook.body-content-type=\"text\""))
                    .get();
            }
        }
        // </PagingSnippet>
    }
}
