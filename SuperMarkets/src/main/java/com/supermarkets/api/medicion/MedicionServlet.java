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

            if (pathInfo != null && pathInfo.endsWith("/busqueda")) {
                medirBusqueda(request, response, startTime);
                return;
            }

            if (pathInfo != null && pathInfo.endsWith("/comparar")) {
                compararEstructuras(request, response, startTime);
                return;
            }

            List<String> endpoints = new ArrayList<>();
            endpoints.add("GET /api/medicion/busqueda?sucursal={id}&tipo={secuencial/binaria/hash}&nombre={nombre}");
            endpoints.add("GET /api/medicion/comparar?sucursal={id}&operacion={buscar/insertar}&nombre={nombre}");

            ApiResponse<List<String>> apiResponse = ApiResponse.success(endpoints);
            apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.getWriter().write(gson.toJson(apiResponse));

        } catch (Exception e) {
            ApiResponse<Object> errorResponse = ApiResponse.error("Error: " + e.getMessage());
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }

    private void medirBusqueda(HttpServletRequest request, HttpServletResponse response, long startTime) throws IOException {
        String sucursalId = request.getParameter("sucursal");
        String tipo = request.getParameter("tipo");
        String nombre = request.getParameter("nombre");

        if (sucursalId == null || tipo == null || nombre == null) {
            ApiResponse<Object> errorResponse = ApiResponse.error("Parámetros requeridos: sucursal, tipo, nombre");
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(errorResponse));
            return;
        }

        Sucursal sucursal = gestor.getSucursal(sucursalId);
        if (sucursal == null) {
            ApiResponse<Object> errorResponse = ApiResponse.error("Sucursal no encontrada");
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(gson.toJson(errorResponse));
            return;
        }

        long tiempoInicio = System.nanoTime();
        Product resultado = null;

        switch (tipo.toLowerCase()) {
            case "secuencial":
            case "lista":
                resultado = buscarSecuencial(sucursal, nombre);
                break;
            case "binaria":
                resultado = sucursal.buscarPorNombre(nombre);
                break;
            case "hash":
                resultado = sucursal.buscarPorBarcode(buscarBarcodePorNombre(sucursal, nombre));
                break;
            default:
                ApiResponse<Object> errorResponse = ApiResponse.error("Tipo no válido: " + tipo);
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
        }

        double tiempoMs = (System.nanoTime() - tiempoInicio) / 1_000_000.0;

        ResultadoMedicion medicion = new ResultadoMedicion(
                tipo,
                "busqueda",
                tiempoMs,
                sucursal.getTotalProductos()
        );
        medicion.getDetalles().add("encontrado: " + (resultado != null));
        medicion.getDetalles().add("producto: " + (resultado != null ? resultado.getName() : "null"));

        ApiResponse<ResultadoMedicion> apiResponse = ApiResponse.success(medicion);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(gson.toJson(apiResponse));
    }

    private void compararEstructuras(HttpServletRequest request, HttpServletResponse response, long startTime) throws IOException {
        String sucursalId = request.getParameter("sucursal");
        String operacion = request.getParameter("operacion");
        String nombre = request.getParameter("nombre");

        if (sucursalId == null || operacion == null) {
            ApiResponse<Object> errorResponse = ApiResponse.error("Parámetros requeridos: sucursal, operacion");
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(errorResponse));
            return;
        }

        Sucursal sucursal = gestor.getSucursal(sucursalId);
        if (sucursal == null) {
            ApiResponse<Object> errorResponse = ApiResponse.error("Sucursal no encontrada");
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(gson.toJson(errorResponse));
            return;
        }

        List<ResultadoMedicion> resultados = new ArrayList<>();
        int totalProductos = sucursal.getTotalProductos();

        if (operacion.equalsIgnoreCase("buscar")) {
            String nombreABuscar = nombre != null ? nombre : "no_existe";
            String barcode = buscarBarcodePorNombre(sucursal, nombreABuscar);

            resultados.add(medirBusquedaSecuencial(sucursal, nombreABuscar, totalProductos));
            resultados.add(medirBusquedaBinaria(sucursal, nombreABuscar, totalProductos));
            resultados.add(medirBusquedaHash(sucursal, barcode, totalProductos));
        } else if (operacion.equalsIgnoreCase("insertar")) {
            resultados.add(new ResultadoMedicion("lista", "insercion", 0.0, totalProductos));
            resultados.add(new ResultadoMedicion("avl", "insercion", 0.0, totalProductos));
            resultados.add(new ResultadoMedicion("b", "insercion", 0.0, totalProductos));
            resultados.add(new ResultadoMedicion("bplus", "insercion", 0.0, totalProductos));
            resultados.add(new ResultadoMedicion("hash", "insercion", 0.0, totalProductos));
        }

        ApiResponse<List<ResultadoMedicion>> apiResponse = ApiResponse.success(resultados);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(gson.toJson(apiResponse));
    }

    private ResultadoMedicion medirBusquedaSecuencial(Sucursal sucursal, String nombre, int total) {
        long inicio = System.nanoTime();
        Product resultado = buscarSecuencial(sucursal, nombre);
        double tiempoMs = (System.nanoTime() - inicio) / 1_000_000.0;

        return new ResultadoMedicion("lista", "busqueda", tiempoMs, total);
    }

    private ResultadoMedicion medirBusquedaBinaria(Sucursal sucursal, String nombre, int total) {
        long inicio = System.nanoTime();
        Product resultado = sucursal.buscarPorNombre(nombre);
        double tiempoMs = (System.nanoTime() - inicio) / 1_000_000.0;

        return new ResultadoMedicion("avl", "busqueda", tiempoMs, total);
    }

    private ResultadoMedicion medirBusquedaHash(Sucursal sucursal, String barcode, int total) {
        long inicio = System.nanoTime();
        Product resultado = sucursal.buscarPorBarcode(barcode);
        double tiempoMs = (System.nanoTime() - inicio) / 1_000_000.0;

        return new ResultadoMedicion("hash", "busqueda", tiempoMs, total);
    }

    private Product buscarSecuencial(Sucursal sucursal, String nombre) {
        Product[] productos = sucursal.getInventarioLista().listarTodos();
        for (Product p : productos) {
            if (p != null && p.getName().equalsIgnoreCase(nombre)) {
                return p;
            }
        }
        return null;
    }

    private String buscarBarcodePorNombre(Sucursal sucursal, String nombre) {
        Product p = sucursal.buscarPorNombre(nombre);
        return p != null ? p.getBarcode() : "no_existe";
    }
}