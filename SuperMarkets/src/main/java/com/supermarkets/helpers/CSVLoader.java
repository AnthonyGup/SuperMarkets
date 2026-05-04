package com.supermarkets.helpers;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.supermarkets.pojo.ConexionSucursal;
import com.supermarkets.pojo.Product;
import com.supermarkets.pojo.Sucursal;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final Set<String> processedBarcodes = new HashSet<>();
    private Path currentLogPath;

    public CSVLoader() {}

    public List<Sucursal> loadSucursales(Path filePath, boolean hasHeader) throws IOException {
        resetStats();
        initErrorLog(filePath);
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

                    if (sucursal.gettIngreso() <= 0 || sucursal.gettTraspaso() <= 0 || sucursal.gettDespacho() <= 0) {
                        registerError("Linea " + (i + 1) + ": Tiempos deben ser mayores a 0");
                        stats.erroresNumeros++;
                        continue;
                    }

                    sucursales.add(sucursal);
                    stats.productosExitosos++;
                } catch (NumberFormatException e) {
                    registerError("Linea " + (i + 1) + ": Formato numerico invalido - " + e.getMessage());
                    stats.erroresNumeros++;
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
        initErrorLog(filePath);
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

                    if (conexion.getTiempo() < 0 || conexion.getCosto() < 0) {
                        registerError("Linea " + (i + 1) + ": Tiempo/Costo no pueden ser negativos");
                        stats.erroresNumeros++;
                        continue;
                    }

                    conexiones.add(conexion);
                    stats.productosExitosos++;
                } catch (NumberFormatException e) {
                    registerError("Linea " + (i + 1) + ": Formato numerico invalido - " + e.getMessage());
                    stats.erroresNumeros++;
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
        processedBarcodes.clear();
        initErrorLog(filePath);
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
                    String barcode = trim(fields[2]);

                    if (barcode == null || barcode.isEmpty()) {
                        registerError("Linea " + (i + 1) + ": Codigo de barra vacio");
                        stats.erroresOtros++;
                        continue;
                    }

                    if (processedBarcodes.contains(barcode)) {
                        registerError("Linea " + (i + 1) + ": Codigo de barra duplicado '" + barcode + "'");
                        stats.erroresDuplicados++;
                        continue;
                    }

                    if (!isValidDate(trim(fields[4]))) {
                        registerError("Linea " + (i + 1) + ": Formato de fecha invalido '" + fields[4] + "' (usar YYYY-MM-DD)");
                        stats.erroresFecha++;
                        continue;
                    }

                    Product product = new Product();
                    product.setSucursalId(trim(fields[0]));
                    product.setName(trim(fields[1]));
                    product.setBarcode(barcode);
                    product.setCategory(trim(fields[3]));
                    product.setExpiryDate(trim(fields[4]));
                    product.setBrand(trim(fields[5]));
                    product.setPrice(parseDouble(trim(fields[6])));
                    product.setStock(parseInt(trim(fields[7])));

                    if (product.getPrice() < 0 || product.getStock() < 0) {
                        registerError("Linea " + (i + 1) + ": Precio/Stock no pueden ser negativos");
                        stats.erroresNumeros++;
                        continue;
                    }

                    processedBarcodes.add(barcode);
                    products.add(product);
                    stats.productosExitosos++;
                } catch (NumberFormatException e) {
                    registerError("Linea " + (i + 1) + ": Formato numerico invalido - " + e.getMessage());
                    stats.erroresNumeros++;
                } catch (Exception e) {
                    registerError("Linea " + (i + 1) + ": " + e.getMessage());
                    stats.erroresOtros++;
                }
            }
        }

        return products;
    }

    private void initErrorLog(Path sourceFile) throws IOException {
        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        Path logDir = projectRoot.resolve("logs");
        Files.createDirectories(logDir);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        this.currentLogPath = logDir.resolve("error_" + timestamp + ".log");

        Files.createFile(this.currentLogPath);
    }

    private void registerError(String message) {
        errorDetails.add(message);
        writeToErrorLog(message);
    }

    private void writeToErrorLog(String message) {
        if (currentLogPath == null) return;

        try (PrintWriter writer = new PrintWriter(new FileWriter(currentLogPath.toFile(), true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.println("[" + timestamp + "] " + message);
        } catch (IOException e) {
            System.err.println("No se pudo escribir al log de errores: " + e.getMessage());
        }
    }

    private boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        try {
            String[] parts = dateStr.split("-");
            if (parts.length != 3) return false;

            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            if (year < 1900 || year > 2100) return false;
            if (month < 1 || month > 12) return false;
            if (day < 1 || day > 31) return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public LoadStats getLoadStats() {
        return new LoadStats(stats);
    }

    public List<String> getErrorDetails() {
        return new ArrayList<>(errorDetails);
    }

    public String getErrorLogPath() {
        return currentLogPath != null ? currentLogPath.toString() : null;
    }

    private void resetStats() {
        errorDetails.clear();
        processedBarcodes.clear();
        stats.totalLineas = 0;
        stats.productosExitosos = 0;
        stats.erroresLinea = 0;
        stats.erroresDuplicados = 0;
        stats.erroresFecha = 0;
        stats.erroresNumeros = 0;
        stats.erroresOtros = 0;
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