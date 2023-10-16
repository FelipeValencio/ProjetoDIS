package org.example;

import no.uib.cipr.matrix.*;

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

        // Step 1: Find the scaling factor
        double scalingFactor = findScalingFactor(matrizModelo);

        // Step 2: Scale the matrix
        scaleMatrix(matrizModelo, scalingFactor);
        scaleVector(vetorSinal, scalingFactor);

        CGNR calcs = new CGNR();

        calcs.CGNRCalc(vetorSinal, matrizModelo, S, N);
    }

    private static double findScalingFactor(Matrix matrix) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (MatrixEntry e : matrix) {
            double value = e.get();
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }

        return Math.max(Math.abs(min), Math.abs(max));
    }

    private static void scaleMatrix(Matrix matrix, double scalingFactor) {
        for (MatrixEntry e : matrix) {
            e.set(e.get() / scalingFactor);
        }
    }

    private static void scaleVector(Vector vector, double scalingFactor) {
        for (VectorEntry e : vector) {
            e.set(e.get() / scalingFactor);
        }
    }

}
