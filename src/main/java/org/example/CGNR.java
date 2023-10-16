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
        f = Matrix
        r = Vector
        z = Vector
        p = Vector
     */

    public void CGNRCalc(Vector g, Matrix h, int S, int N) {

        // f = 0
        Matrix f = new DenseMatrix(h.numColumns(), 1);
        f.zero();
        Matrix fm1 = new DenseMatrix(h.numColumns(), 1);
        fm1.zero();

        // r = g - H*f
        Matrix Hf = new DenseMatrix(h.numRows(), f.numColumns());
        h.mult(f, Hf);
        Vector r = new DenseVector(g.size());
        // Perform element-wise subtraction
        for (int i = 0; i < Hf.numRows(); i++) {
            for (int j = 0; j < Hf.numColumns(); j++) {
                if(i >= 81) {
                    System.out.println("ds");
                }
                r.set(i, g.get(i) - Hf.get(i, j));
            }
        }
        Vector rm1 = new DenseVector(r.size());
        rm1.zero();

        // z = (hˆt) * r
        Vector z = new DenseVector(h.numColumns());
        h.transMult(r, z);
        Vector zm1 = new DenseVector(z.size());
        zm1.zero();

        // p = z
        Vector p = new DenseVector(z.size());
        p.set(z);
        Vector pm1 = new DenseVector(p.size());
        pm1.zero();

        Vector w = new DenseVector(h.numRows());
        double a;
        double b;

        for (int i = 0; i < 5; i++) {
            // w = H * p[i]
            h.mult(p, w);

            // a = norm2(z[i]) / norm2(w[i])
            double zNorm = z.norm(Vector.Norm.Two);
            double wNorm = w.norm(Vector.Norm.Two);
            a = zNorm / wNorm;

            // f[i+1] = f[i] + (a * p[i])
            Vector ap = new DenseVector(p.size());
            ap.set(p);
            ap.scale(a);
            fm1.set(f);
            for (int j = 0; j < fm1.numColumns(); j++) {
                for (int k = 0; k < fm1.numRows(); k++) {
                    fm1.add(k, j, ap.get(j));
                }
            }

            // r[i+1] = r[i] + (a * w[i])
            rm1.set(w);
            rm1.scale(a);
            rm1.add(1, r);

            // z[i+1] = H^t * r[i+1]
            h.transMult(rm1, zm1);

            // b = norm2(z[i+1]) / norm2(z[i])
            double zm1Norm = zm1.norm(Vector.Norm.Two);
            zNorm = z.norm(Vector.Norm.Two);
            b = zm1Norm / zNorm;

            // p[i+1] = z[i+1] + (b * p[i])
            pm1.set(p);
            pm1.scale(b);
            pm1.add(1, zm1);

            // Atualizar valores i+1
            p.set(pm1);
            z.set(zm1);
            f.set(fm1);
            r.set(rm1);
        }
    }

}
