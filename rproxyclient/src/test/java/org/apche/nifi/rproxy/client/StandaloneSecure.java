package org.apche.nifi.rproxy.client;

import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.KeystoreType;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.protocol.SiteToSiteTransportProtocol;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class StandaloneSecure {

    @Test
    public void testSendRAWDirect() throws IOException {
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

        final Transaction transaction = client.createTransaction(TransferDirection.SEND);
        transaction.send("test".getBytes(), Collections.EMPTY_MAP);
        transaction.confirm();
        transaction.complete();
    }

    @Test
    public void testSendRAWWithProxy() throws IOException {
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

        final Transaction transaction = client.createTransaction(TransferDirection.SEND);
        transaction.send("test".getBytes(), Collections.EMPTY_MAP);
        transaction.confirm();
        transaction.complete();
    }
}
