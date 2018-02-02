package org.apche.nifi.rproxy.client;

import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.protocol.SiteToSiteTransportProtocol;

import java.io.IOException;
import java.util.Collections;

public class StandAlonePlain {

    public static void main(String[] args) throws IOException {
        final SiteToSiteClient client = new SiteToSiteClient.Builder()
                .url("http://nginx.example.com:8080/nifi")
                .transportProtocol(SiteToSiteTransportProtocol.RAW)
                .portName("input-raw")
                .build();

        final Transaction transaction = client.createTransaction(TransferDirection.SEND);
        transaction.send("test".getBytes(), Collections.EMPTY_MAP);
        transaction.confirm();
        transaction.complete();


    }
}
