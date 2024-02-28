// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import com.azure.identity.AzureAuthorityHosts;
import com.azure.identity.InteractiveBrowserCredential;
import com.azure.identity.InteractiveBrowserCredentialBuilder;
import com.microsoft.graph.serviceclient.GraphServiceClient;

public class NationalClouds {
    public static GraphServiceClient createClientForUSGov() throws Exception {
        // <NationalCloudSnippet>
        // Create the InteractiveBrowserCredential using details
        // from app registered in the Azure AD for US Government portal
        final InteractiveBrowserCredential credential = new InteractiveBrowserCredentialBuilder()
            .clientId("YOUR_CLIENT_ID").tenantId("YOUR_TENANT_ID")
            // https://login.microsoftonline.us
            .authorityHost(AzureAuthorityHosts.AZURE_GOVERNMENT)
            .redirectUrl("YOUR_REDIRECT_URI").build();

        final String[] scopes = new String[] {"https://graph.microsoft.us/.default"};

        // Create the authentication provider
        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }

        final GraphServiceClient graphClient = new GraphServiceClient(credential, scopes);
        // Set the service root to the
        // Microsoft Graph for US Government L4 endpoint
        // NOTE: The API version must be included in the URL
        graphClient.getRequestAdapter().setBaseUrl("https://graph.microsoft.us/v1.0");
        // </NationalCloudSnippet>

        return graphClient;
    }
}
