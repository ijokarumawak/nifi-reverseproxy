package org.apche.nifi.rproxy.client.raw;

import com.google.api.client.json.GenericJson;
import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.KeystoreType;
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
                .transportProtocol(SiteToSiteTransportProtocol.RAW)
                .portName("input-raw")
                .build();

        final String inputUuid = UUID.randomUUID().toString();
        final Transaction transaction = client.createTransaction(TransferDirection.SEND);
        transaction.send("testSendRawDirect".getBytes(), Collections.singletonMap("input.uuid", inputUuid));
        transaction.confirm();
        transaction.complete();

        final GenericJson json = getJson("http://nifi0:8020?input.uuid=" + inputUuid);
        assertEquals("testSendRawDirect", json.get("content.0"));
        assertEquals("nifi0", json.get("s2s.host"));
    }

    @Test
    public void testSendProxy() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://nginx.example.com:7080/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.RAW)
                .portName("input-raw")
                .build();

        final String inputUuid = UUID.randomUUID().toString();
        final Transaction transaction = client.createTransaction(TransferDirection.SEND);
        transaction.send("testSendRawProxy".getBytes(), Collections.singletonMap("input.uuid", inputUuid));
        transaction.confirm();
        transaction.complete();

        final GenericJson json = getJson("http://nifi0:8020?input.uuid=" + inputUuid);
        assertEquals("testSendRawProxy", json.get("content.0"));
        assertEquals("nginx.example.com", json.get("s2s.host"));
    }

    @Test
    public void testReceiveDirect() throws IOException {

        final String inputUuid = UUID.randomUUID().toString();
        final Map<String, String> payload = new HashMap<>();
        payload.put("input.uuid", inputUuid);
        payload.put("s2s.protocol", "RAW");
        payload.put("test", "testReceiveRawDirect");

        postData(8030, payload);

        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://nifi0:8080/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.RAW)
                .portName("output-raw")
                .build();

        final Transaction transaction = client.createTransaction(TransferDirection.RECEIVE);
        assertEquals("nifi0", transaction.getCommunicant().getHost());
        assertEquals(8081, transaction.getCommunicant().getPort());

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
        payload.put("s2s.protocol", "RAW");
        payload.put("test", "testReceiveRawProxy");

        postData(8030, payload);

        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://nginx.example.com:7080/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.RAW)
                .portName("output-raw")
                .build();

        final Transaction transaction = client.createTransaction(TransferDirection.RECEIVE);
        assertEquals("nginx.example.com", transaction.getCommunicant().getHost());
        assertEquals(7081, transaction.getCommunicant().getPort());

        for (DataPacket packet; (packet = transaction.receive()) != null;) {
            final Map received = jsonFactory.createJsonParser(packet.getData()).parse(Map.class);
            assertEquals(payload, received);
        }

        transaction.confirm();
        transaction.complete();
    }

}
