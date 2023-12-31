// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.util.Arrays;
import java.util.List;

import com.azure.identity.AzureAuthorityHosts;
import com.azure.identity.InteractiveBrowserCredential;
import com.azure.identity.InteractiveBrowserCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;

import okhttp3.Request;

public class NationalClouds {
    public static GraphServiceClient<Request> createClientForUSGov() throws Exception {
        // <NationalCloudSnippet>
        // Create the InteractiveBrowserCredential using details
        // from app registered in the Azure AD for US Government portal
        final InteractiveBrowserCredential credential = new InteractiveBrowserCredentialBuilder()
            .clientId("YOUR_CLIENT_ID").tenantId("YOUR_TENANT_ID")
            // https://login.microsoftonline.us
            .authorityHost(AzureAuthorityHosts.AZURE_GOVERNMENT)
            .redirectUrl("YOUR_REDIRECT_URI").build();

        final List<String> scopes = Arrays.asList("https://graph.microsoft.us/.default");

        // Create the authentication provider
        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
            scopes, credential);

        final GraphServiceClient<Request> graphClient = GraphServiceClient.builder()
            .authenticationProvider(authProvider).buildClient();

        // Set the service root to the
        // Microsoft Graph for US Government L4 endpoint
        // NOTE: The API version must be included in the URL
        graphClient.setServiceRoot("https://graph.microsoft.us/v1.0");
        // </NationalCloudSnippet>

        return graphClient;
    }
}
