package org.example;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;

import java.io.FileNotFoundException;

public class Main {
    /*
        g - Vetor de sinal
        H - Matriz de modelo
        f - Imagem
        S - Número de amostras do sinal
        N - Número de elementos sensores
    */
    public static void main(String[] args) throws FileNotFoundException {
//        runTestesBasicos();

        FileResourcesUtils files = new FileResourcesUtils();

        // Vem do cliente
        Vector vetorSinal = files.importVectorFromCsv("g-30x30-1.csv", ';');

        //Hardcoded no servidor
        Matrix matrizModelo = files.importMatrixFromCsv("H-2.csv", ',');

        int S = 436;
        int N = 64;

        CGNR calcs = new CGNR();

        calcs.CGNRCalc(vetorSinal, matrizModelo, S, N);
    }

}
