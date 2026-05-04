package com.supermarkets.structures.visualization;

import com.supermarkets.utils.DotGenerator;

public interface TreeDotGenerator {
    DotGenerator getDotGenerator(String titulo);
    int getAltura();
    int getTotalNodos();
}
