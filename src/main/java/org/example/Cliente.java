package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import no.uib.cipr.matrix.Vector;
import org.example.grpc.ImagemProcessada;
import org.example.grpc.ProcessamentoImagemServiceGrpc;
import org.example.grpc.VetorSinal;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class Cliente {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        ProcessamentoImagemServiceGrpc.ProcessamentoImagemServiceBlockingStub stub
                = ProcessamentoImagemServiceGrpc.newBlockingStub(channel);

        FileResourcesUtils files = new FileResourcesUtils();
        // Vem do cliente
        double[] vetorSinal;
        try {
            // Achar melhor forma de como mandar esse dado para servidor e converter para Vector
            vetorSinal = files.importVectorFromCsv("g-30x30-1.csv", ';');
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        VetorSinal.Builder vetorSinalBuilder = VetorSinal.newBuilder();

        // Add all elements from the existing array to the repeated field
        Double[] objectArray = Arrays.stream(vetorSinal).boxed().toArray(Double[]::new);
        vetorSinalBuilder.addAllVetorSinal(Arrays.asList(objectArray));

        // Set values for other fields
        vetorSinalBuilder.setS(4.0);
        vetorSinalBuilder.setN(5.0);


        ImagemProcessada imagemProcessada = stub.processarImagem(vetorSinalBuilder.build());

        channel.shutdown();
    }
}