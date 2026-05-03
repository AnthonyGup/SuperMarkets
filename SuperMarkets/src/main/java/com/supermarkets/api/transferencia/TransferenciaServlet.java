package com.supermarkets.api.transferencia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.supermarkets.api.GestorCentral;
import com.supermarkets.api.GestorTransferencias;
import com.supermarkets.dto.ApiResponse;
import com.supermarkets.pojo.Product;
import com.supermarkets.pojo.Sucursal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/transferencia/*")
public class TransferenciaServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final GestorCentral gestor = GestorCentral.getInstancia();
    private final GestorTransferencias gestorTransferencias = GestorTransferencias.getInstancia();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        long startTime = System.currentTimeMillis();

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("")) {
                verEstadoTransferencias(request, response, startTime);
                return;
            }

            if (pathInfo.endsWith("/ruta")) {
                calcularRuta(request, response, startTime);
                return;
            }

            if (pathInfo.endsWith("/cola")) {
                verColas(request, response, startTime);
                return;
            }

            if (pathInfo.endsWith("/activas")) {
                verTransferenciasActivas(request, response, startTime);
                return;
            }

            if (pathInfo.endsWith("/historial")) {
                verHistorial(request, response, startTime);
                return;
            }

            ApiResponse<Object> errorResponse = ApiResponse.error("Endpoint no reconocido");
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

    private void verEstadoTransferencias(HttpServletRequest request, HttpServletResponse response, long startTime) throws IOException {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("activas", gestorTransferencias.getTransferenciasActivas());
        resultado.put("historial", gestorTransferencias.getHistorial());

        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(resultado);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(gson.toJson(apiResponse));
    }

    private void verTransferenciasActivas(HttpServletRequest request, HttpServletResponse response, long startTime) throws IOException {
        List<GestorTransferencias.TransferenciaActiva> activas = gestorTransferencias.getTransferenciasActivas();

        List<Map<String, Object>> activasDto = new ArrayList<>();
        for (GestorTransferencias.TransferenciaActiva t : activas) {
            activasDto.add(t.toMap());
        }

        ApiResponse<List<Map<String, Object>>> apiResponse = ApiResponse.success(activasDto);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(gson.toJson(apiResponse));
    }

    private void verHistorial(HttpServletRequest request, HttpServletResponse response, long startTime) throws IOException {
        List<GestorTransferencias.TransferenciaHistorial> hist = gestorTransferencias.getHistorial();

        List<Map<String, Object>> histDto = new ArrayList<>();
        for (GestorTransferencias.TransferenciaHistorial h : hist) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", h.getId());
            dto.put("producto", h.getProductoNombre());
            dto.put("barcode", h.getBarcode());
            dto.put("origen", h.getOrigen());
            dto.put("destino", h.getDestino());
            dto.put("tiempoCompletado", h.getTiempoCompletado());
            dto.put("duracionMs", h.getDuracionMs());
            histDto.add(dto);
        }

        ApiResponse<List<Map<String, Object>>> apiResponse = ApiResponse.success(histDto);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(gson.toJson(apiResponse));
    }

    private void calcularRuta(HttpServletRequest request, HttpServletResponse response, long startTime) throws IOException {
        String origen = request.getParameter("origen");
        String destino = request.getParameter("destino");
        String criterio = request.getParameter("criterio");

        if (origen == null || origen.isEmpty() || destino == null || destino.isEmpty()) {
            ApiResponse<Object> errorResponse = ApiResponse.error("Parámetros 'origen' y 'destino' requeridos");
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(errorResponse));
            return;
        }

        List<String> ruta = gestor.calcularRuta(origen, destino);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("origen", origen);
        resultado.put("destino", destino);
        resultado.put("criterio", criterio != null ? criterio : "tiempo");

        if (ruta == null || ruta.isEmpty()) {
            resultado.put("ruta", new ArrayList<String>());
            resultado.put("saltos", 0);
            resultado.put("mensaje", "No existe ruta entre las sucursales");
        } else {
            resultado.put("ruta", ruta);
            resultado.put("saltos", ruta.size() - 1);
        }

        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(resultado);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(gson.toJson(apiResponse));
    }

    private void verColas(HttpServletRequest request, HttpServletResponse response, long startTime) throws IOException {
        String sucursalId = request.getParameter("sucursal");

        if (sucursalId == null || sucursalId.isEmpty()) {
            ApiResponse<Object> errorResponse = ApiResponse.error("Parámetro 'sucursal' requerido");
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

        Map<String, Object> colas = new HashMap<>();
        colas.put("ingreso", sucursal.getColaIngreso() != null ? sucursal.getColaIngreso().size() : 0);
        colas.put("preparacion", sucursal.getColaPreparacion() != null ? sucursal.getColaPreparacion().size() : 0);
        colas.put("salida", sucursal.getColaSalida() != null ? sucursal.getColaSalida().size() : 0);

        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(colas);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(gson.toJson(apiResponse));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        long startTime = System.currentTimeMillis();

        try {
            Map<String, String> datos = gson.fromJson(request.getReader(), Map.class);

            String origenId = datos.get("origenId");
            String destinoId = datos.get("destinoId");
            String productoBarcode = datos.get("productoBarcode");
            String criterio = datos.get("criterio");

            if (origenId == null || origenId.isEmpty() || destinoId == null || destinoId.isEmpty() || productoBarcode == null || productoBarcode.isEmpty()) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Datos incompletos: origenId, destinoId y productoBarcode son requeridos");
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            Sucursal origen = gestor.getSucursal(origenId);
            if (origen == null) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Sucursal origen no encontrada: " + origenId);
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            if (gestor.getSucursal(destinoId) == null) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Sucursal destino no encontrada: " + destinoId);
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            Product producto = origen.buscarPorBarcode(productoBarcode);
            if (producto == null) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Producto no encontrado en origen con barcode: " + productoBarcode);
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            List<String> ruta = gestor.calcularRuta(origenId, destinoId);
            String transferId = gestorTransferencias.iniciarTransferencia(origenId, destinoId, producto);

            if (transferId == null) {
                ApiResponse<Object> errorResponse = ApiResponse.error("No se pudo iniciar la transferencia");
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("transferenciaId", transferId);
            resultado.put("producto", producto.getName());
            resultado.put("barcode", producto.getBarcode());
            resultado.put("origen", origenId);
            resultado.put("destino", destinoId);
            resultado.put("ruta", ruta != null ? ruta : new ArrayList<String>());
            resultado.put("saltos", ruta != null ? ruta.size() - 1 : 0);
            resultado.put("estado", producto.getEstado().getValor());
            resultado.put("criterio", criterio != null ? criterio : "tiempo");

            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success("Transferencia iniciada", resultado);
            apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(apiResponse));

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Object> errorResponse = ApiResponse.error("Error: " + e.getMessage());
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }
}
