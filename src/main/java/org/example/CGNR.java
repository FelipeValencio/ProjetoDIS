package org.example;

import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.sparse.CompRowMatrix;

public class CGNR {
    /*
        g - Vetor de sinal
        H - Matriz de modelo
        f - Imagem
        S - Número de amostras do sinal
        N - Número de elementos sensores
    */

    public void CGNRCalc(Vector vetorSinal, Matrix matrizModelo, int S, int N) {

//        // Initialize other variables
//        Matrix imagem = new DenseMatrix(matrizModelo.numRows() ,matrizModelo.numColumns());
//
//        Matrix tempResult = matrizModelo.scale(-imagem.get(0,0));
//
//        Matrix r = vetorSinal.add(1, tempResult);
//
//        Vector z = (Vector) multiply(matrizModelo.transpose(), new DenseMatrix(r));
//
//        Vector p = new DenseVector(z);
//
//        double epsilon = 1e-4;
//        double[] w = new double[matrizModelo.numColumns()];
//        double[] a = new double[matrizModelo.numColumns()];
//        double[] b = new double[matrizModelo.numColumns()];
//
//        double e = r.dot(r) - r.dot(r);
//        int i = 0;
//
//        while (e < epsilon) {
//            matrizModelo.mult(r, z);
//            a[i] = z.dot(z) / matrizModelo.mult(p, w);
//            imagem = imagem.add(p.scale(a[i]));
//            r = r.add(w, -a[i]);
//            matrizModelo.transMult(r, z);
//            b[i] = z.dot(z) / z.dot(z);
//            p = z.add(p.scale(b[i]));
//
//            // Check convergence
//            e = r.dot(r) - r.dot(r);
//
//            i++;
//        }
    }

    public void calc2(Vector vetorSinal, Matrix matrizModelo, int S, int N) {
//        // Define the parameters and matrices
//        Matrix A = new DenseMatrix(/* Initialize A here */); // Symmetric positive-definite matrix
//        Vector b = new DenseVector(/* Initialize b here */);   // Right-hand side vector
//
//        int maxIterations = 100; // Maximum number of iterations
//        double tolerance = 1e-6; // Convergence tolerance
//
//        int n = A.numColumns();  // Size of the system
//        Vector x = new DenseVector(n); // Initial guess for the solution
//        Vector r = new DenseVector(b); // Initial residual
//
//        Vector p = new DenseVector(r); // Initial search direction
//
//        // Main iteration loop
//        for (int k = 0; k < maxIterations; k++) {
//            Vector Ap = new DenseVector(n);
//            A.mult(p, Ap);
//            double alpha = r.dot(r) / p.dot(Ap);
//            x.add(alpha, p);
//            r.add(-alpha, Ap);
//
//            // Check for convergence
//            if (r.norm(Vector.Norm.Two) < tolerance) {
//                System.out.println("Converged after " + (k + 1) + " iterations.");
//                break;
//            }
//
//            double beta = r.dot(r) / r.dot(Ap);
//            p = r.add(beta, p);
//        }
//
//        // Print the final solution
//        System.out.println("Solution x: " + x);
    }

    private Matrix multiply(Matrix x, Matrix y) {
        DenseMatrix res = new DenseMatrix(x.numRows(),y.numColumns());
        return x.mult(y,res);
    }

    private Matrix multiply(Matrix x, DenseMatrix y) {
        DenseMatrix res = new DenseMatrix(x.numRows(),y.numRows());
        return x.mult(y,res);
    }
}
