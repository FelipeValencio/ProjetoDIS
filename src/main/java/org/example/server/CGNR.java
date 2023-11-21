package org.example.server;

import no.uib.cipr.matrix.*;
import org.example.grpc.ImagemProcessada;

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

    public Vector CGNRCalc(Vector g, Matrix h, int S, int N, ImagemProcessada.Builder imagemProcessadaBuilder) {

        // f = 0
        Vector f = new DenseVector(h.numColumns());
        f.zero();
        Vector fm1 = new DenseVector(h.numColumns());
        fm1.zero();

        // r = g - H*f
        Vector Hf = new DenseVector(h.numRows());
        h.mult(f, Hf);
        Vector r = new DenseVector(g.size());
        r.set(g);
        r.add(-1.0, Hf);
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
        double zNorm;
        double wNorm;
        double zm1Norm;
        Vector ap = new DenseVector(p.size());

        int i;
        for (i = 0; i < 3; i++) {
            System.out.println("for");
            // w = H * p[i]
            h.mult(p, w);

            // a = norm2(z[i]) / norm2(w[i])
            zNorm = z.norm(Vector.Norm.Two);
            wNorm = w.norm(Vector.Norm.Two);
            a = zNorm / wNorm;

            // f[i+1] = f[i] + (a * p[i])
            ap.set(p);
            ap.scale(a);
            fm1.set(f);
            fm1.add(ap);

            // r[i+1] = r[i] + (a * w[i])
            rm1.set(w);
            rm1.scale(a);
            rm1.add(1, r);

            // z[i+1] = H^t * r[i+1]
            h.transMult(rm1, zm1);

            // b = norm2(z[i+1]) / norm2(z[i])
            zm1Norm = zm1.norm(Vector.Norm.Two);
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

        imagemProcessadaBuilder.setInteracoes(i);

        System.out.println("cabo");

        return f;
    }
}
