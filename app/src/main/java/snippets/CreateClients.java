// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.util.Arrays;
import java.util.List;

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
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;

import okhttp3.Request;

public class CreateClients {
    public static GraphServiceClient<Request> createWithAuthorizationCode()
        throws Exception {
        // <AuthorizationCodeSnippet>
        final String clientId = "YOUR_CLIENT_ID";
        final String tenantId = "YOUR_TENANT_ID"; // or "common" for multi-tenant apps
        final String clientSecret = "YOUR_CLIENT_SECRET";
        final String authorizationCode = "AUTH_CODE_FROM_REDIRECT";
        final String redirectUrl = "YOUR_REDIRECT_URI";
        final List<String> scopes = Arrays.asList("User.Read");

        final AuthorizationCodeCredential credential = new AuthorizationCodeCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).clientSecret(clientSecret)
            .authorizationCode(authorizationCode).redirectUrl(redirectUrl).build();

        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
            scopes, credential);

        final GraphServiceClient<Request> graphClient = GraphServiceClient.builder()
            .authenticationProvider(authProvider).buildClient();
        // </AuthorizationCodeSnippet>

        return graphClient;
    }

    public static GraphServiceClient<Request> createWithClientSecret() throws Exception {
        // <ClientSecretSnippet>
        final String clientId = "YOUR_CLIENT_ID";
        final String tenantId = "YOUR_TENANT_ID";
        final String clientSecret = "YOUR_CLIENT_SECRET";

        // The client credentials flow requires that you request the
        // /.default scope, and pre-configure your permissions on the
        // app registration in Azure. An administrator must grant consent
        // to those permissions beforehand.
        final List<String> scopes = Arrays.asList("https://graph.microsoft.com/.default");

        final ClientSecretCredential credential = new ClientSecretCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).clientSecret(clientSecret).build();

        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
            scopes, credential);

        final GraphServiceClient<Request> graphClient = GraphServiceClient.builder()
            .authenticationProvider(authProvider).buildClient();
        // </ClientSecretSnippet>

        return graphClient;
    }

    public static GraphServiceClient<Request> createWithClientCertificate()
        throws Exception {
        // <ClientCertificateSnippet>
        final String clientId = "YOUR_CLIENT_ID";
        final String tenantId = "YOUR_TENANT_ID";
        final String clientCertificatePath = "MyCertificate.pem";

        // The client credentials flow requires that you request the
        // /.default scope, and pre-configure your permissions on the
        // app registration in Azure. An administrator must grant consent
        // to those permissions beforehand.
        final List<String> scopes = Arrays.asList("https://graph.microsoft.com/.default");

        final ClientCertificateCredential credential = new ClientCertificateCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).pemCertificate(clientCertificatePath)
            .build();

        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
            scopes, credential);

        final GraphServiceClient<Request> graphClient = GraphServiceClient.builder()
            .authenticationProvider(authProvider).buildClient();
        // </ClientCertificateSnippet>

        return graphClient;
    }

    public static GraphServiceClient<Request> createWithOnBehalfOf() throws Exception {
        // <OnBehalfOfSnippet>
        final String clientId = "YOUR_CLIENT_ID";
        final String tenantId = "YOUR_TENANT_ID"; // or "common" for multi-tenant apps
        final String clientSecret = "YOUR_CLIENT_SECRET";
        final List<String> scopes = Arrays.asList("https://graph.microsoft.com/.default");

        // This is the incoming token to exchange using on-behalf-of flow
        final String oboToken = "JWT_TOKEN_TO_EXCHANGE";

        final OnBehalfOfCredential credential = new OnBehalfOfCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).clientSecret(clientSecret)
            .userAssertion(oboToken).build();

        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
            scopes, credential);

        final GraphServiceClient<Request> graphClient = GraphServiceClient.builder()
            .authenticationProvider(authProvider).buildClient();
        // </OnBehalfOfSnippet>

        return graphClient;
    }

    public static GraphServiceClient<Request> createWithDeviceCode() throws Exception {
        // <DeviceCodeSnippet>
        final String clientId = "YOUR_CLIENT_ID";
        final String tenantId = "YOUR_TENANT_ID"; // or "common" for multi-tenant apps
        final List<String> scopes = Arrays.asList("User.Read");

        final DeviceCodeCredential credential = new DeviceCodeCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).challengeConsumer(challenge -> {
                // Display challenge to the user
                System.out.println(challenge.getMessage());
            }).build();

        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
            scopes, credential);

        final GraphServiceClient<Request> graphClient = GraphServiceClient.builder()
            .authenticationProvider(authProvider).buildClient();
        // </DeviceCodeSnippet>

        return graphClient;
    }

    public static GraphServiceClient<Request> createWithInteractive() throws Exception {
        // <InteractiveSnippet>
        final String clientId = "YOUR_CLIENT_ID";
        final String tenantId = "YOUR_TENANT_ID"; // or "common" for multi-tenant apps
        final String redirectUrl = "YOUR_REDIRECT_URI";
        final List<String> scopes = Arrays.asList("User.Read");

        final InteractiveBrowserCredential credential = new InteractiveBrowserCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).redirectUrl(redirectUrl).build();

        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
            scopes, credential);

        final GraphServiceClient<Request> graphClient = GraphServiceClient.builder()
            .authenticationProvider(authProvider).buildClient();
        // </InteractiveSnippet>

        return graphClient;
    }

    public static GraphServiceClient<Request> createWithUserNamePassword()
        throws Exception {
        // <UserNamePasswordSnippet>
        final String clientId = "YOUR_CLIENT_ID";
        final String tenantId = "YOUR_TENANT_ID"; // or "common" for multi-tenant apps
        final String userName = "YOUR_USER_NAME";
        final String password = "YOUR_PASSWORD";
        final List<String> scopes = Arrays.asList("User.Read");

        final UsernamePasswordCredential credential = new UsernamePasswordCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).username(userName).password(password)
            .build();

        if (null == scopes || null == credential) {
            throw new Exception("Unexpected error");
        }
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
            scopes, credential);

        final GraphServiceClient<Request> graphClient = GraphServiceClient.builder()
            .authenticationProvider(authProvider).buildClient();
        // </UserNamePasswordSnippet>

        return graphClient;
    }
}
