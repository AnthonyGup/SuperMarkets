package com.supermarkets.helpers;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.supermarkets.pojo.ConexionSucursal;
import com.supermarkets.pojo.Product;
import com.supermarkets.pojo.Sucursal;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {

    public enum CsvType {
        SUCURSALES("sucursales"),
        CONEXIONES("conexiones"),
        CATALOGO("catalogo");

        private final String value;

        CsvType(String value) {
            this.value = value;
        }

        public static CsvType fromValue(String value) {
            if (value == null) {
                return null;
            }
            for (CsvType type : values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return null;
        }
    }

    private final LoadStats stats = new LoadStats();
    private final List<String> errorDetails = new ArrayList<>();

    public CSVLoader() {}

    public List<Sucursal> loadSucursales(Path filePath, boolean hasHeader) throws IOException {
        resetStats();
        List<Sucursal> sucursales = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
            List<String[]> allLines;
            try {
                allLines = reader.readAll();
            } catch (CsvException e) {
                throw new IOException("Error al leer el archivo CSV", e);
            }

            int startIndex = hasHeader ? 1 : 0;
            for (int i = startIndex; i < allLines.size(); i++) {
                String[] fields = allLines.get(i);
                stats.totalLineas++;

                if (fields == null || fields.length < 6) {
                    registerError("Linea " + (i + 1) + ": Campos insuficientes. Se esperaban 6, se encontraron " + (fields != null ? fields.length : 0));
                    stats.erroresLinea++;
                    continue;
                }

                try {
                    Sucursal sucursal = new Sucursal();
                    sucursal.setId(trim(fields[0]));
                    sucursal.setNombre(trim(fields[1]));
                    sucursal.setUbicacion(trim(fields[2]));
                    sucursal.settIngreso(parseDouble(trim(fields[3])));
                    sucursal.settTraspaso(parseDouble(trim(fields[4])));
                    sucursal.settDespacho(parseDouble(trim(fields[5])));

                    if (sucursal.getId() == null || sucursal.getId().isEmpty()) {
                        registerError("Linea " + (i + 1) + ": ID de sucursal vacio");
                        stats.erroresOtros++;
                        continue;
                    }

                    sucursales.add(sucursal);
                    stats.productosExitosos++;
                } catch (Exception e) {
                    registerError("Linea " + (i + 1) + ": " + e.getMessage());
                    stats.erroresOtros++;
                }
            }
        }

        return sucursales;
    }

    public List<ConexionSucursal> loadConexiones(Path filePath, boolean hasHeader) throws IOException {
        resetStats();
        List<ConexionSucursal> conexiones = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
            List<String[]> allLines;
            try {
                allLines = reader.readAll();
            } catch (CsvException e) {
                throw new IOException("Error al leer el archivo CSV", e);
            }

            int startIndex = hasHeader ? 1 : 0;
            for (int i = startIndex; i < allLines.size(); i++) {
                String[] fields = allLines.get(i);
                stats.totalLineas++;

                if (fields == null || fields.length < 4) {
                    registerError("Linea " + (i + 1) + ": Campos insuficientes. Se esperaban 4, se encontraron " + (fields != null ? fields.length : 0));
                    stats.erroresLinea++;
                    continue;
                }

                try {
                    ConexionSucursal conexion = new ConexionSucursal();
                    conexion.setOrigenId(trim(fields[0]));
                    conexion.setDestinoId(trim(fields[1]));
                    conexion.setTiempo(parseDouble(trim(fields[2])));
                    conexion.setCosto(parseDouble(trim(fields[3])));

                    if (conexion.getOrigenId() == null || conexion.getOrigenId().isEmpty()) {
                        registerError("Linea " + (i + 1) + ": OrigenID vacio");
                        stats.erroresOtros++;
                        continue;
                    }

                    if (conexion.getDestinoId() == null || conexion.getDestinoId().isEmpty()) {
                        registerError("Linea " + (i + 1) + ": DestinoID vacio");
                        stats.erroresOtros++;
                        continue;
                    }

                    conexiones.add(conexion);
                    stats.productosExitosos++;
                } catch (Exception e) {
                    registerError("Linea " + (i + 1) + ": " + e.getMessage());
                    stats.erroresOtros++;
                }
            }
        }

        return conexiones;
    }

    public List<Product> loadCatalogo(Path filePath, boolean hasHeader) throws IOException {
        resetStats();
        List<Product> products = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
            List<String[]> allLines;
            try {
                allLines = reader.readAll();
            } catch (CsvException e) {
                throw new IOException("Error al leer el archivo CSV", e);
            }

            int startIndex = hasHeader ? 1 : 0;
            for (int i = startIndex; i < allLines.size(); i++) {
                String[] fields = allLines.get(i);
                stats.totalLineas++;

                if (fields == null || fields.length < 8) {
                    registerError("Linea " + (i + 1) + ": Campos insuficientes. Se esperaban 8, se encontraron " + (fields != null ? fields.length : 0));
                    stats.erroresLinea++;
                    continue;
                }

                try {
                    Product product = new Product();
                    product.setSucursalId(trim(fields[0]));
                    product.setName(trim(fields[1]));
                    product.setBarcode(trim(fields[2]));
                    product.setCategory(trim(fields[3]));
                    product.setExpiryDate(trim(fields[4]));
                    product.setBrand(trim(fields[5]));
                    product.setPrice(parseDouble(trim(fields[6])));
                    product.setStock(parseInt(trim(fields[7])));

                    if (product.getBarcode() == null || product.getBarcode().isEmpty()) {
                        registerError("Linea " + (i + 1) + ": Codigo de barra vacio");
                        stats.erroresOtros++;
                        continue;
                    }

                    products.add(product);
                    stats.productosExitosos++;
                } catch (Exception e) {
                    registerError("Linea " + (i + 1) + ": " + e.getMessage());
                    stats.erroresOtros++;
                }
            }
        }

        return products;
    }

    public LoadStats getLoadStats() {
        return new LoadStats(stats);
    }

    public List<String> getErrorDetails() {
        return new ArrayList<>(errorDetails);
    }

    private void resetStats() {
        errorDetails.clear();
        stats.totalLineas = 0;
        stats.productosExitosos = 0;
        stats.erroresLinea = 0;
        stats.erroresDuplicados = 0;
        stats.erroresFecha = 0;
        stats.erroresNumeros = 0;
        stats.erroresOtros = 0;
    }

    private void registerError(String message) {
        errorDetails.add(message);
    }

    private String trim(String str) {
        return str == null ? "" : str.trim();
    }

    private double parseDouble(String str) {
        if (str == null || str.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Valor double invalido: " + str);
        }
    }

    private int parseInt(String str) {
        if (str == null || str.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Valor entero invalido: " + str);
        }
    }
}