package com.supermarkets.structures.visualization;

import com.supermarkets.structures.bplus.NodoBPlus;
import com.supermarkets.utils.DotGenerator;

import java.util.HashMap;
import java.util.Map;

public class BPlusTreeVisualizer {

    private int altura;
    private int totalNodos;

    public BPlusTreeVisualizer() {}

    public String getDot(NodoBPlus raiz) {
        if (raiz == null) {
            DotGenerator gen = new DotGenerator.Builder("arbol_bplus")
                    .title("Arbol B+ (Por Categoria)")
                    .build();
            gen.addNode("empty", "Arbol Vacio", "ellipse", "lightgray");
            return gen.toDotString();
        }

        try {
            Map<NodoBPlus, Integer> mapIds = new HashMap<>();
            asignarIdsRecursivo(raiz, mapIds, 0);

            this.altura = calcularAltura(raiz);
            this.totalNodos = mapIds.size();

            DotGenerator gen = new DotGenerator.Builder("arbol_bplus")
                    .title("Arbol B+ (Por Categoria)")
                    .build();

            agregarNodosAlGenerador(raiz, gen, mapIds);
            agregarAristasAlGenerador(raiz, gen, mapIds);

            return gen.toDotString();
        } catch (Exception e) {
            e.printStackTrace();
            DotGenerator gen = new DotGenerator.Builder("arbol_bplus")
                    .title("Error en Arbol B+")
                    .build();
            gen.addNode("error", "Error: " + e.getMessage(), "ellipse", "lightcoral");
            return gen.toDotString();
        }
    }

    private void asignarIdsRecursivo(NodoBPlus nodo, Map<NodoBPlus, Integer> mapIds, int nivel) {
        if (nodo == null) return;
        if (mapIds.containsKey(nodo)) return;

        mapIds.put(nodo, mapIds.size() + 1);

        if (!nodo.esHoja()) {
            for (int i = 0; i <= nodo.Ocuenta(); i++) {
                try {
                    NodoBPlus hijo = nodo.Orama(i);
                    if (hijo != null) {
                        asignarIdsRecursivo(hijo, mapIds, nivel + 1);
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }

    private void agregarNodosAlGenerador(NodoBPlus nodo, DotGenerator gen, Map<NodoBPlus, Integer> mapIds) {
        if (nodo == null) return;
        if (!mapIds.containsKey(nodo)) return;

        int numClaves = nodo.Ocuenta();
        if (numClaves == 0) return;

        int nodeId = mapIds.get(nodo);

        StringBuilder label = new StringBuilder();
        for (int i = 0; i < numClaves; i++) {
            if (i > 0) label.append(" | ");
            try {
                String clave = nodo.Oclave(i);
                if (clave != null) {
                    if (clave.length() > 10) {
                        clave = clave.substring(0, 7) + "...";
                    }
                    label.append(clave);
                }
            } catch (Exception e) {
                label.append("?");
            }
        }

        String color = nodo.esHoja() ? "lightgreen" : "lightblue";

        gen.addNode(String.valueOf(nodeId), label.toString(), "box", color);

        if (!nodo.esHoja()) {
            for (int i = 0; i <= nodo.Ocuenta(); i++) {
                try {
                    NodoBPlus hijo = nodo.Orama(i);
                    if (hijo != null) {
                        agregarNodosAlGenerador(hijo, gen, mapIds);
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }

    private void agregarAristasAlGenerador(NodoBPlus nodo, DotGenerator gen, Map<NodoBPlus, Integer> mapIds) {
        if (nodo == null || nodo.esHoja()) return;
        if (!mapIds.containsKey(nodo)) return;

        int padreId = mapIds.get(nodo);

        for (int i = 0; i <= nodo.Ocuenta(); i++) {
            try {
                NodoBPlus hijo = nodo.Orama(i);
                if (hijo != null && mapIds.containsKey(hijo)) {
                    int hijoId = mapIds.get(hijo);
                    gen.addEdge(String.valueOf(padreId), String.valueOf(hijoId), String.valueOf(i));
                    agregarAristasAlGenerador(hijo, gen, mapIds);
                }
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    private int calcularAltura(NodoBPlus nodo) {
        if (nodo == null) return 0;
        try {
            if (nodo.esHoja()) return 1;
            return 1 + calcularAltura(nodo.Orama(0));
        } catch (Exception e) {
            return 1;
        }
    }

    public int getAltura() {
        return altura;
    }

    public int getTotalNodos() {
        return totalNodos;
    }
}
