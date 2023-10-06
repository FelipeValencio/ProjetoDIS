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

        // Define the parameters and matrices
        Vector vetorSinal = files.importVectorFromCsv("G-1.csv", ';');
        Matrix matrizModelo = files.importMatrixFromCsv("H-1.csv", ',');

        int S = 794;
        int N = 64;

        CGNR calcs = new CGNR();

        calcs.CGNRCalc(vetorSinal, matrizModelo, S, N);
    }

}
