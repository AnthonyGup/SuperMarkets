package com.supermarkets.structures.visualization;

import com.supermarkets.pojo.Product;
import com.supermarkets.structures.b.NodoB;
import com.supermarkets.utils.DotGenerator;

import java.util.HashMap;
import java.util.Map;

public class BTreeVisualizer {

    private int altura;
    private int totalNodos;

    public BTreeVisualizer() {}

    public String getDot(NodoB raiz) {
        if (raiz == null) {
            DotGenerator gen = new DotGenerator.Builder("arbol_b")
                    .title("Arbol B (Por Fecha de Vencimiento)")
                    .build();
            gen.addNode("empty", "Arbol Vacio", "ellipse", "lightgray");
            return gen.toDotString();
        }

        Map<NodoB, Integer> mapIds = new HashMap<>();
        asignarIdsRecursivo(raiz, mapIds, 0);

        this.altura = calcularAltura(raiz);
        this.totalNodos = mapIds.size();

        DotGenerator gen = new DotGenerator.Builder("arbol_b")
                .title("Arbol B (Por Fecha de Vencimiento)")
                .build();

        agregarNodosAlGenerador(raiz, gen, mapIds);
        agregarAristasAlGenerador(raiz, gen, mapIds);

        return gen.toDotString();
    }

    private int asignarIdsRecursivo(NodoB nodo, Map<NodoB, Integer> mapIds, int nivel) {
        if (nodo == null) return 0;
        int count = 0;
        if (!mapIds.containsKey(nodo)) {
            mapIds.put(nodo, mapIds.size() + 1);
            count = 1;
        }
        if (!nodo.esHoja()) {
            for (int i = 0; i <= nodo.Ocuenta(); i++) {
                NodoB hijo = nodo.Orama(i);
                if (hijo != null) {
                    count += asignarIdsRecursivo(hijo, mapIds, nivel + 1);
                }
            }
        }
        return count;
    }

    private void agregarNodosAlGenerador(NodoB nodo, DotGenerator gen, Map<NodoB, Integer> mapIds) {
        if (nodo == null) return;

        int numClaves = nodo.Ocuenta();
        if (numClaves == 0) return;

        StringBuilder label = new StringBuilder();
        for (int i = 0; i < numClaves; i++) {
            if (i > 0) label.append(" | ");
            Product prod = nodo.Oclave(i + 1);
            if (prod != null) {
                String fecha = prod.getExpiryDate();
                if (fecha != null) {
                    label.append(fecha);
                } else {
                    label.append("-");
                }
            } else {
                label.append("-");
            }
        }

        String color = nodo.esHoja() ? "lightgreen" : "lightblue";

        int primerId = mapIds.get(nodo);
        gen.addNode(String.valueOf(primerId), label.toString(), "box", color);

        if (!nodo.esHoja()) {
            for (int i = 0; i <= nodo.Ocuenta(); i++) {
                NodoB hijo = nodo.Orama(i);
                if (hijo != null) {
                    agregarNodosAlGenerador(hijo, gen, mapIds);
                }
            }
        }
    }

    private void agregarAristasAlGenerador(NodoB nodo, DotGenerator gen, Map<NodoB, Integer> mapIds) {
        if (nodo == null || nodo.esHoja()) return;

        int padreId = mapIds.get(nodo);

        for (int i = 0; i <= nodo.Ocuenta(); i++) {
            NodoB hijo = nodo.Orama(i);
            if (hijo != null) {
                int hijoId = mapIds.get(hijo);
                gen.addEdge(String.valueOf(padreId), String.valueOf(hijoId), String.valueOf(i));
                agregarAristasAlGenerador(hijo, gen, mapIds);
            }
        }
    }

    private int calcularAltura(NodoB nodo) {
        if (nodo == null) return 0;
        if (nodo.esHoja()) return 1;
        return 1 + calcularAltura(nodo.Orama(0));
    }

    public int getAltura() {
        return altura;
    }

    public int getTotalNodos() {
        return totalNodos;
    }
}
