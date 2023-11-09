package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import no.uib.cipr.matrix.Vector;
import org.example.grpc.ImagemProcessada;
import org.example.grpc.ProcessamentoImagemServiceGrpc;
import org.example.grpc.VetorSinal;

import java.io.FileNotFoundException;

public class Cliente {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        ProcessamentoImagemServiceGrpc.ProcessamentoImagemServiceBlockingStub stub
                = ProcessamentoImagemServiceGrpc.newBlockingStub(channel);

        FileResourcesUtils files = new FileResourcesUtils();
        // Vem do cliente
        try {
            // Achar melhor forma de como mandar esse dado para servidor e converter para Vector
            Vector vetorSinal = files.importVectorFromCsv("G-1.csv", ';');
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ImagemProcessada imagemProcessada = stub.processarImagem(VetorSinal.newBuilder()
                .setVetorSinal("teste")
                .setS(1)
                .setN(2)
                .build());

        channel.shutdown();
    }
}