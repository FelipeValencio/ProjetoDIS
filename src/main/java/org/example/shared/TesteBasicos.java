package org.example.shared;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrix;
import org.example.shared.FileResourcesUtils;

import java.io.FileNotFoundException;

public class TesteBasicos {

    public static void runTestesBasicos() throws FileNotFoundException {
        FileResourcesUtils app = new FileResourcesUtils();

        Matrix N = app.importMatrixFromCsv("N.csv", ';');
        Matrix MN = app.importMatrixFromCsv("MN.csv", ';');
        Matrix M = app.importMatrixFromCsv("M.csv", ';');
        Matrix aM = app.importMatrixFromCsv("aM.csv", ';');
        Matrix a = app.importMatrixFromCsv("a.csv", ';');

        //MN = M * N
        DenseMatrix result = new DenseMatrix(M.numRows(),N.numColumns());
        Matrix matrixResult = M.mult(N,result);

        printMatrix("MN = M * N", matrixResult);

        //aM = a * M
        result = new DenseMatrix(a.numRows(),M.numColumns());
        matrixResult = a.mult(M,result);

        printMatrix("aM = a * M", matrixResult);

        //Ma = M * a Está dando erro
//        result = new DenseMatrix(M.numRows(),a.numColumns());
//        matrixResult = M.mult(a,result);
//
//        printMatrix("Ma = M * a", matrixResult);
    }

    public static void printMatrix(String operation, Matrix matrixResult) {
        System.out.print(operation + "\n");
        for (int i = 0; i < matrixResult.numRows(); i++) {
            for (int j = 0; j < matrixResult.numColumns(); j++) {
                System.out.print(matrixResult.get(i, j) + " ");
            }
            System.out.print("\n");
        }
    }
}
