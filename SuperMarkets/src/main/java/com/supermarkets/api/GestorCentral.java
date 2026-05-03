package com.supermarkets.api;

import com.supermarkets.pojo.ConexionSucursal;
import com.supermarkets.pojo.Product;
import com.supermarkets.pojo.Sucursal;
import com.supermarkets.structures.grafo.Grafo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorCentral {
    private static GestorCentral instancia;
    private Map<String, Sucursal> sucursales;
    private Grafo redSucursales;
    private List<ConexionSucursal> conexiones;

    private GestorCentral() {
        this.sucursales = new HashMap<>();
        this.redSucursales = new Grafo();
    }

    public static synchronized GestorCentral getInstancia() {
        if (instancia == null) {
            instancia = new GestorCentral();
        }
        return instancia;
    }

    public void cargarSucursales(List<Sucursal> lista) {
        if (lista == null) return;

        for (Sucursal s : lista) {
            if (s.getId() != null && !s.getId().isEmpty()) {
                sucursales.put(s.getId(), s);
                redSucursales.nuevoNodo(s.getId(), s);
            }
        }
    }

    public void cargarConexiones(List<ConexionSucursal> lista) {
        if (lista == null) return;

        this.conexiones = lista;
        for (ConexionSucursal c : lista) {
            redSucursales.aristaBidireccional(c.getOrigenId(), c.getDestinoId(), (float) c.getTiempo());
        }
    }

    public void cargarProductoASucursal(Product producto) {
        if (producto == null || producto.getSucursalId() == null) return;

        Sucursal sucursal = sucursales.get(producto.getSucursalId());
        if (sucursal != null) {
            sucursal.agregarProducto(producto);
        }
    }

    public void cargarProductos(List<Product> lista) {
        if (lista == null) return;

        for (Product p : lista) {
            cargarProductoASucursal(p);
        }
    }

    public Map<String, Sucursal> getSucursales() {
        return sucursales;
    }

    public Sucursal getSucursal(String id) {
        return sucursales.get(id);
    }

    public void agregarSucursal(Sucursal sucursal) {
        if (sucursal != null && sucursal.getId() != null) {
            sucursales.put(sucursal.getId(), sucursal);
            redSucursales.nuevoNodo(sucursal.getId(), sucursal);
        }
    }

    public void actualizarSucursal(Sucursal sucursal) {
        if (sucursal != null && sucursal.getId() != null) {
            if (sucursales.containsKey(sucursal.getId())) {
                sucursales.put(sucursal.getId(), sucursal);
                redSucursales.nuevoNodo(sucursal.getId(), sucursal);
            }
        }
    }

    public boolean eliminarSucursal(String id) {
        if (id != null && sucursales.containsKey(id)) {
            sucursales.remove(id);
            return true;
        }
        return false;
    }

    public Grafo getRedSucursales() {
        return redSucursales;
    }

    public List<String> calcularRuta(String origenId, String destinoId) {
        return redSucursales.dijkstra(origenId, destinoId);
    }

    public List<ConexionSucursal> getConexiones() {
        return conexiones;
    }

    public void iniciarTodas() {
        for (Sucursal s : sucursales.values()) {
            s.iniciar();
        }
    }

    public void detenerTodas() {
        for (Sucursal s : sucursales.values()) {
            s.detener();
        }
    }

    public void agregarProductosAColaIngreso(List<Product> productos) {
        if (productos == null) return;
        for (Product p : productos) {
            Sucursal suc = sucursales.get(p.getSucursalId());
            if (suc != null) {
                suc.agregarAIngreso(p);
            }
        }
    }

    public void limpiarTodo() {
        sucursales.clear();
        redSucursales = new Grafo();
        conexiones = null;
    }
}