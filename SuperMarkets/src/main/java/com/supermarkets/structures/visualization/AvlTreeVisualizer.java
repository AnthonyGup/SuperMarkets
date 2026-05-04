package com.supermarkets.structures.visualization;

import com.supermarkets.structures.avl.NodoAvl;
import com.supermarkets.utils.DotGenerator;

import java.util.HashMap;
import java.util.Map;

public class AvlTreeVisualizer {

    private int altura;
    private int totalNodos;

    public AvlTreeVisualizer() {}

    public String getDot(NodoAvl raiz) {
        if (raiz == null) {
            DotGenerator gen = new DotGenerator.Builder("arbol_avl")
                    .title("Arbol AVL (Por Nombre)")
                    .build();
            gen.addNode("empty", "Arbol Vacio", "ellipse", "lightgray");
            return gen.toDotString();
        }

        Map<NodoAvl, Integer> mapIds = new HashMap<>();
        asignarIdsRecursivo(raiz, mapIds);

        this.altura = calcularAltura(raiz);
        this.totalNodos = mapIds.size();

        DotGenerator gen = new DotGenerator.Builder("arbol_avl")
                .title("Arbol AVL (Por Nombre)")
                .build();

        agregarNodosAlGenerador(raiz, gen, mapIds);
        agregarAristasAlGenerador(raiz, gen, mapIds);

        return gen.toDotString();
    }

    private void asignarIdsRecursivo(NodoAvl nodo, Map<NodoAvl, Integer> mapIds) {
        if (nodo == null) return;
        int nuevoId = mapIds.size() + 1;
        mapIds.put(nodo, nuevoId);
        asignarIdsRecursivo(nodo.getIzquierdo(), mapIds);
        asignarIdsRecursivo(nodo.getDerecho(), mapIds);
    }

    private void agregarNodosAlGenerador(NodoAvl nodo, DotGenerator gen, Map<NodoAvl, Integer> mapIds) {
        if (nodo == null) return;

        int nodeId = mapIds.get(nodo);

        String nombre = nodo.getDato().getName();
        if (nombre == null) nombre = "null";
        if (nombre.length() > 15) nombre = nombre.substring(0, 12) + "...";

        String label = nombre + "\\nFE:" + nodo.getFe();

        String color = "lightblue";
        if (nodo.getFe() > 1 || nodo.getFe() < -1) {
            color = "lightcoral";
        } else if (nodo.getFe() != 0) {
            color = "lightyellow";
        }

        gen.addNode(String.valueOf(nodeId), label, "box", color);

        agregarNodosAlGenerador(nodo.getIzquierdo(), gen, mapIds);
        agregarNodosAlGenerador(nodo.getDerecho(), gen, mapIds);
    }

    private void agregarAristasAlGenerador(NodoAvl nodo, DotGenerator gen, Map<NodoAvl, Integer> mapIds) {
        if (nodo == null) return;

        int parentId = mapIds.get(nodo);

        if (nodo.getIzquierdo() != null) {
            int leftId = mapIds.get(nodo.getIzquierdo());
            gen.addEdge(String.valueOf(parentId), String.valueOf(leftId), "<-");
            agregarAristasAlGenerador(nodo.getIzquierdo(), gen, mapIds);
        }

        if (nodo.getDerecho() != null) {
            int rightId = mapIds.get(nodo.getDerecho());
            gen.addEdge(String.valueOf(parentId), String.valueOf(rightId), "->");
            agregarAristasAlGenerador(nodo.getDerecho(), gen, mapIds);
        }
    }

    private int calcularAltura(NodoAvl nodo) {
        if (nodo == null) return 0;
        return nodo.getAltura();
    }

    public int getAltura() {
        return altura;
    }

    public int getTotalNodos() {
        return totalNodos;
    }
}
