package org.apche.nifi.rproxy.client.http;

import com.google.api.client.json.GenericJson;
import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.protocol.SiteToSiteTransportProtocol;
import org.apche.nifi.rproxy.client.AbstractS2SClientTest;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ClusterPlain extends AbstractS2SClientTest {

    @Test
    public void testSendDirect() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://localhost:18080/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .portName("input-http")
                .build();

        final String inputUuid = UUID.randomUUID().toString();
        final Transaction transaction = client.createTransaction(TransferDirection.SEND);
        transaction.send("testSendHTTPDirect".getBytes(), Collections.singletonMap("input.uuid", inputUuid));
        transaction.confirm();
        transaction.complete();

        final GenericJson json = getJson("http://localhost:8022?input.uuid=" + inputUuid);
        assertEquals("testSendHTTPDirect", json.get("content.0"));
        assertEquals("localhost", json.get("s2s.host"));
    }

    @Test
    public void testSendProxy() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://nginx.example.com:18060/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .portName("input-http")
                .requestBatchCount(1)
                .build();

        final String inputUuid = UUID.randomUUID().toString();
        final Transaction transaction = client.createTransaction(TransferDirection.SEND);
        transaction.send("testSendHTTPProxy".getBytes(), Collections.singletonMap("input.uuid", inputUuid));
        transaction.confirm();
        transaction.complete();

        final GenericJson json = getJson("http://localhost:8022?input.uuid=" + inputUuid);
        assertEquals("testSendHTTPProxy", json.get("content.0"));
        assertEquals("nginx.example.com", json.get("s2s.host"));
    }

    @Override
    public void testReceiveDirect() throws IOException {

    }

    @Override
    public void testReceiveProxy() throws IOException {

    }
}
