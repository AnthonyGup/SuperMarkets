package com.supermarkets.api.estructuras;

import com.supermarkets.api.GestorCentral;
import com.supermarkets.dto.ApiResponse;
import com.supermarkets.dto.DotResponse;
import com.supermarkets.pojo.Sucursal;
import com.supermarkets.structures.avl.ArbolAvl;
import com.supermarkets.structures.b.ArbolB;
import com.supermarkets.structures.b.NodoB;
import com.supermarkets.structures.bplus.ArbolBPlus;
import com.supermarkets.structures.bplus.NodoBPlus;
import com.supermarkets.structures.hash.TablaHash;
import com.supermarkets.structures.visualization.HashTableVisualizer;
import com.supermarkets.structures.visualization.AvlTreeVisualizer;
import com.supermarkets.structures.visualization.BTreeVisualizer;
import com.supermarkets.structures.visualization.BPlusTreeVisualizer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/estructuras/*")
public class EstructurasServlet extends HttpServlet {
    private final GestorCentral gestor = GestorCentral.getInstancia();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        long startTime = System.currentTimeMillis();

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("")) {
                sendInfo(response, startTime);
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length < 2) {
                sendError(response, "Parámetros insuficientes", startTime, 400);
                return;
            }

            String tipo = parts[1];
            String sucursalId = request.getParameter("sucursal");

            if (sucursalId == null || sucursalId.isEmpty()) {
                sendError(response, "Parámetro 'sucursal' requerido", startTime, 400);
                return;
            }

            Sucursal sucursal = gestor.getSucursal(sucursalId);
            if (sucursal == null) {
                sendError(response, "Sucursal no encontrada: " + sucursalId, startTime, 404);
                return;
            }

            switch (tipo.toLowerCase()) {
                case "avl":
                case "avl-dot":
                    enviarDotAvl(sucursal, response, startTime);
                    break;
                case "b":
                case "b-dot":
                    enviarDotB(sucursal, response, startTime);
                    break;
                case "bplus":
                case "bplus-dot":
                case "b+":
                    enviarDotBPlus(sucursal, response, startTime);
                    break;
                case "hash":
                    enviarTablaHash(sucursal, response, startTime);
                    break;
                default:
                    sendError(response, "Tipo de estructura no reconocido: " + tipo, startTime, 400);
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Error: " + e.getMessage(), startTime, 500);
        }
    }

    private void sendInfo(HttpServletResponse response, long startTime) throws IOException {
        List<String> info = new ArrayList<>();
        info.add("=== VISUALIZACION DE ESTRUCTURAS ===");
        info.add("GET /api/estructuras/{tipo}?sucursal={id}");
        info.add("Tipos disponibles:");
        info.add("- avl: Arbol AVL (por nombre)");
        info.add("- b: Arbol B (por fecha)");
        info.add("- bplus: Arbol B+ (por categoria)");
        info.add("- hash: Tabla Hash (por barcode)");

        ApiResponse<List<String>> apiResponse = ApiResponse.success(info);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(apiResponse));
    }

    private void enviarTablaHash(Sucursal sucursal, HttpServletResponse response, long startTime) throws IOException {
        TablaHash hash = sucursal.getInventarioHash();
        HashTableVisualizer visualizer = new HashTableVisualizer();
        String dot = visualizer.getDot(hash);
        DotResponse dto = new DotResponse("Hash", dot, visualizer.getCapacidad(), (int) (visualizer.getFactorCarga() * 100));

        ApiResponse<DotResponse> apiResponse = ApiResponse.success(dto);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(apiResponse));
    }

    private NodoB getRaizB(ArbolB arbol) {
        try {
            java.lang.reflect.Field field = ArbolB.class.getDeclaredField("raiz");
            field.setAccessible(true);
            return (NodoB) field.get(arbol);
        } catch (Exception e) {
            return null;
        }
    }

    private NodoBPlus getRaizBPlus(ArbolBPlus arbol) {
        try {
            java.lang.reflect.Field field = ArbolBPlus.class.getDeclaredField("raiz");
            field.setAccessible(true);
            return (NodoBPlus) field.get(arbol);
        } catch (Exception e) {
            return null;
        }
    }

    private void sendError(HttpServletResponse response, String mensaje, long startTime, int status) throws IOException {
        ApiResponse<Object> errorResponse = ApiResponse.error(mensaje);
        errorResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.setStatus(status);
        response.getWriter().write(new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(errorResponse));
    }

    private void enviarDotAvl(Sucursal sucursal, HttpServletResponse response, long startTime) throws IOException {
        ArbolAvl avl = sucursal.getInventarioAvl();
        AvlTreeVisualizer visualizer = new AvlTreeVisualizer();
        String dot = visualizer.getDot(avl.getRaiz());
        DotResponse dto = new DotResponse("AVL", dot, visualizer.getTotalNodos(), visualizer.getAltura());

        ApiResponse<DotResponse> apiResponse = ApiResponse.success(dto);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(apiResponse));
    }

    private void enviarDotB(Sucursal sucursal, HttpServletResponse response, long startTime) throws IOException {
        ArbolB arbolB = sucursal.getInventarioB();
        NodoB raiz = getRaizB(arbolB);
        BTreeVisualizer visualizer = new BTreeVisualizer();
        String dot = visualizer.getDot(raiz);
        DotResponse dto = new DotResponse("B", dot, visualizer.getTotalNodos(), visualizer.getAltura());

        ApiResponse<DotResponse> apiResponse = ApiResponse.success(dto);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(apiResponse));
    }

    private void enviarDotBPlus(Sucursal sucursal, HttpServletResponse response, long startTime) throws IOException {
        ArbolBPlus arbolBPlus = sucursal.getInventarioBPlus();
        NodoBPlus raiz = getRaizBPlus(arbolBPlus);
        BPlusTreeVisualizer visualizer = new BPlusTreeVisualizer();
        String dot = visualizer.getDot(raiz);
        DotResponse dto = new DotResponse("B+", dot, visualizer.getTotalNodos(), visualizer.getAltura());

        ApiResponse<DotResponse> apiResponse = ApiResponse.success(dto);
        apiResponse.setTiempoMs(System.currentTimeMillis() - startTime);
        response.getWriter().write(new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(apiResponse));
    }
}