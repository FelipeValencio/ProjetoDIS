package org.example.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.shared.FileResourcesUtils;
import org.example.grpc.ImagemProcessada;
import org.example.grpc.ProcessamentoImagemServiceGrpc;
import org.example.grpc.VetorSinal;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class Cliente {
    public static void main(String[] args) {
        // Inicia comunicacao com servidor
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        //Instancia objeto para processar imagem
        ProcessamentoImagemServiceGrpc.ProcessamentoImagemServiceBlockingStub stub
                = ProcessamentoImagemServiceGrpc.newBlockingStub(channel);

        //Instancia objeto para puxar arquivo sinal
        FileResourcesUtils files = new FileResourcesUtils();
        // Vem do cliente
        double[] vetorSinal;
        try {
            // Achar melhor forma de como mandar esse dado para servidor e converter para Vector
            vetorSinal = files.importVectorFromCsv("g-30x30-1.csv", ';');
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // constroi vetor para mandar para o servidor
        VetorSinal.Builder vetorSinalBuilder = VetorSinal.newBuilder();
        Double[] objectArray = Arrays.stream(vetorSinal).boxed().toArray(Double[]::new);
        vetorSinalBuilder.addAllVetorSinal(Arrays.asList(objectArray));

        // Set values for other fields
        vetorSinalBuilder.setS(4.0);
        vetorSinalBuilder.setN(5.0);

        ImagemProcessada imagemObj = stub.processarImagem(vetorSinalBuilder.build());

        List<Double> imagemProcessada = imagemObj.getImagemProcessadaList();

        GrayscaleImageConverter imageConverter = new GrayscaleImageConverter(imagemProcessada, imagemProcessada.size());

        imageConverter.saveImage();

        channel.shutdown();
    }
}