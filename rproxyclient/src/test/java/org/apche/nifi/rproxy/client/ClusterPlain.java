package org.apche.nifi.rproxy.client;

import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.protocol.SiteToSiteTransportProtocol;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class ClusterPlain {

    @Test
    public void testSendRAWWithProxy() throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://nginx.example.com:18080/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.RAW)
                .portName("input-raw")
                .build();

        final Transaction transaction = client.createTransaction(TransferDirection.SEND);
        transaction.send("test".getBytes(), Collections.EMPTY_MAP);
        transaction.confirm();
        transaction.complete();


    }
}
