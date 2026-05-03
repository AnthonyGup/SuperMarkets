package com.supermarkets.api.productos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.supermarkets.api.GestorCentral;
import com.supermarkets.dto.ApiResponse;
import com.supermarkets.dto.ProductoDto;
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

@WebServlet("/api/productos/*")
public class ProductoServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final GestorCentral gestor = GestorCentral.getInstancia();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        long startTime = System.currentTimeMillis();

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo != null && pathInfo.length() > 1 && pathInfo.endsWith("/buscar")) {
                buscarProductos(request, response, startTime);
                return;
            }

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

            List<ProductoDto> productos = new ArrayList<>();
            Product[] todos = sucursal.getInventarioLista().listarTodos();
            for (Product p : todos) {
                productos.add(ProductoDto.fromProduct(p));
            }

            ApiResponse<List<ProductoDto>> apiResponse = ApiResponse.success(productos);
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

    private void buscarProductos(HttpServletRequest request, HttpServletResponse response, long startTime) throws IOException {
        String sucursalId = request.getParameter("sucursal");
        String nombre = request.getParameter("nombre");
        String barcode = request.getParameter("barcode");
        String categoria = request.getParameter("categoria");
        String fechaInicio = request.getParameter("fechaInicio");
        String fechaFin = request.getParameter("fechaFin");

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

        List<ProductoDto> resultados = new ArrayList<>();

        if (nombre != null && !nombre.isEmpty()) {
            Product p = sucursal.buscarPorNombre(nombre);
            if (p != null) resultados.add(ProductoDto.fromProduct(p));
        } else if (barcode != null && !barcode.isEmpty()) {
            Product p = sucursal.buscarPorBarcode(barcode);
            if (p != null) resultados.add(ProductoDto.fromProduct(p));
        } else if (categoria != null && !categoria.isEmpty()) {
            Product[] prods = sucursal.buscarPorCategoria(categoria);
            if (prods != null) {
                for (Product p : prods) {
                    resultados.add(ProductoDto.fromProduct(p));
                }
            }
        } else if (fechaInicio != null && fechaFin != null && !fechaInicio.isEmpty() && !fechaFin.isEmpty()) {
            Product[] prods = sucursal.buscarPorRangoFechas(fechaInicio, fechaFin);
            if (prods != null) {
                for (Product p : prods) {
                    resultados.add(ProductoDto.fromProduct(p));
                }
            }
        }

        ApiResponse<List<ProductoDto>> apiResponse = ApiResponse.success(resultados);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(gson.toJson(apiResponse));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        long startTime = System.currentTimeMillis();

        try {
            Product producto = gson.fromJson(request.getReader(), Product.class);

            if (producto == null || producto.getSucursalId() == null || producto.getSucursalId().isEmpty()) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Datos inválidos: sucursalId es requerido");
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            Sucursal sucursal = gestor.getSucursal(producto.getSucursalId());
            if (sucursal == null) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Sucursal no encontrada: " + producto.getSucursalId());
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            boolean exito = sucursal.agregarProducto(producto);

            if (exito) {
                ApiResponse<ProductoDto> apiResponse = ApiResponse.success("Producto agregado", ProductoDto.fromProduct(producto));
                apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(gson.toJson(apiResponse));
            } else {
                ApiResponse<Object> errorResponse = ApiResponse.error("No se pudo agregar el producto");
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(errorResponse));
            }

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
                ApiResponse<Object> errorResponse = ApiResponse.error("Nombre requerido");
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            String nombre = pathInfo.substring(1);
            if (nombre.contains("/")) {
                nombre = nombre.substring(0, nombre.indexOf("/"));
            }

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

            boolean exito = sucursal.eliminarProducto(nombre);

            if (exito) {
                ApiResponse<String> apiResponse = ApiResponse.success("Producto eliminado", nombre);
                apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.getWriter().write(gson.toJson(apiResponse));
            } else {
                ApiResponse<Object> errorResponse = ApiResponse.error("Producto no encontrado");
                errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(errorResponse));
            }

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Object> errorResponse = ApiResponse.error("Error: " + e.getMessage());
            errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }
}