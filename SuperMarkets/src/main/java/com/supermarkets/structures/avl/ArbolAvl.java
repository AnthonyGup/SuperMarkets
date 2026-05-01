package com.supermarkets.structures.avl;

import com.supermarkets.pojo.Product;

import java.util.Locale;

public class ArbolAvl {
    private NodoAvl raiz;

    public void insertar(Product valor) {
        validarProducto(valor, "insertar");
        if (buscar(valor.getName())) {
            throw new IllegalArgumentException("ya existe un producto con nombre: " + valor.getName());
        }
        raiz = insertarNodoRecursivo(raiz, valor);
    }

    public void eliminar(Product valor) {
        validarProducto(valor, "eliminar");
        raiz = eliminarNodoRecursivo(raiz, valor);
    }

    public boolean buscar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return false;
        }
        return buscarNodoRecursivo(raiz, nombre);
    }

    public Product buscarProducto(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        return obtenerProducto(nombre);
    }

    public Product busquedaBinaria(String nombre) {
        if (nombre == null || nombre.isBlank() || raiz == null) {
            return null;
        }

        String[] elementos = toArrayInOrder();
        int izquierda = 0;
        int derecha = elementos.length - 1;

        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;
            String actual = elementos[medio];
            int cmp = comparar(actual, nombre);

            if (cmp == 0) {
                return buscarProducto(actual);
            } else if (cmp < 0) {
                izquierda = medio + 1;
            } else {
                derecha = medio - 1;
            }
        }
        return null;
    }

    private String[] toArrayInOrder() {
        int count = contarNodos(raiz);
        String[] resultado = new String[count];
        llenarArray(raiz, resultado, new int[]{0});
        return resultado;
    }

    private int contarNodos(NodoAvl nodo) {
        if (nodo == null) {
            return 0;
        }
        return 1 + contarNodos(nodo.getIzquierdo()) + contarNodos(nodo.getDerecho());
    }

    private void llenarArray(NodoAvl nodo, String[] arr, int[] index) {
        if (nodo == null) {
            return;
        }
        llenarArray(nodo.getIzquierdo(), arr, index);
        arr[index[0]++] = nodo.getDato().getName();
        llenarArray(nodo.getDerecho(), arr, index);
    }

    public Product obtenerProducto(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }

        NodoAvl nodo = raiz;
        while (nodo != null) {
            String actual = nodo.getDato().getName();
            int cmp = comparar(actual, nombre);
            if (cmp == 0) {
                return nodo.getDato();
            }
            nodo = cmp > 0 ? nodo.getIzquierdo() : nodo.getDerecho();
        }
        return null;
    }

    private NodoAvl insertarNodoRecursivo(NodoAvl nodo, Product valor) {
        if (nodo == null) {
            NodoAvl nuevoNodo = new NodoAvl(valor);
            actualizarFe(nuevoNodo);
            return nuevoNodo;
        }

        if (comparar(valor.getName(), nodo.getDato().getName()) > 0) {
            nodo.setDerecho(insertarNodoRecursivo(nodo.getDerecho(), valor));
        } else {
            nodo.setIzquierdo(insertarNodoRecursivo(nodo.getIzquierdo(), valor));
        }

        actualizarFe(nodo);
        if (Math.abs(nodo.getFe()) > 1) {
            nodo = evaluarRotacion(nodo);
        }
        return nodo;
    }

    private NodoAvl eliminarNodoRecursivo(NodoAvl nodo, Product valor) {
        if (nodo == null) {
            throw new IllegalArgumentException("no existe un producto con nombre: " + valor.getName());
        }

        int cmp = comparar(nodo.getDato().getName(), valor.getName());
        if (cmp > 0) {
            nodo.setIzquierdo(eliminarNodoRecursivo(nodo.getIzquierdo(), valor));
        } else if (cmp < 0) {
            nodo.setDerecho(eliminarNodoRecursivo(nodo.getDerecho(), valor));
        } else {
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            }
            if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }

            NodoAvl maximoIzq = buscarMaximo(nodo.getIzquierdo());
            nodo.setDato(maximoIzq.getDato());
            nodo.setIzquierdo(eliminarNodoRecursivo(nodo.getIzquierdo(), maximoIzq.getDato()));
        }

        actualizarFe(nodo);
        if (Math.abs(nodo.getFe()) > 1) {
            nodo = evaluarRotacion(nodo);
        }
        return nodo;
    }

    private NodoAvl buscarMaximo(NodoAvl nodo) {
        NodoAvl actual = nodo;
        while (actual != null && actual.getDerecho() != null) {
            actual = actual.getDerecho();
        }
        return actual;
    }

    private boolean buscarNodoRecursivo(NodoAvl nodo, String valor) {
        if (nodo == null) {
            return false;
        }

        int cmp = comparar(nodo.getDato().getName(), valor);
        if (cmp == 0) {
            return true;
        }
        if (cmp > 0) {
            return buscarNodoRecursivo(nodo.getIzquierdo(), valor);
        }
        return buscarNodoRecursivo(nodo.getDerecho(), valor);
    }

    private NodoAvl evaluarRotacion(NodoAvl nodo) {
        NodoAvl nodo2 = evaluarDesbalance(nodo);
        if (nodo2 == null) {
            return nodo;
        }

        if (nodo.getFe() > 1) {
            if (nodo2.getFe() >= 0) {
                return rotarI(nodo, nodo2);
            }
            return rotarDI(nodo, nodo2, nodo2.getIzquierdo());
        }

        if (nodo.getFe() < -1) {
            if (nodo2.getFe() <= 0) {
                return rotarD(nodo, nodo2);
            }
            return rotarID(nodo, nodo2, nodo2.getDerecho());
        }

        return nodo;
    }

    private NodoAvl evaluarDesbalance(NodoAvl nodo) {
        if (nodo.getFe() > 0) {
            return nodo.getDerecho();
        }
        if (nodo.getFe() < 0) {
            return nodo.getIzquierdo();
        }
        return null;
    }

    private NodoAvl rotarI(NodoAvl nodo1, NodoAvl nodo2) {
        NodoAvl temp = nodo2.getIzquierdo();
        nodo2.setIzquierdo(nodo1);
        nodo1.setDerecho(temp);
        actualizarFe(nodo1);
        actualizarFe(nodo2);
        return nodo2;
    }

    private NodoAvl rotarD(NodoAvl nodo1, NodoAvl nodo2) {
        NodoAvl temp = nodo2.getDerecho();
        nodo2.setDerecho(nodo1);
        nodo1.setIzquierdo(temp);
        actualizarFe(nodo1);
        actualizarFe(nodo2);
        return nodo2;
    }

    private NodoAvl rotarDI(NodoAvl nodo1, NodoAvl nodo2, NodoAvl nodo3) {
        if (nodo3 == null) {
            throw new IllegalStateException("rotacion DI invalida");
        }

        NodoAvl temp = nodo3.getIzquierdo();
        NodoAvl temp2 = nodo3.getDerecho();

        nodo3.setDerecho(nodo2);
        nodo3.setIzquierdo(nodo1);

        nodo1.setDerecho(temp);
        nodo2.setIzquierdo(temp2);

        actualizarFe(nodo1);
        actualizarFe(nodo2);
        actualizarFe(nodo3);

        return nodo3;
    }

    private NodoAvl rotarID(NodoAvl nodo1, NodoAvl nodo2, NodoAvl nodo3) {
        if (nodo3 == null) {
            throw new IllegalStateException("rotacion ID invalida");
        }

        NodoAvl temp = nodo3.getIzquierdo();
        NodoAvl temp2 = nodo3.getDerecho();

        nodo3.setIzquierdo(nodo2);
        nodo3.setDerecho(nodo1);

        nodo1.setIzquierdo(temp);
        nodo2.setDerecho(temp2);

        actualizarFe(nodo1);
        actualizarFe(nodo2);
        actualizarFe(nodo3);

        return nodo3;
    }

    private void actualizarFe(NodoAvl nodo) {
        if (nodo != null) {
            int alturaIzquierda = obtenerAltura(nodo.getIzquierdo());
            int alturaDerecha = obtenerAltura(nodo.getDerecho());
            nodo.setAltura(1 + Math.max(alturaIzquierda, alturaDerecha));
            nodo.setFe(alturaDerecha - alturaIzquierda);
        }
    }

    private int obtenerAltura(NodoAvl nodo) {
        return nodo == null ? 0 : nodo.getAltura();
    }

    private int comparar(String a, String b) {
        String sa = a == null ? "" : a.toLowerCase(Locale.ROOT);
        String sb = b == null ? "" : b.toLowerCase(Locale.ROOT);
        return sa.compareTo(sb);
    }

    private void validarProducto(Product valor, String operacion) {
        if (valor == null) {
            throw new IllegalArgumentException(operacion + ": dato nulo");
        }
        if (valor.getName() == null || valor.getName().isBlank()) {
            throw new IllegalArgumentException(operacion + ": nombre vacio");
        }
    }
}
