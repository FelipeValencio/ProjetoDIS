package org.example;

import io.grpc.stub.StreamObserver;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import org.example.grpc.ImagemProcessada;
import org.example.grpc.ProcessamentoImagemServiceGrpc;
import org.example.grpc.VetorSinal;

import java.io.FileNotFoundException;

public class ProcessamentoImagemServiceImpl extends ProcessamentoImagemServiceGrpc.ProcessamentoImagemServiceImplBase {
    @Override
    public void processarImagem(VetorSinal request, StreamObserver<ImagemProcessada> responseObserver) {

        System.out.println(request.getVetorSinal());
        System.out.println(request.getS());
        System.out.println(request.getN());

        FileResourcesUtils files = new FileResourcesUtils();

        Matrix matrizModelo = null;
        try {
            matrizModelo = files.importMatrixFromCsv("H-1.csv", ',');
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        CGNR calcs = new CGNR();

        Vector vetorSinal = null;
//        Vector vetorSinal = request.getVetorSinal(); // Transformar String para Vector

        calcs.CGNRCalc(vetorSinal, matrizModelo, (int) request.getS(), (int) request.getN());

    }

}
