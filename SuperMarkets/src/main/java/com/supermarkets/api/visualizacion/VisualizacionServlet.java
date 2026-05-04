package com.supermarkets.api.visualizacion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.supermarkets.api.GestorCentral;
import com.supermarkets.api.GestorTransferencias;
import com.supermarkets.api.TransferenciaActiva;
import com.supermarkets.api.TransferenciaHistorial;
import com.supermarkets.dto.ApiResponse;
import com.supermarkets.pojo.ConexionSucursal;
import com.supermarkets.pojo.Sucursal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@WebServlet("/api/visualizacion/*")
public class VisualizacionServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final GestorCentral gestor = GestorCentral.getInstancia();
    private final GestorTransferencias gestorTransferencias = GestorTransferencias.getInstancia();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        long startTime = System.currentTimeMillis();

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/estado")) {
                obtenerEstadoGeneral(request, response, startTime);
                return;
            }

            if (pathInfo != null && pathInfo.startsWith("/sucursal/")) {
                String sucursalId = pathInfo.substring("/sucursal/".length());
                obtenerEstadoSucursal(sucursalId, request, response, startTime);
                return;
            }

            ApiResponse<Object> errorResponse = ApiResponse.error("Endpoint no reconocido: " + pathInfo);
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(errorResponse));

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Object> errorResponse = ApiResponse.error("Error: " + e.getMessage());
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }

    private void obtenerEstadoGeneral(HttpServletRequest request, HttpServletResponse response, long startTime) throws IOException {
        Map<String, Sucursal> sucursalesMap = gestor.getSucursales();
        List<ConexionSucursal> conexiones = gestor.getConexiones();

        Map<String, Object> resultado = new HashMap<>();

        List<Map<String, Object>> sucursalesList = new ArrayList<>();
        for (Sucursal s : sucursalesMap.values()) {
            Map<String, Object> sucData = new HashMap<>();
            sucData.put("id", s.getId());
            sucData.put("nombre", s.getNombre());
            sucData.put("colaIngreso", s.getColaIngreso() != null ? s.getColaIngreso().size() : 0);
            sucData.put("colaPreparacion", s.getColaPreparacion() != null ? s.getColaPreparacion().size() : 0);
            sucData.put("colaSalida", s.getColaSalida() != null ? s.getColaSalida().size() : 0);
            sucData.put("tIngreso", s.gettIngreso());
            sucData.put("tTraspaso", s.gettTraspaso());
            sucData.put("tDespacho", s.gettDespacho());
            sucData.put("totalProductos", s.getTotalProductos());
            sucData.put("estado", s.isEjecutando() ? "procesando" : "normal");
            sucursalesList.add(sucData);
        }
        resultado.put("sucursales", sucursalesList);

        List<Map<String, Object>> conexionesList = new ArrayList<>();
        if (conexiones != null) {
            for (ConexionSucursal c : conexiones) {
                Map<String, Object> connData = new HashMap<>();
                connData.put("origen", c.getOrigenId());
                connData.put("destino", c.getDestinoId());
                connData.put("tiempo", c.getTiempo());
                connData.put("costo", c.getCosto());
                conexionesList.add(connData);
            }
        }
        resultado.put("conexiones", conexionesList);

        List<TransferenciaActiva> activas = gestorTransferencias.getTransferenciasActivas();
        List<Map<String, Object>> transferenciasActivas = new ArrayList<>();
        for (TransferenciaActiva t : activas) {
            transferenciasActivas.add(t.toMap());
        }
        resultado.put("transferenciasActivas", transferenciasActivas);

        List<TransferenciaHistorial> historial = gestorTransferencias.getHistorial();
        List<Map<String, Object>> historialDto = new ArrayList<>();
        for (TransferenciaHistorial h : historial) {
            Map<String, Object> hData = new HashMap<>();
            hData.put("id", h.getId());
            hData.put("producto", h.getProductoNombre());
            hData.put("origen", h.getOrigen());
            hData.put("destino", h.getDestino());
            hData.put("tiempoCompletado", h.getTiempoCompletado());
            hData.put("duracionMs", h.getDuracionMs());
            historialDto.add(hData);
        }
        resultado.put("historialTransferencias", historialDto);

        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(resultado);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(gson.toJson(apiResponse));
    }

    private void obtenerEstadoSucursal(String sucursalId, HttpServletRequest request, HttpServletResponse response, long startTime) throws IOException {
        Sucursal sucursal = gestor.getSucursal(sucursalId);

        if (sucursal == null) {
            ApiResponse<Object> errorResponse = ApiResponse.error("Sucursal no encontrada");
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(gson.toJson(errorResponse));
            return;
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("id", sucursal.getId());
        resultado.put("nombre", sucursal.getNombre());
        resultado.put("colaIngreso", sucursal.getColaIngreso() != null ? sucursal.getColaIngreso().size() : 0);
        resultado.put("colaPreparacion", sucursal.getColaPreparacion() != null ? sucursal.getColaPreparacion().size() : 0);
        resultado.put("colaSalida", sucursal.getColaSalida() != null ? sucursal.getColaSalida().size() : 0);
        resultado.put("tIngreso", sucursal.gettIngreso());
        resultado.put("tTraspaso", sucursal.gettTraspaso());
        resultado.put("tDespacho", sucursal.gettDespacho());
        resultado.put("totalProductos", sucursal.getTotalProductos());
        resultado.put("estado", sucursal.isEjecutando() ? "procesando" : "normal");

        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(resultado);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(gson.toJson(apiResponse));
    }
}