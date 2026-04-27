package com.supermarkets.structures.b;

import com.supermarkets.pojo.Product;

public class ArbolB {
    protected int orden;
    protected NodoB raiz;

    public ArbolB(int m) {
        this.orden = m;
        this.raiz = null;
    }

    public void insertar(Product cl) {
        validarProducto(cl, "insertar");

        if (buscar(cl.getExpiryDate()) != null) {
            throw new IllegalArgumentException("ya existe un producto con expiryDate: " + cl.getExpiryDate());
        }

        if (raiz == null) {
            raiz = new NodoB(orden);
            raiz.Pcuenta(1);
            raiz.Pclave(1, cl);
            return;
        }

        if (raiz.nodoLleno()) {
            NodoB nuevaRaiz = new NodoB(orden);
            nuevaRaiz.Prama(0, raiz);
            dividirHijo(nuevaRaiz, 0);
            raiz = nuevaRaiz;
        }

        insertarNoLleno(raiz, cl);
    }

    public void eliminar(String clave) {
        if (clave == null || clave.trim().isEmpty()) {
            return;
        }

        raiz = eliminar(raiz, clave);

        if (raiz != null && raiz.Ocuenta() == 0) {
            if (raiz.nodoSemiVacio()) {
                raiz = null;
            } else {
                raiz = raiz.Orama(0);
            }
        }
    }

    public Product buscar(String clave) {
        if (clave == null || clave.trim().isEmpty()) {
            return null;
        }

        return buscarRecursivo(raiz, clave);
    }

    private Product buscarRecursivo(NodoB actual, String clave) {
        if (actual == null) {
            return null;
        }

        if (actual.Ocuenta() == 0) {
            if (actual.nodoSemiVacio()) {
                return null;
            }
            return buscarRecursivo(actual.Orama(0), clave);
        }

        int index;
        boolean encontrado;

        if (comparar(clave, actual.Oclave(1).getExpiryDate()) < 0) {
            encontrado = false;
            index = 0;
        } else {
            index = actual.Ocuenta();
            while (comparar(clave, actual.Oclave(index).getExpiryDate()) < 0 && index > 1) {
                index--;
            }
            encontrado = comparar(clave, actual.Oclave(index).getExpiryDate()) == 0;
        }

        if (encontrado) {
            return actual.Oclave(index);
        }
        return buscarRecursivo(actual.Orama(index), clave);
    }

    private void insertarNoLleno(NodoB nodo, Product producto) {
        if (nodo == null) {
            return;
        }

        int i = nodo.Ocuenta();
        String clave = producto.getExpiryDate();

        if (nodo.nodoSemiVacio()) {
            while (i >= 1 && comparar(clave, nodo.Oclave(i).getExpiryDate()) < 0) {
                nodo.Pclave(i + 1, nodo.Oclave(i));
                i--;
            }
            nodo.Pclave(i + 1, producto);
            nodo.Pcuenta(nodo.Ocuenta() + 1);
            return;
        }

        while (i >= 1 && comparar(clave, nodo.Oclave(i).getExpiryDate()) < 0) {
            i--;
        }

        if (nodo.Orama(i) != null && nodo.Orama(i).nodoLleno()) {
            dividirHijo(nodo, i);
            if (comparar(clave, nodo.Oclave(i + 1).getExpiryDate()) > 0) {
                i++;
            }
        }

        insertarNoLleno(nodo.Orama(i), producto);
    }

    private void dividirHijo(NodoB padre, int indiceHijo) {
        if (padre == null) {
            return;
        }

        NodoB hijo = padre.Orama(indiceHijo);
        if (hijo == null || !hijo.nodoLleno()) {
            return;
        }

        int cuentaOriginal = hijo.Ocuenta();
        int claveMediana = orden / 2;
        Product mediana = hijo.Oclave(claveMediana);

        NodoB nuevo = new NodoB(orden);
        int clavesDerecha = cuentaOriginal - claveMediana;

        nuevo.Pcuenta(clavesDerecha);
        int destinoClave = 1;
        for (int i = claveMediana + 1; i <= cuentaOriginal; i++) {
            nuevo.Pclave(destinoClave++, hijo.Oclave(i));
            hijo.Pclave(i, null);
        }

        if (!hijo.nodoSemiVacio()) {
            int destinoRama = 0;
            for (int i = claveMediana; i <= cuentaOriginal; i++) {
                nuevo.Prama(destinoRama++, hijo.Orama(i));
                hijo.Prama(i, null);
            }
        }

        hijo.Pcuenta(claveMediana - 1);

        for (int i = padre.Ocuenta(); i >= indiceHijo + 1; i--) {
            padre.Pclave(i + 1, padre.Oclave(i));
            padre.Prama(i + 1, padre.Orama(i));
        }

        padre.Pclave(indiceHijo + 1, mediana);
        padre.Prama(indiceHijo + 1, nuevo);
        padre.Pcuenta(padre.Ocuenta() + 1);
    }

    private NodoB eliminar(NodoB nodo, String clave) {
        if (nodo == null) {
            return null;
        }

        int minClaves = (orden - 1) / 2;

        int k = 1;
        while (k <= nodo.Ocuenta() && comparar(clave, nodo.Oclave(k).getExpiryDate()) > 0) {
            k++;
        }

        boolean encontrado = k <= nodo.Ocuenta() && comparar(clave, nodo.Oclave(k).getExpiryDate()) == 0;

        if (nodo.nodoSemiVacio()) {
            if (encontrado) {
                for (int i = k; i < nodo.Ocuenta(); i++) {
                    nodo.Pclave(i, nodo.Oclave(i + 1));
                }
                if (nodo.Ocuenta() > 0) {
                    nodo.Pclave(nodo.Ocuenta(), null);
                }
                nodo.Pcuenta(nodo.Ocuenta() - 1);
            }
        } else {
            if (encontrado) {
                if (nodo.Orama(k - 1) != null && nodo.Orama(k - 1).Ocuenta() > minClaves) {
                    NodoB pred = nodo.Orama(k - 1);
                    while (pred != null && !pred.nodoSemiVacio()) {
                        pred = pred.Orama(pred.Ocuenta());
                    }
                    Product predecesor = pred.Oclave(pred.Ocuenta());
                    nodo.Pclave(k, predecesor);
                    nodo.Prama(k - 1, eliminar(nodo.Orama(k - 1), predecesor.getExpiryDate()));
                } else if (nodo.Orama(k) != null && nodo.Orama(k).Ocuenta() > minClaves) {
                    NodoB succ = nodo.Orama(k);
                    while (succ != null && !succ.nodoSemiVacio()) {
                        succ = succ.Orama(0);
                    }
                    Product sucesor = succ.Oclave(1);
                    nodo.Pclave(k, sucesor);
                    nodo.Prama(k, eliminar(nodo.Orama(k), sucesor.getExpiryDate()));
                } else {
                    fusionarNodos(nodo, k);
                    nodo.Prama(k - 1, eliminar(nodo.Orama(k - 1), clave));
                }
            } else {
                boolean esEnUltimo = k == nodo.Ocuenta() + 1;

                if (nodo.Orama(k) != null && nodo.Orama(k).Ocuenta() <= minClaves) {
                    llenarNodo(nodo, k);
                }

                if (esEnUltimo && k > nodo.Ocuenta()) {
                    nodo.Prama(k - 1, eliminar(nodo.Orama(k - 1), clave));
                } else {
                    nodo.Prama(k, eliminar(nodo.Orama(k), clave));
                }
            }
        }
        return nodo;
    }

    private void llenarNodo(NodoB nodo, int k) {
        int minClaves = (orden - 1) / 2;

        if (nodo == null || nodo.Orama(k) == null) {
            return;
        }

        if (k > 0 && nodo.Orama(k - 1) != null && nodo.Orama(k - 1).Ocuenta() > minClaves) {
            tomarDelAnterior(nodo, k);
        } else if (k < nodo.Ocuenta() && nodo.Orama(k + 1) != null && nodo.Orama(k + 1).Ocuenta() > minClaves) {
            tomarDelSiguiente(nodo, k);
        } else if (k > 0) {
            fusionarNodos(nodo, k);
        } else {
            fusionarNodos(nodo, k + 1);
        }
    }

    private void tomarDelAnterior(NodoB nodo, int pos) {
        NodoB hijo = nodo.Orama(pos);
        NodoB hermanoAnterior = nodo.Orama(pos - 1);
        boolean esHoja = hijo.Orama(0) == null;

        int cuentaHijo = hijo.Ocuenta();
        int cuentaHermano = hermanoAnterior.Ocuenta();

        for (int i = cuentaHijo; i >= 1; i--) {
            hijo.Pclave(i + 1, hijo.Oclave(i));
        }
        if (!esHoja) {
            for (int i = cuentaHijo; i >= 0; i--) {
                hijo.Prama(i + 1, hijo.Orama(i));
            }
            hijo.Prama(0, hermanoAnterior.Orama(cuentaHermano));
        }

        hijo.Pclave(1, nodo.Oclave(pos));
        nodo.Pclave(pos, hermanoAnterior.Oclave(cuentaHermano));
        hijo.Pcuenta(cuentaHijo + 1);
        hermanoAnterior.Pcuenta(cuentaHermano - 1);

        hermanoAnterior.Pclave(cuentaHermano, null);
        if (!esHoja) {
            hermanoAnterior.Prama(cuentaHermano, null);
        }
    }

    private void tomarDelSiguiente(NodoB nodo, int pos) {
        NodoB hijo = nodo.Orama(pos);
        NodoB hermanoSiguiente = nodo.Orama(pos + 1);
        boolean esHoja = hijo.Orama(0) == null;

        int cuentaHijo = hijo.Ocuenta();
        int cuentaHermano = hermanoSiguiente.Ocuenta();

        hijo.Pclave(cuentaHijo + 1, nodo.Oclave(pos + 1));
        if (!esHoja) {
            hijo.Prama(cuentaHijo + 1, hermanoSiguiente.Orama(0));
        }

        nodo.Pclave(pos + 1, hermanoSiguiente.Oclave(1));

        for (int i = 1; i < cuentaHermano; i++) {
            hermanoSiguiente.Pclave(i, hermanoSiguiente.Oclave(i + 1));
        }
        if (!esHoja) {
            for (int i = 0; i < cuentaHermano; i++) {
                hermanoSiguiente.Prama(i, hermanoSiguiente.Orama(i + 1));
            }
        }

        hijo.Pcuenta(cuentaHijo + 1);
        hermanoSiguiente.Pcuenta(cuentaHermano - 1);

        hermanoSiguiente.Pclave(cuentaHermano, null);
        if (!esHoja) {
            hermanoSiguiente.Prama(cuentaHermano, null);
        }
    }

    private void fusionarNodos(NodoB nodo, int k) {
        NodoB hijo = nodo.Orama(k - 1);
        NodoB hermano = nodo.Orama(k);
        boolean esHoja = hijo.Orama(0) == null;

        int cuentaHijo = hijo.Ocuenta();
        int cuentaHermano = hermano.Ocuenta();

        hijo.Pclave(cuentaHijo + 1, nodo.Oclave(k));
        if (!esHoja) {
            hijo.Prama(cuentaHijo + 1, hermano.Orama(0));
        }
        for (int i = 1; i <= cuentaHermano; i++) {
            hijo.Pclave(cuentaHijo + 1 + i, hermano.Oclave(i));
        }
        if (!esHoja) {
            for (int i = 1; i <= cuentaHermano; i++) {
                hijo.Prama(cuentaHijo + 1 + i, hermano.Orama(i));
            }
        }

        hijo.Pcuenta(cuentaHijo + cuentaHermano + 1);

        for (int i = k; i < nodo.Ocuenta(); i++) {
            nodo.Pclave(i, nodo.Oclave(i + 1));
            nodo.Prama(i, nodo.Orama(i + 1));
        }

        nodo.Pcuenta(nodo.Ocuenta() - 1);
        nodo.Pclave(nodo.Ocuenta() + 1, null);
        nodo.Prama(nodo.Ocuenta() + 1, null);
    }

    private int comparar(String a, String b) {
        String izquierda = a == null ? "" : a;
        String derecha = b == null ? "" : b;
        return izquierda.compareTo(derecha);
    }

    private void validarProducto(Product cl, String operacion) {
        if (cl == null) {
            throw new IllegalArgumentException(operacion + ": dato nulo");
        }

        String expiryDate = cl.getExpiryDate();
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            throw new IllegalArgumentException(operacion + ": expiryDate vacio");
        }
    }
}
