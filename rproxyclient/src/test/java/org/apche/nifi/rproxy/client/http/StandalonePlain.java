package org.apche.nifi.rproxy.client.http;

import com.google.api.client.json.GenericJson;
import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.protocol.DataPacket;
import org.apache.nifi.remote.protocol.SiteToSiteTransportProtocol;
import org.apche.nifi.rproxy.client.AbstractS2SClientTest;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class StandalonePlain extends AbstractS2SClientTest {

    @Test
    public void testSendDirect() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://nifi0:8080/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .portName("input-http")
                .build();

        final String inputUuid = UUID.randomUUID().toString();
        final Transaction transaction = client.createTransaction(TransferDirection.SEND);
        transaction.send("testSendHTTPDirect".getBytes(), Collections.singletonMap("input.uuid", inputUuid));
        transaction.confirm();
        transaction.complete();

        final GenericJson json = getJson("http://nifi0:8020?input.uuid=" + inputUuid);
        assertEquals("testSendHTTPDirect", json.get("content.0"));
        assertEquals("nifi0", json.get("s2s.host"));
    }

    @Test
    public void testSendProxy() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://nginx.example.com:7070/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .portName("input-http")
                .build();

        final String inputUuid = UUID.randomUUID().toString();
        final Transaction transaction = client.createTransaction(TransferDirection.SEND);
        transaction.send("testSendHTTPProxy".getBytes(), Collections.singletonMap("input.uuid", inputUuid));
        transaction.confirm();
        transaction.complete();

        final GenericJson json = getJson("http://nifi0:8020?input.uuid=" + inputUuid);
        assertEquals("testSendHTTPProxy", json.get("content.0"));
        assertEquals("nginx.example.com", json.get("s2s.host"));
    }

    @Test
    public void testReceiveDirect() throws IOException {

        final String inputUuid = UUID.randomUUID().toString();
        final Map<String, String> payload = new HashMap<>();
        payload.put("input.uuid", inputUuid);
        payload.put("s2s.protocol", "HTTP");
        payload.put("test", "testReceiveHttpDirect");

        postData(8030, payload);

        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://nifi0:8080/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .portName("output-http")
                .build();

        final Transaction transaction = client.createTransaction(TransferDirection.RECEIVE);
        assertEquals("nifi0", transaction.getCommunicant().getHost());
        assertEquals(8080, transaction.getCommunicant().getPort());

        for (DataPacket packet; (packet = transaction.receive()) != null;) {
            final Map received = jsonFactory.createJsonParser(packet.getData()).parse(Map.class);
            assertEquals(payload, received);
        }

        transaction.confirm();
        transaction.complete();
    }

    @Test
    public void testReceiveProxy() throws IOException {

        final String inputUuid = UUID.randomUUID().toString();
        final Map<String, String> payload = new HashMap<>();
        payload.put("input.uuid", inputUuid);
        payload.put("s2s.protocol", "HTTP");
        payload.put("test", "testReceiveHttpProxy");

        postData(8030, payload);

        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://nginx.example.com:7070/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .portName("output-http")
                .build();

        final Transaction transaction = client.createTransaction(TransferDirection.RECEIVE);
        assertEquals("nginx.example.com", transaction.getCommunicant().getHost());
        assertEquals(7070, transaction.getCommunicant().getPort());

        for (DataPacket packet; (packet = transaction.receive()) != null;) {
            final Map received = jsonFactory.createJsonParser(packet.getData()).parse(Map.class);
            assertEquals(payload, received);
        }

        transaction.confirm();
        transaction.complete();
    }
}
