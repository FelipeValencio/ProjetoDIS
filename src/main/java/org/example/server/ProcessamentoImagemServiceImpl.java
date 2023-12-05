package org.example.server;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import org.example.client.GrayscaleImageConverter;
import org.example.grpc.*;
import org.example.shared.FileResourcesUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import com.sun.management.OperatingSystemMXBean;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ProcessamentoImagemServiceImpl extends ProcessamentoImagemServiceGrpc.ProcessamentoImagemServiceImplBase {

    Matrix matrizModelo;

    Queue<VetorSinal> fifoQueue = new LinkedList<>();

    public ProcessamentoImagemServiceImpl() {
        FileResourcesUtils files = new FileResourcesUtils();
        try {
            matrizModelo = files.importMatrixFromCsv("modelo1/H-1.csv", ',');
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private VetorSinal gerenciadorFila() {

        while (getCpu() > 10 || getMemory() > 80) {
            printFormattedQueue();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return fifoQueue.poll();

    }

    @Override
    public void processarImagem(VetorSinal request, StreamObserver<ImagemProcessada> responseObserver) {

        fifoQueue.offer(request);

        request = gerenciadorFila();

        if(request == null) {
            System.out.println("Erro ao processar");
            return;
        }

        try {
            Files.createDirectories(Paths.get("./results"));
            Files.createDirectories(Paths.get("./results/" + request.getIdUsuario()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ImagemProcessada.Builder imagemProcessadaBuilder = ImagemProcessada.newBuilder();
        final Timestamp ts1 = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
        imagemProcessadaBuilder.setInicio(ts1);

        CGNR cgnr = new CGNR();
        CGNE cgne = new CGNE();

        double[] doubleArray = request.getVetorSinalList().stream().mapToDouble(Double::doubleValue).toArray();

        Vector vetorSinal = new DenseVector(doubleArray);
        Vector result;

        System.out.println("Inicio processamento "+ request.getIdUsuario());

        if(request.getAlgoritmo().equals("CGNR")) {
            result = cgnr.CGNRCalc(vetorSinal, matrizModelo, imagemProcessadaBuilder);
        }
        else {
            result = cgne.CGNECalc(vetorSinal, matrizModelo, imagemProcessadaBuilder);
        }

        System.out.println("Final processamento "+ request.getIdUsuario());

        final Timestamp ts2 = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
        imagemProcessadaBuilder.setTermino(ts2);
        imagemProcessadaBuilder.setInicio(ts1);

        List<Double> vetorDouble = vectorToList(result);

        imagemProcessadaBuilder.addAllImagem(vetorDouble);
        imagemProcessadaBuilder.setIdUsuario(request.getIdUsuario());
        imagemProcessadaBuilder.setAlgoritmo(request.getAlgoritmo());
        imagemProcessadaBuilder.setTamanho(vetorDouble.size());

        GrayscaleImageConverter imageConverter = new GrayscaleImageConverter(vetorDouble, vetorDouble.size());

        imageConverter.saveImage(request.getIdUsuario());

        responseObserver.onNext(imagemProcessadaBuilder.build());
        responseObserver.onCompleted();

    }

    public static List<Double> vectorToList(Vector vector) {
        List<Double> doubleList = new ArrayList<>();

        for (VectorEntry value : vector) {
            doubleList.add(value.get());
        }

        return doubleList;
    }

    public double getCpu() {
        // Get the OperatingSystemMXBean
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        return osBean.getSystemCpuLoad() * 100;
    }

    public double getMemory() {
        // Get the MemoryMXBean
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        long usedMemory = heapMemoryUsage.getUsed();
        long maxMemory = heapMemoryUsage.getMax();

        return (double) usedMemory / maxMemory * 100;
    }

    private String formatQueue(Queue<VetorSinal> queue, String separator) {
        StringBuilder formattedQueue = new StringBuilder();

        for (VetorSinal item : queue) {
            formattedQueue.append(item.getIdUsuario()).append(separator);
        }

        // Remove the trailing separator if the queue is not empty
        if (!queue.isEmpty()) {
            formattedQueue.setLength(formattedQueue.length() - separator.length());
        }

        return formattedQueue.toString();
    }

    // Example method to print the formatted queue
    private void printFormattedQueue() {
        String separator = " - ";
        String formattedQueue = formatQueue(fifoQueue, separator);
        System.out.println("Fila: " + formattedQueue);
    }


}
