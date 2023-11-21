package org.example.server;

import no.uib.cipr.matrix.*;

public class CGNE {
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

    public Vector CGNECalc(Vector g, Matrix h, int S, int N) {
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

        // p = (hˆt) * r
        Vector p = new DenseVector(h.numColumns());
        h.transMult(r, p);
        Vector pm1 = new DenseVector(p.size());
        pm1.zero();

        double a;
        double b;
        Vector ahp = new DenseVector(h.numRows());
        Vector ap = new DenseVector(p.size());

        for (int i = 0; i < 3; i++) {

            //a = ( (r^t) * r ) / ( (p^t) * p )
            a = ( r.dot(r) ) / ( p.dot(p) );

            // f[i+1] = f[i] + (a * p[i])
            ap.set(p);
            ap.scale(a);
            fm1.set(f);
            fm1.add(ap);

            // r[i+1] = r[i] - (a * (H*p))
            h.mult(p, ahp);
            ahp.scale(a);
            rm1.set(r);
            rm1.add(-1, ahp);

            //b = ( (r[i+1]^t) * r[i+1] ) / ( (r^t) * r )
            b = ( rm1.dot(rm1) ) / ( r.dot(r) );

            // p[i+1] = H^t * r[i+1] + (b * p[i])
            h.transMult(rm1, pm1);
            pm1.add(p.scale(b));

            // Atualizar valores i+1
            p.set(pm1);
            f.set(fm1);
            r.set(rm1);
        }

        System.out.println("cabo");
        return f;
    }

}
