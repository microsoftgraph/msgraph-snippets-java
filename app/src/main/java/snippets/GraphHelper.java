// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.DeviceCodeInfo;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;

import okhttp3.Request;

public class GraphHelper {
    public static GraphServiceClient<Request> getGraphClientForUser(@Nonnull Properties properties,
        @Nonnull Consumer<DeviceCodeInfo> challenge) throws Exception {

        // Get required properties
        final String clientId = properties.getProperty("app.clientId");
        final String tenantId = properties.getProperty("app.tenantId");
        final List<String> graphUserScopes = Arrays
            .asList(properties.getProperty("app.graphUserScopes").split(","));

        if (null == clientId || clientId.isBlank() || null == tenantId || tenantId.isBlank()
            || null == graphUserScopes || graphUserScopes.isEmpty()) {
            throw new Exception("Missing required configuration. See README.");
        }

        final DeviceCodeCredential credential = new DeviceCodeCredentialBuilder().clientId(clientId)
            .tenantId(tenantId).challengeConsumer(challenge).build();

        if (null == credential) {
            throw new Exception("Could not create required credential.");
        }

        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
            graphUserScopes, credential);

        return GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
    }
}
