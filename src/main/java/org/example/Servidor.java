package org.example;

import io.grpc.ServerBuilder;
import io.grpc.Server;

import java.io.IOException;

public class Servidor {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(8080)
                .addService(new ProcessamentoImagemServiceImpl()).build();

        server.start();
        server.awaitTermination();
    }
}
