package org.example;

import no.uib.cipr.matrix.*;

public class CGNR {
    /*
        g - Vetor de sinal = Entrada que o cliente envia
        H - Matriz de modelo = Entrada fixa no servidor, tem dois modelos um de 60x60 e outro de 30x30
        f - Imagem = Saída
        S - Número de amostras do sinal
        N - Número de elementos sensores
    */

    /*
        g = Vector
        h = Matrix
        S = int
        N = int
        f = Matrix
     */

    public void CGNRCalc(Vector g, Matrix h, int S, int N) {

        // f = 0
        Matrix f = new DenseMatrix(h.numColumns(), 1);
        f.zero();

        // r = g - H*f
        Matrix Hf = new DenseMatrix(h.numRows(), f.numColumns());
        h.mult(f, Hf);
        Matrix r = new DenseMatrix(Hf.numRows(), Hf.numColumns());
        r.set(Hf);
        r.add(-1.0, Hf);
        Matrix rm1 = new DenseMatrix(Hf.numRows(), Hf.numColumns());
        rm1.zero();

        // z = (hˆt)*r
        Matrix z = new DenseMatrix(h.numColumns(), r.numColumns());
        h.transAmult(r, z);
        Matrix zm1 = new DenseMatrix(h.numColumns(), r.numColumns());
        zm1.zero();

        // p = z
        Matrix p = new DenseMatrix(z.numRows(), z.numColumns());
        p.set(z);
        Matrix pm1 = new DenseMatrix(z.numRows(), z.numColumns());
        pm1.zero();

        int i;

        Matrix w = new DenseMatrix(h.numRows(), p.numColumns());
        double a = 0;
        double b = 0;

        for (i = 0; i < 5; i++) {
            // w = H * p[i]
            h.mult(p, w);
            // a[i] = norm2(z[i]) / norm2(w[i])
            double zNorm = z.norm(Matrix.Norm.Frobenius);
            double wNorm = w.norm(Matrix.Norm.Frobenius);
            a = zNorm / wNorm;
            i++;
            // f[i+1] = f[i] + (a[i] * p[i])
            // r[i+1] = r[i] + (a[i] * w[i])
            // z[i+1] = H^t * r[i+1]
            // b[i] = norm2(z[i+1]) / norm2(z[i])
            // p[i+1] = z[i+1] + b[i] * p[i]

        }
    }

}
