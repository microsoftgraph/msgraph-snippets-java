// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.DeviceCodeInfo;
import com.microsoft.graph.core.authentication.AzureIdentityAuthenticationProvider;
import com.microsoft.graph.core.requests.GraphClientFactory;
import com.microsoft.graph.serviceclient.GraphServiceClient;

import okhttp3.OkHttpClient;

public class GraphHelper {
    public static GraphServiceClient getGraphClientForUser(Properties properties,
        Consumer<DeviceCodeInfo> challenge) throws Exception {

        // Get required properties
        final String clientId = properties.getProperty("app.clientId");
        final String tenantId = properties.getProperty("app.tenantId");
        final List<String> graphUserScopes = Arrays
            .asList(properties.getProperty("app.graphUserScopes").split(","));

        if (null == clientId || clientId.isBlank() || null == tenantId
            || tenantId.isBlank() || null == graphUserScopes
            || graphUserScopes.isEmpty()) {
            throw new Exception("Missing required configuration. See README.");
        }

        final DeviceCodeCredential credential = new DeviceCodeCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).challengeConsumer(challenge).build();

        if (null == credential) {
            throw new Exception("Could not create required credential.");
        }
        return new GraphServiceClient(credential, graphUserScopes.toArray(new String[0]));
    }

    public static GraphServiceClient getDebugGraphClientForUser(Properties properties,
        Consumer<DeviceCodeInfo> challenge) throws Exception {

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

        final String[] allowedHosts = { "graph.microsoft.com" };
        final AzureIdentityAuthenticationProvider authProvider = new AzureIdentityAuthenticationProvider(
            credential, allowedHosts, graphUserScopes.toArray(new String[0]));

        final DebugHandler debugHandler = new DebugHandler();

        final OkHttpClient httpClient = GraphClientFactory.create()
            .addInterceptor(debugHandler).build();

        return new GraphServiceClient(authProvider, httpClient);
    }
}
