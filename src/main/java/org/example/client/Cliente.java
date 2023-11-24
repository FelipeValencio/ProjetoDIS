package org.example.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.shared.FileResourcesUtils;
import org.example.grpc.ImagemProcessada;
import org.example.grpc.ProcessamentoImagemServiceGrpc;
import org.example.grpc.VetorSinal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Cliente extends Thread{
    public static void main(String[] args) throws IOException, InterruptedException {

        Files.createDirectories(Paths.get("./results"));

        final double NUM_THREADS = 1;

        Cliente cliente = new Cliente();

        Random rand = new Random();

        for(int i = 0; i < NUM_THREADS; i++) {

            Thread.sleep((rand.nextInt(5) * 1000));

            Thread t = new Thread(cliente, "Cliente" + (i+1));

            t.start();
        }

    }

    public void run()  {

        int S = 436;
        int N = 64;

        try {
            Files.createDirectories(Paths.get("./results/" + Thread.currentThread().getName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(
                "Current Thread Name: "
                        + Thread.currentThread().getName());

        System.out.println(
                "Current Thread ID: "
                        + Thread.currentThread().getId());

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

        vetorSinal = calculaGanhoSinal(vetorSinal, S, N);

        // constroi vetor para mandar para o servidor
        VetorSinal.Builder vetorSinalBuilder = VetorSinal.newBuilder();

        Double[] objectArray = Arrays.stream(vetorSinal).boxed().toArray(Double[]::new);
        vetorSinalBuilder.addAllVetorSinal(Arrays.asList(objectArray));

        Random rand = new Random();
        // Set values for other fields
        vetorSinalBuilder.setS(rand.nextInt());
        vetorSinalBuilder.setN(rand.nextInt());
        vetorSinalBuilder.setIdUsuario(Thread.currentThread().getName());
        vetorSinalBuilder.setAlgoritmo("CGNE");

        ImagemProcessada imagemObj = stub.processarImagem(vetorSinalBuilder.build());

        List<Double> imagemProcessada = imagemObj.getImagemList();

        GrayscaleImageConverter imageConverter = new GrayscaleImageConverter(imagemProcessada, imagemProcessada.size());

        imageConverter.saveImage(Thread.currentThread().getName());
        try {
            relatorio(imagemObj, Thread.currentThread().getName());
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        channel.shutdown();
    }

    private void relatorio(ImagemProcessada imagemProcessada, String thread) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("results/"+thread + "/relatorio-"+thread+".txt", "UTF-8");
        writer.println("Imagem gerada: " + "grayscale_image_" + thread + ".png");
        writer.println("Número de iterações: " + imagemProcessada.getInteracoes());
        writer.println("Tempo em segundos: " + (imagemProcessada.getTermino().getSeconds() - imagemProcessada.getInicio().getSeconds()));
        writer.println("Algoritmo: " + imagemProcessada.getAlgoritmo());
        writer.close();
    }

    private double[] calculaGanhoSinal(double[] vetorSinal, int S, int N) {

        double[][] matrizSinal = convertVectorToMatrix(vetorSinal, N, S, S*N);

        double[] y;

        for (int c = 0; c < N; c++) {
            for (int l = 0; l < S; l++) {
                matrizSinal[c][l] = matrizSinal[c][l] * (100 + ( (double) 1/20 * ( l * Math.sqrt(l) ) ) );
            }
        }

        return convertMatrixToVector(matrizSinal);

    }

    public static double[][] convertVectorToMatrix(double[] vector, int rows, int cols, int size) {
        double[][] matrix = new double[rows][cols];
        int index = 0;

        // Populate the matrix using the vector
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (index < size) {
                    matrix[i][j] = vector[index++];
                }
            }
        }
        return matrix;
    }

    public static double[] convertMatrixToVector(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        double[] vector = new double[rows * cols];
        int index = 0;

        // Populate the vector from the matrix
        for (double[] doubles : matrix) {
            for (int j = 0; j < cols; j++) {
                vector[index++] = doubles[j];
            }
        }
        return vector;
    }
}