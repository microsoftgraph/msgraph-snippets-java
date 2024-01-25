// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import com.azure.identity.AuthorizationCodeCredential;
import com.azure.identity.AuthorizationCodeCredentialBuilder;
import com.azure.identity.ClientCertificateCredential;
import com.azure.identity.ClientCertificateCredentialBuilder;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.InteractiveBrowserCredential;
import com.azure.identity.InteractiveBrowserCredentialBuilder;
import com.azure.identity.OnBehalfOfCredential;
import com.azure.identity.OnBehalfOfCredentialBuilder;
import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import com.microsoft.graph.serviceclient.GraphServiceClient;

public class CreateClients {
    public static GraphServiceClient createWithAuthorizationCode()
        throws Exception {
        // <AuthorizationCodeSnippet>
        final String clientId = "YOUR_CLIENT_ID";
        final String tenantId = "YOUR_TENANT_ID"; // or "common" for multi-tenant apps
        final String clientSecret = "YOUR_CLIENT_SECRET";
        final String authorizationCode = "AUTH_CODE_FROM_REDIRECT";
        final String redirectUrl = "YOUR_REDIRECT_URI";
        final String[] scopes = new String[] { "User.Read" };

        final AuthorizationCodeCredential credential = new AuthorizationCodeCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).clientSecret(clientSecret)
            .authorizationCode(authorizationCode).redirectUrl(redirectUrl).build();

        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }

        final GraphServiceClient graphClient = new GraphServiceClient(credential, scopes);
        // </AuthorizationCodeSnippet>

        return graphClient;
    }

    public static GraphServiceClient createWithClientSecret() throws Exception {
        // <ClientSecretSnippet>
        final String clientId = "YOUR_CLIENT_ID";
        final String tenantId = "YOUR_TENANT_ID";
        final String clientSecret = "YOUR_CLIENT_SECRET";

        // The client credentials flow requires that you request the
        // /.default scope, and pre-configure your permissions on the
        // app registration in Azure. An administrator must grant consent
        // to those permissions beforehand.
        final String[] scopes = new String[] { "https://graph.microsoft.com/.default" };

        final ClientSecretCredential credential = new ClientSecretCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).clientSecret(clientSecret).build();

        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }

        final GraphServiceClient graphClient = new GraphServiceClient(credential, scopes);
        // </ClientSecretSnippet>

        return graphClient;
    }

    public static GraphServiceClient createWithClientCertificate()
        throws Exception {
        // <ClientCertificateSnippet>
        final String clientId = "YOUR_CLIENT_ID";
        final String tenantId = "YOUR_TENANT_ID";
        final String clientCertificatePath = "MyCertificate.pem";

        // The client credentials flow requires that you request the
        // /.default scope, and pre-configure your permissions on the
        // app registration in Azure. An administrator must grant consent
        // to those permissions beforehand.
        final String[] scopes = new String[] {"https://graph.microsoft.com/.default"};

        final ClientCertificateCredential credential = new ClientCertificateCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).pemCertificate(clientCertificatePath)
            .build();

        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }

        final GraphServiceClient graphClient = new GraphServiceClient(credential, scopes);
        // </ClientCertificateSnippet>

        return graphClient;
    }

    public static GraphServiceClient createWithOnBehalfOf() throws Exception {
        // <OnBehalfOfSnippet>
        final String clientId = "YOUR_CLIENT_ID";
        final String tenantId = "YOUR_TENANT_ID"; // or "common" for multi-tenant apps
        final String clientSecret = "YOUR_CLIENT_SECRET";
        final String[] scopes = new String[] {"https://graph.microsoft.com/.default"};

        // This is the incoming token to exchange using on-behalf-of flow
        final String oboToken = "JWT_TOKEN_TO_EXCHANGE";

        final OnBehalfOfCredential credential = new OnBehalfOfCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).clientSecret(clientSecret)
            .userAssertion(oboToken).build();

        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }

        final GraphServiceClient graphClient = new GraphServiceClient(credential, scopes);
        // </OnBehalfOfSnippet>

        return graphClient;
    }

    public static GraphServiceClient createWithDeviceCode() throws Exception {
        // <DeviceCodeSnippet>
        final String clientId = "YOUR_CLIENT_ID";
        final String tenantId = "YOUR_TENANT_ID"; // or "common" for multi-tenant apps
        final String[] scopes = new String[] {"User.Read"};

        final DeviceCodeCredential credential = new DeviceCodeCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).challengeConsumer(challenge -> {
                // Display challenge to the user
                System.out.println(challenge.getMessage());
            }).build();

        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }

        final GraphServiceClient graphClient = new GraphServiceClient(credential, scopes);
        // </DeviceCodeSnippet>

        return graphClient;
    }

    public static GraphServiceClient createWithInteractive() throws Exception {
        // <InteractiveSnippet>
        final String clientId = "YOUR_CLIENT_ID";
        final String tenantId = "YOUR_TENANT_ID"; // or "common" for multi-tenant apps
        final String redirectUrl = "YOUR_REDIRECT_URI";
        final String[] scopes = new String[] {"User.Read"};

        final InteractiveBrowserCredential credential = new InteractiveBrowserCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).redirectUrl(redirectUrl).build();

        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }

        final GraphServiceClient graphClient = new GraphServiceClient(credential, scopes);
        // </InteractiveSnippet>

        return graphClient;
    }

    public static GraphServiceClient createWithUserNamePassword()
        throws Exception {
        // <UserNamePasswordSnippet>
        final String clientId = "YOUR_CLIENT_ID";
        final String tenantId = "YOUR_TENANT_ID"; // or "common" for multi-tenant apps
        final String userName = "YOUR_USER_NAME";
        final String password = "YOUR_PASSWORD";
        final String[] scopes = new String[] {"User.Read"};

        final UsernamePasswordCredential credential = new UsernamePasswordCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).username(userName).password(password)
            .build();

        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }

        final GraphServiceClient graphClient = new GraphServiceClient(credential, scopes);
        // </UserNamePasswordSnippet>

        return graphClient;
    }
}
