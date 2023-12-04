package org.example.shared;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileResourcesUtils {

    public Matrix importMatrixFromCsv(String fileName, char separator) throws FileNotFoundException {
        try (CSVReader csvReader = getStrings(fileName, separator)) {
            int chunkSize = 1000; // Define your chunk size

            List<double[][]> chunks = new ArrayList<>();
            int numCols = 0;

            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                if (numCols == 0) {
                    numCols = nextRecord.length;
                }

                double[][] chunk = new double[chunkSize][numCols];
                int rowCount = 0;

                do {
                    for (int j = 0; j < numCols; j++) {
                        chunk[rowCount][j] = Double.parseDouble(nextRecord[j]);
                    }
                    rowCount++;

                    if (rowCount == chunkSize) {
                        chunks.add(chunk);
                        chunk = new double[chunkSize][numCols];
                        rowCount = 0;
                    }
                } while ( (nextRecord = csvReader.readNext()) != null);

                if (rowCount > 0) {
                    double[][] trimmedChunk = new double[rowCount][numCols];
                    System.arraycopy(chunk, 0, trimmedChunk, 0, rowCount);
                    chunks.add(trimmedChunk);
                }
            }

            int numRows = chunks.stream().mapToInt(chunk -> chunk.length).sum();

            if (numRows > 0 && numCols > 0) {
                double[][] finalMatrix = new double[numRows][numCols];
                int rowIndex = 0;

                for (double[][] chunk : chunks) {
                    int chunkRows = chunk.length;
                    System.arraycopy(chunk, 0, finalMatrix, rowIndex, chunkRows);
                    rowIndex += chunkRows;
                }

                return new DenseMatrix(finalMatrix);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public double[] importVectorFromCsv(String fileName, char separator) {
        try (CSVReader csvReader = getStrings(fileName, separator)) {
            List<String[]> records = csvReader.readAll();
            int totalRecords = records.size();

            if (totalRecords > 0) {
                int totalColumns = records.get(0).length;
                double[] dataArray = new double[totalRecords * totalColumns];
                int dataIndex = 0;

                for (String[] row : records) {
                    for (String cell : row) {
                        dataArray[dataIndex++] = Double.parseDouble(cell);
                    }
                }

                return dataArray;
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }

        return new double[0];
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