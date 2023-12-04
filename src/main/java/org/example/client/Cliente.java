package org.example.client;

import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.grpc.*;
import org.example.shared.FileResourcesUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cliente extends Thread{
    public static void main(String[] args) throws InterruptedException {

        final double NUM_THREADS = 10;

        Cliente cliente = new Cliente();

        Random rand = new Random();

        for(int i = 0; i < NUM_THREADS; i++) {

            Thread.sleep((rand.nextInt(5) * 1000));

            Thread t = new Thread(cliente, "Cliente" + (i+1));

            t.start();
        }

    }

    public void run()  {

        //Modelo 1
//        int S = 794;
        //Modelo 2
        int S = 436;
        int N = 64;

        System.out.println(
                "Current Thread Name: "
                        + Thread.currentThread().getName());

        System.out.println(
                "Current Thread ID: "
                        + Thread.currentThread().getId());

        // Inicia comunicacao com servidor
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8081).usePlaintext().build();

        //Instancia objeto para processar imagem
        ProcessamentoImagemServiceGrpc.ProcessamentoImagemServiceFutureStub stub =
                ProcessamentoImagemServiceGrpc.newFutureStub(channel);

        ProcessamentoImagemServiceGrpc.ProcessamentoImagemServiceBlockingStub blockingStub
                = ProcessamentoImagemServiceGrpc.newBlockingStub(channel);

        //Instancia objeto para puxar arquivo sinal
        FileResourcesUtils files = new FileResourcesUtils();

        double[] vetorSinal;

        vetorSinal = files.importVectorFromCsv("modelo2/g-30x30-1.csv", ';');

        vetorSinal = calculaGanhoSinal(vetorSinal, S, N);

        // constroi vetor para mandar para o servidor
        VetorSinal.Builder vetorSinalBuilder = VetorSinal.newBuilder();

        Double[] objectArray = Arrays.stream(vetorSinal).boxed().toArray(Double[]::new);
        vetorSinalBuilder.addAllVetorSinal(Arrays.asList(objectArray));

        vetorSinalBuilder.setIdUsuario(Thread.currentThread().getName());
        vetorSinalBuilder.setAlgoritmo("CGNR");

        int tentativa = 1;

        Recursos recursos = blockingStub.getRecursos(EmptyRequest.newBuilder().build());
        System.out.println( "Current Thread Name: "
                + Thread.currentThread().getName() + " CPU Usage: " + recursos.getCpu() + "%");
        System.out.println( "Current Thread Name: "
                + Thread.currentThread().getName() + " Memory Usage: " + recursos.getMemoria() + "%");

        // Mover para servidor com fila
        while(recursos.getCpu() > 60 || recursos.getMemoria() > 50) {
            tentativa++;
            System.out.println( "Current Thread Name: "
                    + Thread.currentThread().getName() + " CPU Usage: " + recursos.getCpu() + "%");
            System.out.println( "Current Thread Name: "
                    + Thread.currentThread().getName() + " Memory Usage: " + recursos.getMemoria() + "%");
            System.out.println( "Current Thread Name: "
                    + Thread.currentThread().getName() + " Tentativa: " + tentativa);
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            recursos = blockingStub.getRecursos(EmptyRequest.newBuilder().build());
        }

        ListenableFuture<ImagemProcessada> listenableFuture =
                stub.processarImagem(vetorSinalBuilder.build());

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);

        try {
            ImagemProcessada value = listenableFuture.get();

            relatorio(value, Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fixedThreadPool.shutdown();
            channel.shutdown();
        }

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

        for (int c = 0; c < N; c++) {
            for (int l = 0; l < S; l++) {
                matrizSinal[c][l] = matrizSinal[c][l] * (100 + ( (double) 1/20 * ( l * Math.sqrt(l) ) ) );
            }
        }

        return convertMatrixToVector(matrizSinal);

    }

    public double[][] convertVectorToMatrix(double[] vector, int rows, int cols, int size) {
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