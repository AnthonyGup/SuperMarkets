package com.supermarkets.structures.visualization;

import com.supermarkets.structures.hash.NodoHash;
import com.supermarkets.structures.hash.TablaHash;
import com.supermarkets.utils.DotGenerator;

import java.lang.reflect.Field;

public class HashTableVisualizer {

    private int capacidad;
    private int elementos;
    private double factorCarga;

    public HashTableVisualizer() {}

    public String getDot(TablaHash hash) {
        this.capacidad = 16;
        this.elementos = hash.getSize();
        this.factorCarga = (double) elementos / capacidad;

        NodoHash[] buckets = getBuckets(hash);

        DotGenerator gen = new DotGenerator.Builder("hash_table")
                .title("Tabla Hash (Cap: " + capacidad + ", Elem: " + elementos + ", Carga: " + String.format("%.1f", factorCarga * 100) + "%)")
                .build();

        gen.addCustom("    rankdir=LR;\n");
        gen.addCustom("    node [shape=box, style=filled, fontname=\"Arial\", fontsize=10];\n");
        gen.addCustom("    edge [fontname=\"Arial\", fontsize=8];\n");

        for (int i = 0; i < buckets.length; i++) {
            String bucketId = "bucket_" + i;

            if (buckets[i] == null) {
                gen.addNode(bucketId, "[ " + i + " ]", "box", "lightgray");
            } else {
                StringBuilder label = new StringBuilder("[ " + i + " ]\n");
                NodoHash actual = buckets[i];
                int count = 0;
                while (actual != null) {
                    String clave = actual.getClave();
                    if (clave != null && clave.length() > 12) {
                        clave = clave.substring(0, 10) + "..";
                    }
                    label.append(clave != null ? clave : "null");
                    if (actual.getSiguiente() != null) {
                        label.append("\n        -> ");
                    }
                    actual = actual.getSiguiente();
                    count++;
                }
                String color = count > 1 ? "orange" : "lightblue";
                gen.addNode(bucketId, label.toString(), "box", color);
            }
        }

        return gen.toDotString();
    }

    private NodoHash[] getBuckets(TablaHash hash) {
        try {
            Field field = TablaHash.class.getDeclaredField("buckets");
            field.setAccessible(true);
            return (NodoHash[]) field.get(hash);
        } catch (Exception e) {
            return new NodoHash[16];
        }
    }

    public int getCapacidad() {
        return capacidad;
    }

    public int getElementos() {
        return elementos;
    }

    public double getFactorCarga() {
        return factorCarga;
    }
}
