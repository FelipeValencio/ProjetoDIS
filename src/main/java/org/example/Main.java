package org.example;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrix;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;

public class Main {

    @Contract("_ -> new")
    public static @NotNull Matrix importFromCsv(String fileName) throws FileNotFoundException {
        ArrayList<ArrayList<Double>> matrix = new ArrayList<>();

        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("dados/" + fileName);

            assert is != null;
            Reader targetReader = new InputStreamReader(is);
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

            CSVReader csvReader = new CSVReaderBuilder(targetReader).withCSVParser(parser).build();
            String[] nextRecord;

            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                ArrayList<Double> temp = new ArrayList<>();
                for (String cell : nextRecord) {
                    temp.add(Double.parseDouble(cell));
                }
                matrix.add(temp);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        int numRows = matrix.size();
        int numCols = (numRows > 0) ? matrix.get(0).size() : 0;

        // Convert the ArrayList<ArrayList<Double>> to double[][]
        double[][] matrixArray = new double[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            ArrayList<Double> row = matrix.get(i);
            for (int j = 0; j < numCols; j++) {
                matrixArray[i][j] = row.get(j);
            }
        }

        return new DenseMatrix(matrixArray);
    }

    public static void printMatrix(Matrix matrixResult) {
        for (int i = 0; i < matrixResult.numColumns(); i++) {
            for (int j = 0; j < matrixResult.numRows(); j++) {
                System.out.print(matrixResult.get(i, j) + " ");
            }
            System.out.print("\n");
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Matrix N = importFromCsv("N.csv");
        Matrix MN = importFromCsv("MN.csv");
        Matrix M = importFromCsv("M.csv");
        Matrix aM = importFromCsv("aM.csv");
        Matrix a = importFromCsv("a.csv");

        //MN = M * N
        DenseMatrix result = new DenseMatrix(M.numRows(),N.numColumns());
        Matrix matrixResult = M.mult(N,result);

        printMatrix(matrixResult);

        //aM = a * M
        result = new DenseMatrix(a.numRows(),M.numColumns());
        matrixResult = a.mult(M,result);

        printMatrix(matrixResult);

        //Ma = M * a

        result = new DenseMatrix(M.numRows(),a.numColumns());
        matrixResult = M.mult(a,result);

        printMatrix(matrixResult);
    }

}