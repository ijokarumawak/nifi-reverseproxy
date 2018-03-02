package org.apache.nifi.rproxy.client;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;


public abstract class AbstractS2SClientTest {
    private final ApacheHttpTransport httpTransport = new ApacheHttpTransport.Builder().build();
    protected final JsonFactory jsonFactory = new JacksonFactory();

    protected GenericJson getJson(String url) throws IOException {
        final HttpRequestFactory requestFactory = httpTransport.createRequestFactory(request -> {
            request.setParser(jsonFactory.createJsonObjectParser());
        });
        final HttpRequest httpRequest = requestFactory.buildGetRequest(new GenericUrl(url));
        final ExponentialBackOff backOff = new ExponentialBackOff.Builder()
                .setInitialIntervalMillis(1000)
                .setMaxElapsedTimeMillis(5000)
                .build();
        final HttpBackOffUnsuccessfulResponseHandler responseHandler = new HttpBackOffUnsuccessfulResponseHandler(backOff);
        responseHandler.setBackOffRequired(response -> response.getStatusCode() != 200);
        httpRequest.setUnsuccessfulResponseHandler(responseHandler);
        final HttpResponse httpResponse = httpRequest.execute();

        Assert.assertEquals(200, httpResponse.getStatusCode());

        final GenericJson json = httpResponse.parseAs(GenericJson.class);
        return json;
    }

    protected void postData(int listenHttpPort, Map<String, String> payload) throws IOException {
        postData("nifi0:" + listenHttpPort, payload);
    }

    protected void postData(String targetAddress, Map<String, String> payload) throws IOException {
        final HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
        final HttpRequest httpRequest = requestFactory.buildPostRequest(
                new GenericUrl("http://" + targetAddress + "/contentListener"),
                new JsonHttpContent(jsonFactory, payload));
        final HttpResponse httpResponse = httpRequest.execute();

        Assert.assertEquals(200, httpResponse.getStatusCode());
    }

    @Test
    public void testSendDirect() throws IOException {}
    @Test
    public void testSendProxy() throws IOException {}

    @Test
    public void testReceiveDirect() throws IOException {}
    @Test
    public void testReceiveProxy() throws IOException {}
}
