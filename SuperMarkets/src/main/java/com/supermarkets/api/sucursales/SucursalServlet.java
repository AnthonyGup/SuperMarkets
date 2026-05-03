package com.supermarkets.api.sucursales;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.supermarkets.api.GestorCentral;
import com.supermarkets.dto.ApiResponse;
import com.supermarkets.dto.SucursalDto;
import com.supermarkets.pojo.Sucursal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/sucursales/*")
public class SucursalServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final GestorCentral gestor = GestorCentral.getInstancia();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String pathInfo = request.getPathInfo();
        long startTime = System.currentTimeMillis();

        try {
            List<SucursalDto> sucursalesDto = new ArrayList<>();

            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("")) {
                for (Sucursal s : gestor.getSucursales().values()) {
                    sucursalesDto.add(toDto(s));
                }
                ApiResponse<List<SucursalDto>> apiResponse = ApiResponse.success(sucursalesDto);
                apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.getWriter().write(gson.toJson(apiResponse));
                return;
            }

            String id = pathInfo.substring(1);
            if (id.contains("/")) {
                id = id.substring(0, id.indexOf("/"));
            }

            Sucursal sucursal = gestor.getSucursal(id);

            if (sucursal == null) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Sucursal no encontrada: " + id);
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            ApiResponse<SucursalDto> apiResponse = ApiResponse.success(toDto(sucursal));
            apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.getWriter().write(gson.toJson(apiResponse));

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Object> errorResponse = ApiResponse.error("Error: " + e.getMessage());
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        long startTime = System.currentTimeMillis();

        try {
            Sucursal sucursal = gson.fromJson(request.getReader(), Sucursal.class);

            if (sucursal == null || sucursal.getId() == null || sucursal.getId().isEmpty()) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Datos inválidos");
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            gestor.agregarSucursal(sucursal);

            ApiResponse<SucursalDto> apiResponse = ApiResponse.success("Sucursal creada", toDto(sucursal));
            apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(apiResponse));

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Object> errorResponse = ApiResponse.error("Error: " + e.getMessage());
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        long startTime = System.currentTimeMillis();

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("")) {
                ApiResponse<Object> errorResponse = ApiResponse.error("ID requerido");
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            String id = pathInfo.substring(1);
            if (id.contains("/")) {
                id = id.substring(0, id.indexOf("/"));
            }

            Sucursal sucursal = gson.fromJson(request.getReader(), Sucursal.class);

            if (sucursal == null) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Datos inválidos");
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            if (!gestor.getSucursales().containsKey(id)) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Sucursal no encontrada: " + id);
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            gestor.actualizarSucursal(sucursal);

            ApiResponse<SucursalDto> apiResponse = ApiResponse.success("Sucursal actualizada", toDto(sucursal));
            apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.getWriter().write(gson.toJson(apiResponse));

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Object> errorResponse = ApiResponse.error("Error: " + e.getMessage());
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        long startTime = System.currentTimeMillis();

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("")) {
                ApiResponse<Object> errorResponse = ApiResponse.error("ID requerido");
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            String id = pathInfo.substring(1);
            if (id.contains("/")) {
                id = id.substring(0, id.indexOf("/"));
            }

            if (!gestor.getSucursales().containsKey(id)) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Sucursal no encontrada: " + id);
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            gestor.eliminarSucursal(id);

            ApiResponse<String> apiResponse = ApiResponse.success("Sucursal eliminada", id);
            apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.getWriter().write(gson.toJson(apiResponse));

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Object> errorResponse = ApiResponse.error("Error: " + e.getMessage());
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }

    private SucursalDto toDto(Sucursal s) {
        if (s == null) return null;
        SucursalDto dto = new SucursalDto();
        dto.setId(s.getId());
        dto.setNombre(s.getNombre());
        dto.setUbicacion(s.getUbicacion());
        dto.settIngreso(s.gettIngreso());
        dto.settTraspaso(s.gettTraspaso());
        dto.settDespacho(s.gettDespacho());
        dto.setTotalProductos(s.getTotalProductos());
        dto.setColaIngresoSize(s.getColaIngreso() != null ? s.getColaIngreso().size() : 0);
        dto.setColaPreparacionSize(s.getColaPreparacion() != null ? s.getColaPreparacion().size() : 0);
        dto.setColaSalidaSize(s.getColaSalida() != null ? s.getColaSalida().size() : 0);
        return dto;
    }
}