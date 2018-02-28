package org.apche.nifi.rproxy.client.http;

import com.google.api.client.json.GenericJson;
import org.apache.nifi.remote.Communicant;
import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.KeystoreType;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.protocol.DataPacket;
import org.apache.nifi.remote.protocol.SiteToSiteTransportProtocol;
import org.apche.nifi.rproxy.client.AbstractS2SClientTest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ELBTerminate extends AbstractS2SClientTest {

    @Ignore
    @Override
    public void testSendDirect() throws IOException {
        // Do nothing.
    }

    @Test
    @Override
    public void testSendProxy() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("https://s2s-elb.rumawaks.com/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .portName("input-http")
                .keystoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/s2s-client/keystore.jks")
                .keystorePass("password")
                .keystoreType(KeystoreType.JKS)
                .truststoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/s2s-client/truststore.jks")
                .truststorePass("password")
                .truststoreType(KeystoreType.JKS)
                .requestBatchCount(1)
                .build();

        final Map<String, AtomicInteger> distCount = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            final String inputUuid = UUID.randomUUID().toString();
            final Transaction transaction = client.createTransaction(TransferDirection.SEND);
            final Communicant peer = transaction.getCommunicant();
            distCount.computeIfAbsent(format("%s:%d", peer.getHost(), peer.getPort()),
                    k -> new AtomicInteger()).getAndIncrement();
            transaction.send("testSendHTTPProxy".getBytes(), Collections.singletonMap("input.uuid", inputUuid));
            transaction.confirm();
            transaction.complete();

            final GenericJson json = getJson("http://nifi0.aws.mine:8022?input.uuid=" + inputUuid);
            assertEquals("testSendHTTPProxy", json.get("content.0"));
        }

        assertTrue(distCount.get("s2s-elb.rumawaks.com:18440").get() > 0);
        assertTrue(distCount.get("s2s-elb.rumawaks.com:18441").get() > 0);
    }

    @Ignore
    @Override
    public void testReceiveDirect() throws IOException {
        // Do nothing.
    }

    @Test
    @Override
    public void testReceiveProxy() throws IOException {
        final String inputUuid = UUID.randomUUID().toString();
        final Map<String, String> payload = new HashMap<>();
        payload.put("input.uuid", inputUuid);
        payload.put("s2s.protocol", "HTTP");
        payload.put("test", "testReceiveHttpProxy");

        postData("nifi0.aws.mine:8032", payload);
        postData("nifi1.aws.mine:8032", payload);

        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("https://s2s-elb.rumawaks.com/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .portName("output-http")
                .keystoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/s2s-client/keystore.jks")
                .keystorePass("password")
                .keystoreType(KeystoreType.JKS)
                .truststoreFilename("/Users/koji/dev/nifi-reverseproxy/nifi/s2s-client/truststore.jks")
                .truststorePass("password")
                .truststoreType(KeystoreType.JKS)
                .requestBatchCount(1)
                .build();

        final Map<String, AtomicInteger> distCount = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            final Transaction transaction = client.createTransaction(TransferDirection.RECEIVE);

            final Communicant peer = transaction.getCommunicant();
            distCount.computeIfAbsent(format("%s:%d", peer.getHost(), peer.getPort()),
                    k -> new AtomicInteger()).getAndIncrement();

            for (DataPacket packet; (packet = transaction.receive()) != null; ) {
                final Map received = jsonFactory.createJsonParser(packet.getData()).parse(Map.class);
                assertEquals(payload, received);
            }

            transaction.confirm();
            transaction.complete();
        }

        assertTrue(distCount.get("s2s-elb.rumawaks.com:18440").get() > 0);
        assertTrue(distCount.get("s2s-elb.rumawaks.com:18441").get() > 0);
    }
}
