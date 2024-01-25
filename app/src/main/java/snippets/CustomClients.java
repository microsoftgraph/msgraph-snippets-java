// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.ProxyOptions;
import com.azure.core.http.ProxyOptions.Type;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.core.authentication.AzureIdentityAuthenticationProvider;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.kiota.http.KiotaClientFactory;
import com.microsoft.kiota.http.middleware.ChaosHandler;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class CustomClients {
    public static GraphServiceClient createWithChaosHandler(
        TokenCredential credential, String[] allowedHosts, String[] scopes) throws Exception {
        if (null == credential || scopes == null) {
            throw new Exception("Parameters are not optional");
        }
        // <ChaosHandlerSnippet>
        // tokenCredential is one of the credential classes from azure-identity
        // scopes is a list of permission scope strings
        final AzureIdentityAuthenticationProvider authProvider = new AzureIdentityAuthenticationProvider(
            credential, allowedHosts, scopes);

        final ChaosHandler chaosHandler = new ChaosHandler();

        final OkHttpClient httpClient = KiotaClientFactory.create().addInterceptor(chaosHandler).build();

        if (null == httpClient) {
            throw new Exception("Could not create HTTP client.");
        }

        final GraphServiceClient graphClient = new GraphServiceClient(authProvider, httpClient);
        // </ChaosHandlerSnippet>

        return graphClient;
    }

    public static GraphServiceClient createWithProxy(String[] allowedHosts, String[] scopes) throws Exception {
        if (scopes == null) {
            throw new Exception("Parameters are not optional");
        }
        // <ProxySnippet>
        final String proxyHost = "localhost";
        final int proxyPort = 8888;
        final InetSocketAddress proxyAddress = new InetSocketAddress(proxyHost,
            proxyPort);

        // Setup proxy for the token credential from azure-identity
        // From the com.azure.core.http.* packages
        final ProxyOptions options = new ProxyOptions(Type.HTTP, proxyAddress);
        // If the proxy requires authentication, use setCredentials
        options.setCredentials("username", "password");
        final HttpClient authClient = new NettyAsyncHttpClientBuilder().proxy(options)
            .build();

        final ClientSecretCredential credential = new ClientSecretCredentialBuilder()
            .clientId("YOUR_CLIENT_ID")
            .tenantId("YOUR_TENANT_ID")
            .clientSecret("YOUR_CLIENT_SECRET")
            .httpClient(authClient)
            .build();

        if (credential == null) {
            throw new Exception("Could not create credential");
        }

        // scopes is a list of permission scope strings
        final AzureIdentityAuthenticationProvider authProvider = new AzureIdentityAuthenticationProvider(
            credential, allowedHosts, scopes);

        // Setup proxy for the Graph client
        final Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);

        // This object is only needed if the proxy requires authentication
        final Authenticator proxyAuthenticator = new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response)
                throws IOException {
                String credential = Credentials.basic("username", "password");
                return response.request().newBuilder()
                    .header("Proxy-Authorization", credential).build();
            }
        };

        // Omit proxyAuthenticator if no authentication required
        final OkHttpClient httpClient = KiotaClientFactory.create().proxy(proxy)
            .proxyAuthenticator(proxyAuthenticator).build();
        if (null == httpClient) {
            throw new Exception("Could not create HTTP client.");
        }

        final GraphServiceClient graphClient = new GraphServiceClient(authProvider, httpClient);
        // </ProxySnippet>

        return graphClient;
    }
}
