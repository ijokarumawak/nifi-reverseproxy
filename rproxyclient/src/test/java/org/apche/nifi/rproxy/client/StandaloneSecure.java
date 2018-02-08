package org.apche.nifi.rproxy.client;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonObjectParser;
import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.KeystoreType;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.protocol.DataPacket;
import org.apache.nifi.remote.protocol.SiteToSiteTransportProtocol;
import org.apache.nifi.stream.io.StreamUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class StandaloneSecure extends AbstractS2SClientTest {

    @Test
    public void testSendRawDirect() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("https://localhost:8443/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.RAW)
                .portName("input-raw")
                .keystoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/localhost/keystore.jks")
                .keystorePass("password")
                .keystoreType(KeystoreType.JKS)
                .truststoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/localhost/truststore.jks")
                .truststorePass("password")
                .truststoreType(KeystoreType.JKS)
                .build();

        final String inputUuid = UUID.randomUUID().toString();
        final Transaction transaction = client.createTransaction(TransferDirection.SEND);
        transaction.send("testSendRawDirect".getBytes(), Collections.singletonMap("input.uuid", inputUuid));
        transaction.confirm();
        transaction.complete();

        final GenericJson json = getJson("http://localhost:8021?input.uuid=" + inputUuid);
        assertEquals("testSendRawDirect", json.get("content.0"));
        assertEquals("192.168.99.1", json.get("s2s.host"));
    }

    @Test
    public void testSendRawProxy() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("https://nginx.example.com:8443/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.RAW)
                .portName("input-raw")
                .keystoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/localhost/keystore.jks")
                .keystorePass("password")
                .keystoreType(KeystoreType.JKS)
                .truststoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/localhost/truststore.jks")
                .truststorePass("password")
                .truststoreType(KeystoreType.JKS)
                .build();

        final String inputUuid = UUID.randomUUID().toString();
        final Transaction transaction = client.createTransaction(TransferDirection.SEND);
        transaction.send("testSendRawProxy".getBytes(), Collections.singletonMap("input.uuid", inputUuid));
        transaction.confirm();
        transaction.complete();

        final GenericJson json = getJson("http://localhost:8021?input.uuid=" + inputUuid);
        assertEquals("testSendRawProxy", json.get("content.0"));
        assertEquals("nginx.example.com", json.get("s2s.host"));
    }

    @Test
    public void testSendHttpDirect() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("https://localhost:8443/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .portName("input-raw")
                .keystoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/localhost/keystore.jks")
                .keystorePass("password")
                .keystoreType(KeystoreType.JKS)
                .truststoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/localhost/truststore.jks")
                .truststorePass("password")
                .truststoreType(KeystoreType.JKS)
                .build();

        final String inputUuid = UUID.randomUUID().toString();
        final Transaction transaction = client.createTransaction(TransferDirection.SEND);
        transaction.send("testSendHttpDirect".getBytes(), Collections.singletonMap("input.uuid", inputUuid));
        transaction.confirm();
        transaction.complete();

        final GenericJson json = getJson("http://localhost:8021?input.uuid=" + inputUuid);
        assertEquals("testSendHttpDirect", json.get("content.0"));
        assertEquals("192.168.99.1", json.get("s2s.host"));
    }

    @Test
    public void testSendHttpProxy() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("https://nginx.example.com:8443/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .portName("input-raw")
                .keystoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/localhost/keystore.jks")
                .keystorePass("password")
                .keystoreType(KeystoreType.JKS)
                .truststoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/localhost/truststore.jks")
                .truststorePass("password")
                .truststoreType(KeystoreType.JKS)
                .build();

        final String inputUuid = UUID.randomUUID().toString();
        final Transaction transaction = client.createTransaction(TransferDirection.SEND);
        transaction.send("testSendHttpProxy".getBytes(), Collections.singletonMap("input.uuid", inputUuid));
        transaction.confirm();
        transaction.complete();

        final GenericJson json = getJson("http://localhost:8021?input.uuid=" + inputUuid);
        assertEquals("testSendHttpProxy", json.get("content.0"));
        assertEquals("nginx.example.com", json.get("s2s.host"));
    }

    @Test
    public void testReceiveRawDirect() throws IOException {

        final String inputUuid = UUID.randomUUID().toString();
        final Map<String, String> payload = new HashMap<>();
        payload.put("input.uuid", inputUuid);
        payload.put("s2s.protocol", "RAW");
        payload.put("test", "testReceiveRawDirect");

        postData(payload);

        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("https://localhost:8443/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.RAW)
                .portName("output-raw")
                .keystoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/localhost/keystore.jks")
                .keystorePass("password")
                .keystoreType(KeystoreType.JKS)
                .truststoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/localhost/truststore.jks")
                .truststorePass("password")
                .truststoreType(KeystoreType.JKS)
                .build();

        final Transaction transaction = client.createTransaction(TransferDirection.RECEIVE);
        assertEquals("HW13076.local", transaction.getCommunicant().getHost());
        assertEquals(8481, transaction.getCommunicant().getPort());

        for (DataPacket packet; (packet = transaction.receive()) != null;) {
            final Map received = jsonFactory.createJsonParser(packet.getData()).parse(Map.class);
            assertEquals(payload, received);
        }

        transaction.confirm();
        transaction.complete();
    }

    @Test
    public void testReceiveHttpDirect() throws IOException {

        final String inputUuid = UUID.randomUUID().toString();
        final Map<String, String> payload = new HashMap<>();
        payload.put("input.uuid", inputUuid);
        payload.put("s2s.protocol", "HTTP");
        payload.put("test", "testReceiveHttpDirect");

        postData(payload);

        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("https://localhost:8443/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .portName("output-http")
                .keystoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/localhost/keystore.jks")
                .keystorePass("password")
                .keystoreType(KeystoreType.JKS)
                .truststoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/localhost/truststore.jks")
                .truststorePass("password")
                .truststoreType(KeystoreType.JKS)
                .build();

        final Transaction transaction = client.createTransaction(TransferDirection.RECEIVE);
        assertEquals("HW13076.local", transaction.getCommunicant().getHost());
        assertEquals(8443, transaction.getCommunicant().getPort());

        for (DataPacket packet; (packet = transaction.receive()) != null;) {
            final Map received = jsonFactory.createJsonParser(packet.getData()).parse(Map.class);
            assertEquals(payload, received);
        }

        transaction.confirm();
        transaction.complete();
    }
}
