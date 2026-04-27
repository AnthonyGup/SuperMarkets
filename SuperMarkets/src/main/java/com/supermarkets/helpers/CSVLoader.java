package com.supermarkets.helpers;

import com.supermarkets.pojo.Product;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVLoader {
    private final String delimiter;

    private String[] loadedBarcodes;
    private int loadedBarcodesCount;

    private String[] errorDetails;
    private int errorDetailsCount;

    private LoadStats stats;

    public CSVLoader() {
        this(null, ",");
    }

    public CSVLoader(String logFilePath, String delimiter) {
        this.delimiter = (delimiter == null || delimiter.isEmpty()) ? "," : delimiter;
        this.loadedBarcodes = new String[64];
        this.loadedBarcodesCount = 0;
        this.errorDetails = new String[64];
        this.errorDetailsCount = 0;
        this.stats = new LoadStats();
    }

    public Product[] loadProducts(String filename, boolean hasHeader) {
        Product[] products = new Product[64];
        int productsCount = 0;

        stats = new LoadStats();
        loadedBarcodesCount = 0;
        errorDetailsCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int lineNumber = 0;
            String line;

            if (hasHeader) {
                line = reader.readLine();
                if (line != null) {
                    lineNumber++;
                }
            }

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (trim(line).isEmpty()) {
                    continue;
                }

                stats.totalLineas++;

                String[] fields = splitLineWithQuotes(line, delimiter.charAt(0));
                if (fields.length < 7) {
                    registerError("Numero insuficiente de campos. Se esperaban 7, se encontraron " + fields.length, lineNumber);
                    stats.erroresLinea++;
                    continue;
                }

                try {
                    String nombre = removeQuotes(fields[0]);
                    String codigoBarra = removeQuotes(fields[1]);
                    String categoria = removeQuotes(fields[2]);
                    String fechaCaducidad = removeQuotes(fields[3]);
                    String marca = removeQuotes(fields[4]);
                    String precioStr = removeQuotes(fields[5]);
                    String stockStr = removeQuotes(fields[6]);

                    if (nombre.isEmpty()) {
                        registerError("Campo 'Nombre' vacio", lineNumber);
                        stats.erroresOtros++;
                        continue;
                    }

                    if (codigoBarra.isEmpty()) {
                        registerError("Campo 'CodigoBarra' vacio", lineNumber);
                        stats.erroresOtros++;
                        continue;
                    }

                    if (!isValidISODate(fechaCaducidad)) {
                        registerError("Fecha de caducidad invalida: '" + fechaCaducidad + "'. Formato esperado: YYYY-MM-DD", lineNumber);
                        stats.erroresFecha++;
                        continue;
                    }

                    if (!isValidDouble(precioStr)) {
                        registerError("Precio invalido: '" + precioStr + "' no es un numero valido", lineNumber);
                        stats.erroresNumeros++;
                        continue;
                    }

                    if (!isValidInteger(stockStr)) {
                        registerError("Stock invalido: '" + stockStr + "' no es un numero entero valido", lineNumber);
                        stats.erroresNumeros++;
                        continue;
                    }

                    if (!isUniqueBarcode(codigoBarra)) {
                        registerError("Codigo de barra duplicado: '" + codigoBarra + "'. Producto omitido.", lineNumber);
                        stats.erroresDuplicados++;
                        continue;
                    }

                    Product product = new Product();
                    product.setName(nombre);
                    product.setBarcode(codigoBarra);
                    product.setCategory(categoria);
                    product.setExpiryDate(fechaCaducidad);
                    product.setBrand(marca);
                    product.setPrice(Double.parseDouble(precioStr));
                    product.setStock(Integer.parseInt(stockStr));

                    if (productsCount == products.length) {
                        products = growProductArray(products);
                    }
                    products[productsCount++] = product;
                    stats.productosExitosos++;
                } catch (Exception e) {
                    registerError("Excepcion durante procesamiento: " + e.getMessage(), lineNumber);
                    stats.erroresOtros++;
                }
            }
        } catch (IOException e) {
            String errorMsg = "ERROR CRITICO: No se pudo abrir o leer el archivo: " + filename;
            registerError(errorMsg, -1);
            throw new RuntimeException(errorMsg, e);
        }

        Product[] result = new Product[productsCount];
        for (int i = 0; i < productsCount; i++) {
            result[i] = products[i];
        }
        return result;
    }

    public Product[] loadProducts(String filename) {
        return loadProducts(filename, true);
    }

    public LoadStats getLoadStats() {
        return new LoadStats(stats);
    }

    public String[] getErrorDetails() {
        String[] result = new String[errorDetailsCount];
        for (int i = 0; i < errorDetailsCount; i++) {
            result[i] = errorDetails[i];
        }
        return result;
    }

    private void registerError(String message, int lineNumber) {
        String entry;
        if (lineNumber > 0) {
            entry = "[Linea " + lineNumber + "] " + message;
        } else {
            entry = message;
        }

        if (errorDetailsCount == errorDetails.length) {
            errorDetails = growStringArray(errorDetails);
        }
        errorDetails[errorDetailsCount++] = entry;
    }

    private String[] splitLineWithQuotes(String line, char delimiterChar) {
        String[] tokens = new String[8];
        int count = 0;

        StringBuilder token = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                insideQuotes = !insideQuotes;
                token.append(c);
            } else if (c == delimiterChar && !insideQuotes) {
                if (count == tokens.length) {
                    tokens = growStringArray(tokens);
                }
                tokens[count++] = token.toString();
                token.setLength(0);
            } else {
                token.append(c);
            }
        }

        if (count == tokens.length) {
            tokens = growStringArray(tokens);
        }
        tokens[count++] = token.toString();

        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = tokens[i];
        }
        return result;
    }

    private String trim(String str) {
        return str == null ? "" : str.trim();
    }

    private String removeQuotes(String str) {
        String trimmed = trim(str);
        if (trimmed.length() >= 2 && trimmed.charAt(0) == '"' && trimmed.charAt(trimmed.length() - 1) == '"') {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed;
    }

    private boolean isValidISODate(String date) {
        if (date == null || date.length() != 10) {
            return false;
        }

        if (date.charAt(4) != '-' || date.charAt(7) != '-') {
            return false;
        }

        for (int i = 0; i < date.length(); i++) {
            if (i == 4 || i == 7) {
                continue;
            }
            if (!Character.isDigit(date.charAt(i))) {
                return false;
            }
        }

        try {
            int month = Integer.parseInt(date.substring(5, 7));
            int day = Integer.parseInt(date.substring(8, 10));
            if (month < 1 || month > 12) {
                return false;
            }
            if (day < 1 || day > 31) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isUniqueBarcode(String barcode) {
        for (int i = 0; i < loadedBarcodesCount; i++) {
            if (barcode.equals(loadedBarcodes[i])) {
                return false;
            }
        }

        if (loadedBarcodesCount == loadedBarcodes.length) {
            loadedBarcodes = growStringArray(loadedBarcodes);
        }
        loadedBarcodes[loadedBarcodesCount++] = barcode;
        return true;
    }

    private String[] growStringArray(String[] source) {
        String[] grown = new String[source.length * 2];
        for (int i = 0; i < source.length; i++) {
            grown[i] = source[i];
        }
        return grown;
    }

    private Product[] growProductArray(Product[] source) {
        Product[] grown = new Product[source.length * 2];
        for (int i = 0; i < source.length; i++) {
            grown[i] = source[i];
        }
        return grown;
    }
}
