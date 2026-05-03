package com.supermarkets.api.products;

import com.supermarkets.api.GestorCentral;
import com.supermarkets.helpers.CSVLoader;
import com.supermarkets.helpers.CSVLoader.CsvType;
import com.supermarkets.helpers.LoadStats;
import com.supermarkets.pojo.ConexionSucursal;
import com.supermarkets.pojo.Product;
import com.supermarkets.pojo.Sucursal;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@WebServlet("/api/products/upload")
@MultipartConfig
public class UploadCsvServlet extends HttpServlet {
    private final GestorCentral gestor = GestorCentral.getInstancia();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        Part filePart = request.getPart("file");
        if (filePart == null || filePart.getSize() == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"Debes seleccionar un archivo CSV.\"}");
            return;
        }

        String csvType = request.getParameter("csvType");
        boolean hasHeader = Boolean.parseBoolean(request.getParameter("hasHeader"));

        if (csvType == null || csvType.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"Debes especificar el tipo de CSV (sucursales, conexiones, catalogo).\"}");
            return;
        }

        Path tempFile = Files.createTempFile("supermarkets-upload-", ".csv");

        try (InputStream inputStream = filePart.getInputStream()) {
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            CSVLoader loader = new CSVLoader();
            CsvType type = CsvType.fromValue(csvType);
            int successCount = 0;

            switch (type) {
                case SUCURSALES:
                    List<Sucursal> sucursales = loader.loadSucursales(tempFile, hasHeader);
                    gestor.cargarSucursales(sucursales);
                    successCount = sucursales.size();
                    break;
                case CONEXIONES:
                    List<ConexionSucursal> conexiones = loader.loadConexiones(tempFile, hasHeader);
                    gestor.cargarConexiones(conexiones);
                    successCount = conexiones.size();
                    break;
                case CATALOGO:
                    List<Product> products = loader.loadCatalogo(tempFile, hasHeader);
                    gestor.cargarProductos(products);
                    successCount = products.size();
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de CSV desconocido: " + csvType);
            }

            LoadStats stats = loader.getLoadStats();
            List<String> errorDetails = loader.getErrorDetails();
            String logPath = loader.getErrorLogPath();
            int totalErrors = stats.erroresLinea + stats.erroresDuplicados + stats.erroresFecha
                    + stats.erroresNumeros + stats.erroresOtros;

            String message = "Carga completada. Registros cargados: " + successCount
                    + ". Errores: " + totalErrors + ".";

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(buildJson(true, message, successCount, totalErrors, stats, errorDetails, logPath));

        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(buildJson(false, ex.getMessage(), 0, 0, null, null, null));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private String buildJson(boolean success, String message, int recordsLoaded, int totalErrors, LoadStats stats,
                             List<String> errorDetails, String logPath) {
        StringBuilder json = new StringBuilder();
        json.append('{');
        json.append("\"success\":").append(success).append(',');
        json.append("\"message\":\"").append(escape(message)).append("\",");
        json.append("\"recordsLoaded\":").append(recordsLoaded).append(',');
        json.append("\"totalErrors\":").append(totalErrors);

        if (logPath != null && !logPath.isEmpty()) {
            json.append(',');
            json.append("\"logPath\":\"").append(escape(logPath)).append("\"");
        }

        if (errorDetails != null && !errorDetails.isEmpty()) {
            json.append(',');
            json.append("\"errors\":[");
            for (int i = 0; i < errorDetails.size(); i++) {
                if (i > 0) {
                    json.append(',');
                }
                json.append("\"").append(escape(errorDetails.get(i))).append("\"");
            }
            json.append(']');
        }

        if (stats != null) {
            json.append(',');
            json.append("\"stats\":{");
            json.append("\"totalLineas\":").append(stats.totalLineas).append(',');
            json.append("\"registrosExitosos\":").append(stats.productosExitosos).append(',');
            json.append("\"erroresLinea\":").append(stats.erroresLinea).append(',');
            json.append("\"erroresDuplicados\":").append(stats.erroresDuplicados).append(',');
            json.append("\"erroresFecha\":").append(stats.erroresFecha).append(',');
            json.append("\"erroresNumeros\":").append(stats.erroresNumeros).append(',');
            json.append("\"erroresOtros\":").append(stats.erroresOtros);
            json.append('}');
        }

        json.append('}');
        return json.toString();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    escaped.append(c);
            }
        }
        return escaped.toString();
    }
}