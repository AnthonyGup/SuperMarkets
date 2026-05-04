package com.supermarkets.api.medicion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.supermarkets.api.GestorCentral;
import com.supermarkets.dto.ApiResponse;
import com.supermarkets.dto.ResultadoMedicion;
import com.supermarkets.pojo.Product;
import com.supermarkets.pojo.Sucursal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@WebServlet("/api/medicion/*")
public class MedicionServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final GestorCentral gestor = GestorCentral.getInstancia();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        long startTime = System.currentTimeMillis();

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo != null && pathInfo.endsWith("/comparar")) {
                compararBusquedas(request, response, startTime);
                return;
            }

            List<String> info = new ArrayList<>();
            info.add("=== MEDICION DE BUSQUEDA ===");
            info.add("GET /api/medicion/comparar?sucursal={id}&nombre={nombre}&barcode={barcode}&iteraciones={n}");
            info.add("Compara: Lista vs AVL vs Hash");
            info.add("- nombre: para buscar por Lista y AVL");
            info.add("- barcode: para buscar por Hash");
            info.add("- iteraciones: numero de repeticiones (default 1)");
            info.add("");
            info.add("Complejidad:");
            info.add("- Lista (secuencial): O(n)");
            info.add("- AVL (balanceado): O(log n)");
            info.add("- Hash: O(1) promedio");

            ApiResponse<List<String>> apiResponse = ApiResponse.success(info);
            apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.getWriter().write(gson.toJson(apiResponse));

        } catch (Exception e) {
            ApiResponse<Object> errorResponse = ApiResponse.error("Error: " + e.getMessage());
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }

    private void compararBusquedas(HttpServletRequest request, HttpServletResponse response, long startTime) throws IOException {
        String sucursalId = request.getParameter("sucursal");
        String nombre = request.getParameter("nombre");
        String barcode = request.getParameter("barcode");

        if (sucursalId == null) {
            sendError(response, "Parametro requerido: sucursal", startTime, HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Sucursal sucursal = gestor.getSucursal(sucursalId);
        if (sucursal == null) {
            sendError(response, "Sucursal no encontrada: " + sucursalId, startTime, HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int iteraciones = 1;
        String iteracionesStr = request.getParameter("iteraciones");
        if (iteracionesStr != null) {
            try {
                iteraciones = Math.max(1, Integer.parseInt(iteracionesStr));
            } catch (NumberFormatException ignored) {}
        }

        Product[] productos = sucursal.getInventarioLista().listarTodos();
        int totalProductos = productos != null ? productos.length : 0;

        if (totalProductos == 0) {
            sendError(response, "La sucursal no tiene productos", startTime, HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if ((nombre == null || nombre.isEmpty()) && (barcode == null || barcode.isEmpty())) {
            Random rand = new Random();
            int idx = rand.nextInt(totalProductos);
            nombre = productos[idx].getName();
            barcode = productos[idx].getBarcode();
        }

        if (nombre == null || nombre.isEmpty()) {
            Product p = sucursal.buscarPorBarcode(barcode);
            if (p != null) nombre = p.getName();
            else nombre = "NO_ENCONTRADO";
        }

        if (barcode == null || barcode.isEmpty()) {
            Product p = buscarSecuencial(sucursal, nombre);
            if (p != null) barcode = p.getBarcode();
            else barcode = "NO_ENCONTRADO";
        }

        List<ResultadoMedicion> resultados = new ArrayList<>();

        resultados.add(medirBusquedaLista(sucursal, nombre, iteraciones, totalProductos));
        resultados.add(medirBusquedaAvl(sucursal, nombre, iteraciones, totalProductos));
        resultados.add(medirBusquedaHash(sucursal, barcode, iteraciones, totalProductos));

        ApiResponse<List<ResultadoMedicion>> apiResponse = ApiResponse.success(resultados);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(gson.toJson(apiResponse));
    }

    private ResultadoMedicion medirBusquedaLista(Sucursal sucursal, String nombre, int iteraciones, int total) {
        long tiempoTotalNs = 0;
        boolean encontrado = false;

        for (int i = 0; i < iteraciones; i++) {
            long inicio = System.nanoTime();
            Product resultado = buscarSecuencial(sucursal, nombre);
            tiempoTotalNs += (System.nanoTime() - inicio);
            if (resultado != null) encontrado = true;
        }

        double tiempoPromedioMs = (tiempoTotalNs / (double) iteraciones) / 1_000_000.0;

        ResultadoMedicion resultado = new ResultadoMedicion("lista", "busqueda", tiempoPromedioMs * iteraciones, total);
        resultado.setIteraciones(iteraciones);
        resultado.setTiempoPromedioMs(tiempoPromedioMs);
        resultado.setClaveBusqueda(nombre);
        resultado.setComplejidad("O(n)");
        resultado.getDetalles().add("encontrado: " + encontrado);
        resultado.getDetalles().add("metodo: secuencial");

        return resultado;
    }

    private ResultadoMedicion medirBusquedaAvl(Sucursal sucursal, String nombre, int iteraciones, int total) {
        long tiempoTotalNs = 0;
        boolean encontrado = false;

        for (int i = 0; i < iteraciones; i++) {
            long inicio = System.nanoTime();
            Product resultado = sucursal.buscarPorNombre(nombre);
            tiempoTotalNs += (System.nanoTime() - inicio);
            if (resultado != null) encontrado = true;
        }

        double tiempoPromedioMs = (tiempoTotalNs / (double) iteraciones) / 1_000_000.0;

        ResultadoMedicion resultado = new ResultadoMedicion("avl", "busqueda", tiempoPromedioMs * iteraciones, total);
        resultado.setIteraciones(iteraciones);
        resultado.setTiempoPromedioMs(tiempoPromedioMs);
        resultado.setClaveBusqueda(nombre);
        resultado.setComplejidad("O(log n)");
        resultado.getDetalles().add("encontrado: " + encontrado);
        resultado.getDetalles().add("metodo: arbol balanceado");

        return resultado;
    }

    private ResultadoMedicion medirBusquedaHash(Sucursal sucursal, String barcode, int iteraciones, int total) {
        long tiempoTotalNs = 0;
        boolean encontrado = false;

        for (int i = 0; i < iteraciones; i++) {
            long inicio = System.nanoTime();
            Product resultado = sucursal.buscarPorBarcode(barcode);
            tiempoTotalNs += (System.nanoTime() - inicio);
            if (resultado != null) encontrado = true;
        }

        double tiempoPromedioMs = (tiempoTotalNs / (double) iteraciones) / 1_000_000.0;

        ResultadoMedicion resultado = new ResultadoMedicion("hash", "busqueda", tiempoPromedioMs * iteraciones, total);
        resultado.setIteraciones(iteraciones);
        resultado.setTiempoPromedioMs(tiempoPromedioMs);
        resultado.setClaveBusqueda(barcode);
        resultado.setComplejidad("O(1) promedio");
        resultado.getDetalles().add("encontrado: " + encontrado);
        resultado.getDetalles().add("metodo: tabla hash");

        return resultado;
    }

    private Product buscarSecuencial(Sucursal sucursal, String nombre) {
        Product[] productos = sucursal.getInventarioLista().listarTodos();
        if (productos == null) return null;
        for (Product p : productos) {
            if (p != null && p.getName().equalsIgnoreCase(nombre)) {
                return p;
            }
        }
        return null;
    }

    private void sendError(HttpServletResponse response, String mensaje, long startTime, int status) throws IOException {
        ApiResponse<Object> errorResponse = ApiResponse.error(mensaje);
        errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.setStatus(status);
        response.getWriter().write(gson.toJson(errorResponse));
    }
}