package com.rumawaks.nifi.rproxy.client.raw;

import com.google.api.client.json.GenericJson;
import com.rumawaks.nifi.rproxy.client.AbstractS2SClientTest;
import org.apache.nifi.remote.Communicant;
import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.protocol.DataPacket;
import org.apache.nifi.remote.protocol.SiteToSiteTransportProtocol;
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

public class ClusterPlainServerName extends AbstractS2SClientTest {

    @Test
    public void testSendDirect() throws IOException {
        try (final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://nifi0:18080/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.RAW)
                .portName("input-raw")
                .requestBatchCount(1)
                .build()) {

            final Map<String, AtomicInteger> distCount = new HashMap<>();
            for (int i = 0; i < 4; i++) {
                final String inputUuid = UUID.randomUUID().toString();
                final Transaction transaction = client.createTransaction(TransferDirection.SEND);
                final Communicant peer = transaction.getCommunicant();
                distCount.computeIfAbsent(format("%s:%d", peer.getHost(), peer.getPort()),
                        k -> new AtomicInteger()).getAndIncrement();
                transaction.send("testSendRawDirect".getBytes(), Collections.singletonMap("input.uuid", inputUuid));
                transaction.confirm();
                transaction.complete();

                final GenericJson json = getJson("http://nifi0:8022?input.uuid=" + inputUuid);
                assertEquals("testSendRawDirect", json.get("content.0"));
                assertEquals("s2sclient", json.get("s2s.host"));
            }

            assertTrue(distCount.get("nifi0:18091").get() > 0);
            assertTrue(distCount.get("nifi1:18092").get() > 0);

        }
    }

    @Test
    public void testSendProxy() throws IOException {
        try (final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://nginx.example.com:17190/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.RAW)
                .portName("input-raw")
                .requestBatchCount(1)
                .build()) {

            final Map<String, AtomicInteger> distCount = new HashMap<>();
            for (int i = 0; i < 4; i++) {
                final String inputUuid = UUID.randomUUID().toString();
                final Transaction transaction = client.createTransaction(TransferDirection.SEND);
                final Communicant peer = transaction.getCommunicant();
                distCount.computeIfAbsent(format("%s:%d", peer.getHost(), peer.getPort()),
                        k -> new AtomicInteger()).getAndIncrement();
                transaction.send("testSendRawProxy".getBytes(), Collections.singletonMap("input.uuid", inputUuid));
                transaction.confirm();
                transaction.complete();

                final GenericJson json = getJson("http://nifi0:8022?input.uuid=" + inputUuid);
                assertEquals("testSendRawProxy", json.get("content.0"));
                assertEquals("nginx.example.com", json.get("s2s.host"));
            }

            assertTrue(distCount.get("nifi0.example.com:17191").get() > 0);
            assertTrue(distCount.get("nifi1.example.com:17191").get() > 0);
        }
    }

    @Test
    @Override
    public void testReceiveDirect() throws IOException {
        final String inputUuid = UUID.randomUUID().toString();
        final Map<String, String> payload = new HashMap<>();
        payload.put("input.uuid", inputUuid);
        payload.put("s2s.protocol", "RAW");
        payload.put("test", "testReceiveRawDirect");

        postData(8032, payload);
        postData(8033, payload);

        try (final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://nifi0:18080/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.RAW)
                .portName("output-raw")
                .requestBatchCount(1)
                .build()) {

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

            assertTrue(distCount.get("nifi0:18091").get() > 0);
            assertTrue(distCount.get("nifi1:18092").get() > 0);
        }
    }

    @Test
    @Override
    public void testReceiveProxy() throws IOException {
        final String inputUuid = UUID.randomUUID().toString();
        final Map<String, String> payload = new HashMap<>();
        payload.put("input.uuid", inputUuid);
        payload.put("s2s.protocol", "RAW");
        payload.put("test", "testReceiveRawProxy");

        postData(8032, payload);
        postData(8033, payload);

        try (final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://nginx.example.com:17190/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.RAW)
                .portName("output-raw")
                .requestBatchCount(1)
                .build()) {

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

            assertTrue(distCount.get("nifi0.example.com:17191").get() > 0);
            assertTrue(distCount.get("nifi1.example.com:17191").get() > 0);
        }

   }
}
