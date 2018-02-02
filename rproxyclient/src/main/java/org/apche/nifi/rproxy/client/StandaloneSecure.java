package org.apche.nifi.rproxy.client;

import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.KeystoreType;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.protocol.SiteToSiteTransportProtocol;

import java.io.IOException;
import java.util.Collections;

public class StandaloneSecure {

    public static void main(String[] args) throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                // TODO: Sending directly works but not with the proxy.
                /*

2018-02-02 17:57:20,102 ERROR [Site-to-Site Worker Thread-7] o.a.nifi.remote.SocketRemoteSiteListener Unable to communicate with remote instance null due to org.apache.nifi.remote.exception.HandshakeException: Handshake with nifi://nginx.example.com:52666 failed because the Magic Header was not present; closing connection
2018-02-02 17:57:20,102 ERROR [Site-to-Site Worker Thread-8] o.a.nifi.remote.SocketRemoteSiteListener Unable to communicate with remote instance null due to org.apache.nifi.remote.exception.HandshakeException: Handshake with nifi://nginx.example.com:52668 failed because the Magic Header was not present; closing connection

                 */
//                .url("https://localhost:8443/nifi")
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
