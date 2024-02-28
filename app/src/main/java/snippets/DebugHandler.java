// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class DebugHandler implements Interceptor {

    @Override
    public Response intercept(final Chain chain) throws IOException {
        Request request = chain.request();
        System.out.println("BEGIN REQUEST");
        System.out.println(String.format("%s %s", request.method(), request.url()));

        final Headers headers = request.headers();
        for (int i = 0; i < headers.size(); i++) {
            logHeader(headers, i);
        }

        final RequestBody requestBody = request.body();
        if (requestBody != null) {
            final MediaType contentType = requestBody.contentType();
            if (contentType != null
                && contentType.subtype().compareToIgnoreCase("json") == 0) {
                final Charset charset = contentType.charset(Charset.forName("UTF-8"));
                final Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                System.out.println(buffer.readString(charset));
            } else {
                System.out.println("Non-JSON content");
                System.out.println(String.format("Content-Length: %d", requestBody.contentLength()));
                // if (contentType != null && contentType.subtype().compareToIgnoreCase("octet-stream") == 0) {
                //     final Buffer buffer = new Buffer();
                //     requestBody.writeTo(buffer);
                //     final ByteString byteString = buffer.readByteString();
                //     System.out.println(byteString.hex());
                // }
            }
        }
        System.out.println("END REQUEST");

        final Response response = chain.proceed(request);

        System.out.println("BEGIN RESPONSE");
        System.out.println(String.format("Status: %d", response.code()));
        final ResponseBody responseBody = response.peekBody(Long.MAX_VALUE);
        if (responseBody != null) {
            System.out.println(responseBody.string());
        }
        System.out.println("END RESPONSE");

        return response;
    }

    private void logHeader(Headers headers, int index) {
        final String value = headers.name(index).compareToIgnoreCase("Authorization") == 0
            ? "***"
            : headers.value(index);
        System.out.println(String.format("%s: %s", headers.name(index), value));
    }
}
