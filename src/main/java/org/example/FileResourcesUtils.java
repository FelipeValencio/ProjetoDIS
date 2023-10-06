package org.example;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;

public class FileResourcesUtils {

    public @NotNull Matrix importMatrixFromCsv(String fileName, char separator) throws FileNotFoundException {
        ArrayList<ArrayList<Double>> matrix = new ArrayList<>();

        try {
            CSVReader csvReader = getStrings(fileName, separator);
            String[] nextRecord;

            while ((nextRecord = csvReader.readNext()) != null) {
                ArrayList<Double> temp = new ArrayList<>();
                for (String cell : nextRecord) {
                    temp.add(Double.parseDouble(cell));
                }
                matrix.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int numRows = matrix.size();
        int numCols = (numRows > 0) ? matrix.get(0).size() : 0;

        double[][] matrixArray = new double[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            ArrayList<Double> row = matrix.get(i);
            for (int j = 0; j < numCols; j++) {
                matrixArray[i][j] = row.get(j);
            }
        }

        return new DenseMatrix(matrixArray);
    }

    public @NotNull Vector importVectorFromCsv(String fileName, char separator) throws FileNotFoundException {
        ArrayList<Double> vector = new ArrayList<>();

        try {
            CSVReader csvReader = getStrings(fileName, separator);
            String[] nextRecord;

            while ((nextRecord = csvReader.readNext()) != null) {
                for (String cell : nextRecord) {
                    vector.add(Double.parseDouble(cell));
                }
            }

            double[] vectorArray = convertToDoubleArray(vector);

            return new DenseVector(vectorArray);

        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    private double[] convertToDoubleArray(ArrayList<Double> arrayList) {
        int size = arrayList.size();
        double[] doubleArray = new double[size];

        for (int i = 0; i < size; i++) {
            BigDecimal bigDecimal = BigDecimal.valueOf(arrayList.get(i));
            doubleArray[i] = bigDecimal.doubleValue();
        }

        return doubleArray;
    }

    private CSVReader getStrings(String fileName, char separator) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        }
        Reader targetReader = new InputStreamReader(inputStream);
        CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();

        return new CSVReaderBuilder(targetReader).withCSVParser(parser).build();
    }
}