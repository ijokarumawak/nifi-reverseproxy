package org.apche.nifi.rproxy.client;

import com.google.api.client.json.GenericJson;
import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.KeystoreType;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.protocol.SiteToSiteTransportProtocol;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ClusterSecure extends AbstractS2SClientTest {

    @Test
    public void testSendRawDirect() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("https://localhost:18443/nifi")
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

        final GenericJson json = getJson("http://localhost:8023?input.uuid=" + inputUuid);
        assertEquals("testSendRawDirect", json.get("content.0"));
        assertEquals("localhost", json.get("s2s.host"));
    }

    @Test
    public void testSendRawProxy() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("https://nginx.example.com:18443/nifi")
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

        final GenericJson json = getJson("http://localhost:8023?input.uuid=" + inputUuid);
        assertEquals("testSendRawProxy", json.get("content.0"));
        assertEquals("nginx.example.com", json.get("s2s.host"));
    }

    @Test
    public void testSendHttpDirect() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("https://localhost:18443/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .portName("input-http")
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

        final GenericJson json = getJson("http://localhost:8023?input.uuid=" + inputUuid);
        assertEquals("testSendHttpDirect", json.get("content.0"));
        assertEquals("localhost", json.get("s2s.host"));
    }

    @Test
    public void testSendHttpProxy() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("https://nginx.example.com:18460/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .portName("input-http")
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

        final GenericJson json = getJson("http://localhost:8023?input.uuid=" + inputUuid);
        assertEquals("testSendHttpDirect", json.get("content.0"));
        assertEquals("localhost", json.get("s2s.host"));
    }

}
