package org.example.server;

import io.grpc.stub.StreamObserver;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import org.example.grpc.ImagemProcessada;
import org.example.grpc.ProcessamentoImagemServiceGrpc;
import org.example.grpc.VetorSinal;
import org.example.server.CGNR;
import org.example.shared.FileResourcesUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ProcessamentoImagemServiceImpl extends ProcessamentoImagemServiceGrpc.ProcessamentoImagemServiceImplBase {
    @Override
    public void processarImagem(VetorSinal request, StreamObserver<ImagemProcessada> responseObserver) {


        FileResourcesUtils files = new FileResourcesUtils();
        Matrix matrizModelo = null;
        try {
            matrizModelo = files.importMatrixFromCsv("H-2.csv", ',');
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        CGNR calcs = new CGNR();

        double[] doubleArray = request.getVetorSinalList().stream().mapToDouble(Double::doubleValue).toArray();

        Vector vetorSinal = new DenseVector(doubleArray);

        Vector result = calcs.CGNRCalc(vetorSinal, matrizModelo, (int) request.getS(), (int) request.getN());
        
        List<Double> vetorDouble = vectorToList(result);

        ImagemProcessada.Builder imagemProcessadaBuilder = ImagemProcessada.newBuilder();
        imagemProcessadaBuilder.addAllImagemProcessada(vetorDouble);

        responseObserver.onNext(imagemProcessadaBuilder.build());

        responseObserver.onCompleted();

    }

    public static List<Double> vectorToList(Vector vector) {
        List<Double> doubleList = new ArrayList<>();

        for (VectorEntry value : vector) {
            doubleList.add(value.get());
        }

        System.out.println("converteu");

        return doubleList;
    }


}
