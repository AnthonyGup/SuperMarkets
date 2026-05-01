package com.supermarkets.structures.grafo;

import com.supermarkets.pojo.Sucursal;
import java.util.ArrayList;
import java.util.List;

public class Grafo {
    private NodoGrafo primero;
    private NodoGrafo ultimo;
    private int tamanio;

    public Grafo() {
        this.primero = null;
        this.ultimo = null;
        this.tamanio = 0;
    }

    public boolean grafoVacio() {
        return this.primero == null;
    }

    public int contarVertices() {
        int count = 0;
        NodoGrafo temp = primero;
        while (temp != null) {
            count++;
            temp = temp.getSiguiente();
        }
        return count;
    }

    public void nuevoNodo(String id, Sucursal dato) {
        if (existeVertice(id)) {
            return;
        }

        NodoGrafo nodo = new NodoGrafo(id, dato);
        if (grafoVacio()) {
            this.primero = nodo;
            this.ultimo = nodo;
        } else {
            this.ultimo.setSiguiente(nodo);
            this.ultimo = nodo;
        }
        tamanio++;
    }

    public boolean existeVertice(String id) {
        NodoGrafo temp = primero;
        while (temp != null) {
            if (temp.getId().equals(id)) {
                return true;
            }
            temp = temp.getSiguiente();
        }
        return false;
    }

    public NodoGrafo getVertice(String id) {
        NodoGrafo temp = primero;
        while (temp != null) {
            if (temp.getId().equals(id)) {
                return temp;
            }
            temp = temp.getSiguiente();
        }
        return null;
    }

    public void nuevaArista(String origenId, String destinoId, float peso) {
        if (existeVertice(origenId) && existeVertice(destinoId)) {
            NodoGrafo nodo = getVertice(origenId);
            nodo.getLista().insertar(destinoId, peso);
        }
    }

    public void aristaBidireccional(String id1, String id2, float peso) {
        nuevaArista(id1, id2, peso);
        nuevaArista(id2, id1, peso);
    }

    public boolean existeArista(String origenId, String destinoId) {
        NodoGrafo nodo = getVertice(origenId);
        if (nodo != null) {
            return nodo.getLista().buscar(destinoId);
        }
        return false;
    }

    public List<String> dijkstra(String etiquetaOrigen, String etiquetaDestino) {
        if (!existeVertice(etiquetaOrigen) || !existeVertice(etiquetaDestino)) {
            return null;
        }

        int n = contarVertices();
        String[] vertices = getVerticesIds();

        float[] distancias = new float[n];
        boolean[] visitado = new boolean[n];
        int[] anterior = new int[n];
        int idxOrigen = getIndice(vertices, etiquetaOrigen);
        int idxDestino = getIndice(vertices, etiquetaDestino);

        for (int i = 0; i < n; i++) {
            distancias[i] = Float.MAX_VALUE;
            anterior[i] = -1;
            visitado[i] = false;
        }
        distancias[idxOrigen] = 0;

        for (int iteracion = 0; iteracion < n; iteracion++) {
            int distanciaMinima = Integer.MAX_VALUE;
            int u = -1;

            for (int i = 0; i < n; i++) {
                if (!visitado[i] && distancias[i] < distanciaMinima) {
                    distanciaMinima = (int) distancias[i];
                    u = i;
                }
            }

            if (u == -1) {
                break;
            }

            visitado[u] = true;

            if (u == idxDestino) {
                break;
            }

            NodoGrafo nodoU = getVertice(vertices[u]);
            Arco arco = nodoU.getLista().getPrimero();

            while (arco != null) {
                String vId = arco.getDestinoId();
                int v = getIndice(vertices, vId);

                boolean verticeLibre = !visitado[v];
                boolean origenAlcanzado = distancias[u] != Float.MAX_VALUE;

                if (verticeLibre && origenAlcanzado) {
                    float distanciaAlternativa = distancias[u] + arco.getPeso();
                    if (distanciaAlternativa < distancias[v]) {
                        distancias[v] = distanciaAlternativa;
                        anterior[v] = u;
                    }
                }
                arco = arco.getSiguiente();
            }
        }

        if (distancias[idxDestino] == Float.MAX_VALUE) {
            return null;
        }

        List<String> camino = new ArrayList<>();
        int actual = idxDestino;
        while (actual != -1) {
            camino.add(0, vertices[actual]);
            actual = anterior[actual];
        }

        return camino;
    }

    public List<String> bfs(String inicioId) {
        List<String> resultado = new ArrayList<>();
        if (!existeVertice(inicioId)) {
            return resultado;
        }

        String[] vertices = getVerticesIds();
        int n = vertices.length;
        boolean[] visitado = new boolean[n];
        List<String> cola = new ArrayList<>();

        int inicioIdx = getIndice(vertices, inicioId);
        cola.add(inicioId);
        visitado[inicioIdx] = true;

        while (!cola.isEmpty()) {
            String actualId = cola.remove(0);
            resultado.add(actualId);

            NodoGrafo nodo = getVertice(actualId);
            Arco arco = nodo.getLista().getPrimero();

            while (arco != null) {
                String vId = arco.getDestinoId();
                int vIdx = getIndice(vertices, vId);

                if (!visitado[vIdx]) {
                    visitado[vIdx] = true;
                    cola.add(vId);
                }
                arco = arco.getSiguiente();
            }
        }

        return resultado;
    }

    public List<String> dfs(String inicioId) {
        List<String> resultado = new ArrayList<>();
        if (!existeVertice(inicioId)) {
            return resultado;
        }

        String[] vertices = getVerticesIds();
        int n = vertices.length;
        boolean[] visitado = new boolean[n];
        int inicioIdx = getIndice(vertices, inicioId);

        dfsRec(vertices, inicioIdx, visitado, resultado);

        return resultado;
    }

    private void dfsRec(String[] vertices, int idx, boolean[] visitado, List<String> resultado) {
        visitado[idx] = true;
        resultado.add(vertices[idx]);

        NodoGrafo nodo = getVertice(vertices[idx]);
        Arco arco = nodo.getLista().getPrimero();

        while (arco != null) {
            String vId = arco.getDestinoId();
            int vIdx = getIndice(vertices, vId);

            if (!visitado[vIdx]) {
                dfsRec(vertices, vIdx, visitado, resultado);
            }
            arco = arco.getSiguiente();
        }
    }

    public List<NodoGrafo> getVertices() {
        List<NodoGrafo> lista = new ArrayList<>();
        NodoGrafo temp = primero;
        while (temp != null) {
            lista.add(temp);
            temp = temp.getSiguiente();
        }
        return lista;
    }

    public float getDistancia(String origenId, String destinoId) {
        NodoGrafo nodo = getVertice(origenId);
        if (nodo != null) {
            return nodo.getLista().getPeso(destinoId);
        }
        return 0;
    }

    private String[] getVerticesIds() {
        int n = contarVertices();
        String[] ids = new String[n];
        NodoGrafo temp = primero;
        int i = 0;
        while (temp != null) {
            ids[i++] = temp.getId();
            temp = temp.getSiguiente();
        }
        return ids;
    }

    private int getIndice(String[] array, String valor) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(valor)) {
                return i;
            }
        }
        return -1;
    }
}