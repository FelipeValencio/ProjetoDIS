package org.example.server;

import io.grpc.ServerBuilder;
import io.grpc.Server;

import java.io.IOException;

public class Servidor {
    public static void main(String[] args) throws IOException, InterruptedException {

        int port = 8081;

        Server server = ServerBuilder
                .forPort(port)
                .addService(new ProcessamentoImagemServiceImpl())
                .build();

        server.start();

        System.out.println("Servidor iniciado na porta: " + port);

        server.awaitTermination();
    }
}
