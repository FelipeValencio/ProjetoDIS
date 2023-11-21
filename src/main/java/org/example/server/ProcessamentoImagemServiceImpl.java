package org.example.server;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import org.example.grpc.ImagemProcessada;
import org.example.grpc.ProcessamentoImagemServiceGrpc;
import org.example.grpc.VetorSinal;
import org.example.shared.FileResourcesUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ProcessamentoImagemServiceImpl extends ProcessamentoImagemServiceGrpc.ProcessamentoImagemServiceImplBase {
    @Override
    public void processarImagem(VetorSinal request, StreamObserver<ImagemProcessada> responseObserver) {

        ImagemProcessada.Builder imagemProcessadaBuilder = ImagemProcessada.newBuilder();
        final Timestamp ts1 = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
        imagemProcessadaBuilder.setInicio(ts1);

        FileResourcesUtils files = new FileResourcesUtils();
        Matrix matrizModelo = null;
        try {
            matrizModelo = files.importMatrixFromCsv("H-2.csv", ',');
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        CGNR cgnr = new CGNR();
        CGNE cgne = new CGNE();

        double[] doubleArray = request.getVetorSinalList().stream().mapToDouble(Double::doubleValue).toArray();

        Vector vetorSinal = new DenseVector(doubleArray);
        Vector result;

        if(request.getAlgoritmo().equals("CGNR")) {
            result = cgnr.CGNRCalc(vetorSinal, matrizModelo, (int) request.getS(), (int) request.getN(), imagemProcessadaBuilder);
        }
        else {
            result = cgne.CGNECalc(vetorSinal, matrizModelo, (int) request.getS(), (int) request.getN(), imagemProcessadaBuilder);
        }

        final Timestamp ts2 = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
        imagemProcessadaBuilder.setTermino(ts2);

        List<Double> vetorDouble = vectorToList(result);

        imagemProcessadaBuilder.addAllImagem(vetorDouble);
        imagemProcessadaBuilder.setIdUsuario(request.getIdUsuario());
        imagemProcessadaBuilder.setAlgoritmo(request.getAlgoritmo());
        imagemProcessadaBuilder.setTamanho(vetorDouble.size());

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
