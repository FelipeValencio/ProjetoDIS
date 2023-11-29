package org.example.server;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import org.example.client.GrayscaleImageConverter;
import org.example.grpc.ImagemProcessada;
import org.example.grpc.ProcessamentoImagemServiceGrpc;
import org.example.grpc.VetorSinal;
import org.example.shared.FileResourcesUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProcessamentoImagemServiceImpl extends ProcessamentoImagemServiceGrpc.ProcessamentoImagemServiceImplBase {

    Matrix matrizModelo;

    public ProcessamentoImagemServiceImpl() {
        FileResourcesUtils files = new FileResourcesUtils();
        try {
            matrizModelo = files.importMatrixFromCsv("modelo2/H-2.csv", ',');
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processarImagem(VetorSinal request, StreamObserver<ImagemProcessada> responseObserver) {

        try {
            Files.createDirectories(Paths.get("./results"));
            Files.createDirectories(Paths.get("./results/" + request.getIdUsuario()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("servidor");

        ImagemProcessada.Builder imagemProcessadaBuilder = ImagemProcessada.newBuilder();
        final Timestamp ts1 = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
        imagemProcessadaBuilder.setInicio(ts1);

        CGNR cgnr = new CGNR();
        CGNE cgne = new CGNE();

        double[] doubleArray = request.getVetorSinalList().stream().mapToDouble(Double::doubleValue).toArray();

        Vector vetorSinal = new DenseVector(doubleArray);
        Vector result;

        System.out.println("Inicio processamento");

        if(request.getAlgoritmo().equals("CGNR")) {
            result = cgnr.CGNRCalc(vetorSinal, matrizModelo, imagemProcessadaBuilder);
        }
        else {
            result = cgne.CGNECalc(vetorSinal, matrizModelo, imagemProcessadaBuilder);
        }

        System.out.println("Final processamento");

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

        System.out.println("converteu");

        return doubleList;
    }


}
