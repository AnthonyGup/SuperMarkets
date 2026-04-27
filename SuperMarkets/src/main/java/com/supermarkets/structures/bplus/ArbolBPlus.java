package com.supermarkets.structures.bplus;

import com.supermarkets.pojo.Product;
import com.supermarkets.structures.listas.ListaEnlazada;

public class ArbolBPlus {
	protected int orden;
	protected NodoBPlus raiz;
	protected NodoBPlus hojaInicio;

	public ArbolBPlus(int m) {
		this.orden = m;
		this.raiz = null;
		this.hojaInicio = null;
		crear();
	}

	private void crear() {
		raiz = new NodoBPlus(orden, true);
		hojaInicio = raiz;
	}

	public void insertar(Product producto) {
		validarProducto(producto, "insertar");

		if (raiz == null) {
			crear();
		}

		if (raiz.nodoLLeno()) {
			NodoBPlus nuevaRaiz = new NodoBPlus(orden, false);
			nuevaRaiz.Prama(0, raiz);
			dividirNodo(nuevaRaiz, 0, raiz);
			raiz = nuevaRaiz;
		}

		insertarEnNodoNoLleno(raiz, producto);
	}

	public void eliminar(String categoria) {
		if (raiz == null || categoria == null || categoria.trim().isEmpty()) {
			return;
		}

		eliminar(raiz, categoria);

		if (raiz.Ocuenta() == 0 && !raiz.esHoja() && raiz.Orama(0) != null) {
			raiz = raiz.Orama(0);
		}
	}

	public void listarCategoria(String categoria) {
		ListaEnlazada productos = buscar(categoria);
		if (productos != null) {
			System.out.println("Productos en categoria: " + categoria);
			System.out.println("Total: " + productos.getSize());
		} else {
			System.out.println("No hay productos en la categoria: " + categoria);
		}
	}

	private ListaEnlazada buscar(String categoria) {
		if (raiz == null || categoria == null || categoria.trim().isEmpty()) {
			return null;
		}
		return buscar(raiz, categoria);
	}

	private ListaEnlazada buscar(NodoBPlus nodo, String categoria) {
		if (nodo == null) {
			return null;
		}

		if (nodo.esHoja()) {
			for (int i = 0; i < nodo.Ocuenta(); i++) {
				if (categoria.equals(nodo.Oclave(i))) {
					return nodo.Ovalor(i);
				}
			}
			return null;
		}

		int i = 0;
		while (i < nodo.Ocuenta() && categoria.compareTo(nodo.Oclave(i)) >= 0) {
			i++;
		}

		return buscar(nodo.Orama(i), categoria);
	}

	private void insertarEnNodoNoLleno(NodoBPlus nodo, Product producto) {
		if (nodo == null || producto == null) {
			return;
		}

		String categoria = producto.getCategory();
		int i = nodo.Ocuenta() - 1;

		if (nodo.esHoja()) {
			while (i >= 0 && categoria.compareTo(nodo.Oclave(i)) < 0) {
				nodo.Pclave(i + 1, nodo.Oclave(i));
				nodo.Pvalor(i + 1, nodo.Ovalor(i));
				i--;
			}

			if (i >= 0 && categoria.equals(nodo.Oclave(i))) {
				nodo.agregarProductoEnHoja(nodo.Ovalor(i), producto);
			} else {
				nodo.Pclave(i + 1, categoria);
				ListaEnlazada nuevaLista = new ListaEnlazada();
				nuevaLista.insertar(producto);
				nodo.Pvalor(i + 1, nuevaLista);
				nodo.Pcuenta(nodo.Ocuenta() + 1);
			}
		} else {
			while (i >= 0 && categoria.compareTo(nodo.Oclave(i)) < 0) {
				i--;
			}
			i++;

			NodoBPlus hijo = nodo.Orama(i);
			if (hijo != null && hijo.nodoLLeno()) {
				dividirNodo(nodo, i, hijo);
				if (categoria.compareTo(nodo.Oclave(i)) >= 0) {
					i++;
				}
			}

			insertarEnNodoNoLleno(nodo.Orama(i), producto);
		}
	}

	private void dividirNodo(NodoBPlus nodoPadre, int indice, NodoBPlus nodoLleno) {
		if (nodoPadre == null || nodoLleno == null) {
			return;
		}

		boolean esHoja = nodoLleno.esHoja();
		int totalClaves = nodoLleno.Ocuenta();
		int mitad = totalClaves / 2;

		NodoBPlus nodoNuevo = new NodoBPlus(orden, esHoja);

		if (esHoja) {
			int nuevaCuenta = totalClaves - mitad;
			for (int i = 0; i < nuevaCuenta; i++) {
				nodoNuevo.Pclave(i, nodoLleno.Oclave(mitad + i));
				nodoNuevo.Pvalor(i, nodoLleno.Ovalor(mitad + i));
			}
			nodoNuevo.Pcuenta(nuevaCuenta);
			nodoLleno.Pcuenta(mitad);

			nodoNuevo.PramaSiguiente(nodoLleno.OramaSiguiente());
			nodoLleno.PramaSiguiente(nodoNuevo);
		} else {
			String separador = nodoLleno.Oclave(mitad);
			int nuevaCuenta = totalClaves - mitad - 1;

			for (int i = 0; i < nuevaCuenta; i++) {
				nodoNuevo.Pclave(i, nodoLleno.Oclave(mitad + 1 + i));
			}
			for (int i = 0; i <= nuevaCuenta; i++) {
				nodoNuevo.Prama(i, nodoLleno.Orama(mitad + 1 + i));
			}

			nodoNuevo.Pcuenta(nuevaCuenta);
			nodoLleno.Pcuenta(mitad);

			for (int i = nodoPadre.Ocuenta(); i >= indice + 1; i--) {
				nodoPadre.Prama(i + 1, nodoPadre.Orama(i));
			}
			for (int i = nodoPadre.Ocuenta() - 1; i >= indice; i--) {
				nodoPadre.Pclave(i + 1, nodoPadre.Oclave(i));
			}

			nodoPadre.Pclave(indice, separador);
			nodoPadre.Prama(indice + 1, nodoNuevo);
			nodoPadre.Pcuenta(nodoPadre.Ocuenta() + 1);
			return;
		}

		String separador = nodoNuevo.Oclave(0);

		for (int i = nodoPadre.Ocuenta(); i >= indice + 1; i--) {
			nodoPadre.Prama(i + 1, nodoPadre.Orama(i));
		}
		for (int i = nodoPadre.Ocuenta() - 1; i >= indice; i--) {
			nodoPadre.Pclave(i + 1, nodoPadre.Oclave(i));
		}

		nodoPadre.Pclave(indice, separador);
		nodoPadre.Prama(indice + 1, nodoNuevo);
		nodoPadre.Pcuenta(nodoPadre.Ocuenta() + 1);
	}

	private void eliminar(NodoBPlus nodo, String categoria) {
		if (nodo == null) {
			return;
		}

		int i = 0;
		while (i < nodo.Ocuenta() && categoria.compareTo(nodo.Oclave(i)) > 0) {
			i++;
		}

		boolean encontrado = i < nodo.Ocuenta() && categoria.equals(nodo.Oclave(i));

		if (nodo.esHoja()) {
			if (encontrado) {
				for (int j = i; j < nodo.Ocuenta() - 1; j++) {
					nodo.Pclave(j, nodo.Oclave(j + 1));
					nodo.Pvalor(j, nodo.Ovalor(j + 1));
				}

				int ultima = nodo.Ocuenta() - 1;
				nodo.Pclave(ultima, "");
				nodo.Pvalor(ultima, null);
				nodo.Pcuenta(ultima);
			}
		} else {
			if (!encontrado) {
				boolean esUltimo = i == nodo.Ocuenta();

				if (nodo.Orama(i) != null && nodo.Orama(i).Ocuenta() < (orden / 2)) {
					llenarNodo(nodo, i);
				}

				if (esUltimo && i > nodo.Ocuenta()) {
					eliminar(nodo.Orama(i - 1), categoria);
				} else {
					eliminar(nodo.Orama(i), categoria);
				}
			}
		}
	}

	private void llenarNodo(NodoBPlus nodo, int pos) {
		if (pos != 0 && nodo.Orama(pos - 1) != null && nodo.Orama(pos - 1).Ocuenta() >= orden / 2) {
			tomarDelAnterior(nodo, pos);
		} else if (pos != nodo.Ocuenta() && nodo.Orama(pos + 1) != null
				&& nodo.Orama(pos + 1).Ocuenta() >= orden / 2) {
			tomarDelSiguiente(nodo, pos);
		} else if (pos != 0) {
			fusionarNodos(nodo, pos);
		} else if (pos != nodo.Ocuenta()) {
			fusionarNodos(nodo, pos + 1);
		}
	}

	private void tomarDelAnterior(NodoBPlus nodo, int pos) {
		NodoBPlus hijo = nodo.Orama(pos);
		NodoBPlus hermanoAnterior = nodo.Orama(pos - 1);

		for (int i = hijo.Ocuenta() - 1; i >= 0; i--) {
			hijo.Pclave(i + 1, hijo.Oclave(i));
			if (hijo.esHoja()) {
				hijo.Pvalor(i + 1, hijo.Ovalor(i));
			} else {
				hijo.Prama(i + 1, hijo.Orama(i));
			}
		}

		if (!hijo.esHoja()) {
			hijo.Prama(0, hermanoAnterior.Orama(hermanoAnterior.Ocuenta()));
		}

		hijo.Pclave(0, nodo.Oclave(pos - 1));
		if (hijo.esHoja()) {
			hijo.Pvalor(0, null);
		}

		nodo.Pclave(pos - 1, hermanoAnterior.Oclave(hermanoAnterior.Ocuenta() - 1));

		hijo.Pcuenta(hijo.Ocuenta() + 1);
		hermanoAnterior.Pcuenta(hermanoAnterior.Ocuenta() - 1);
	}

	private void tomarDelSiguiente(NodoBPlus nodo, int pos) {
		NodoBPlus hijo = nodo.Orama(pos);
		NodoBPlus hermanoSiguiente = nodo.Orama(pos + 1);

		hijo.Pclave(hijo.Ocuenta(), nodo.Oclave(pos));
		if (hijo.esHoja()) {
			hijo.Pvalor(hijo.Ocuenta(), null);
		}

		if (!hijo.esHoja()) {
			hijo.Prama(hijo.Ocuenta() + 1, hermanoSiguiente.Orama(0));
		}

		nodo.Pclave(pos, hermanoSiguiente.Oclave(0));

		for (int i = 0; i < hermanoSiguiente.Ocuenta() - 1; i++) {
			hermanoSiguiente.Pclave(i, hermanoSiguiente.Oclave(i + 1));
			if (hermanoSiguiente.esHoja()) {
				hermanoSiguiente.Pvalor(i, hermanoSiguiente.Ovalor(i + 1));
			} else {
				hermanoSiguiente.Prama(i, hermanoSiguiente.Orama(i + 1));
			}
		}

		if (!hermanoSiguiente.esHoja()) {
			hermanoSiguiente.Prama(hermanoSiguiente.Ocuenta() - 1,
					hermanoSiguiente.Orama(hermanoSiguiente.Ocuenta()));
		}

		hijo.Pcuenta(hijo.Ocuenta() + 1);
		hermanoSiguiente.Pcuenta(hermanoSiguiente.Ocuenta() - 1);
	}

	private void fusionarNodos(NodoBPlus nodo, int pos) {
		NodoBPlus hijo = nodo.Orama(pos - 1);
		NodoBPlus hermano = nodo.Orama(pos);

		hijo.Pclave(hijo.Ocuenta(), nodo.Oclave(pos - 1));

		for (int i = 0; i < hermano.Ocuenta(); i++) {
			hijo.Pclave(hijo.Ocuenta() + 1, hermano.Oclave(i));
			if (hermano.esHoja()) {
				hijo.Pvalor(hijo.Ocuenta(), hermano.Ovalor(i));
			} else {
				hijo.Prama(hijo.Ocuenta(), hermano.Orama(i));
			}
		}

		if (!hermano.esHoja()) {
			hijo.Prama(hijo.Ocuenta() + hermano.Ocuenta(), hermano.Orama(hermano.Ocuenta()));
		}

		hijo.Pcuenta(hijo.Ocuenta() + hermano.Ocuenta() + 1);

		for (int i = pos - 1; i < nodo.Ocuenta() - 1; i++) {
			nodo.Pclave(i, nodo.Oclave(i + 1));
			nodo.Prama(i, nodo.Orama(i + 1));
		}
		nodo.Prama(nodo.Ocuenta() - 1, nodo.Orama(nodo.Ocuenta()));

		nodo.Pcuenta(nodo.Ocuenta() - 1);
	}

	private void validarProducto(Product producto, String operacion) {
		if (producto == null) {
			throw new IllegalArgumentException(operacion + ": producto nulo");
		}

		String categoria = producto.getCategory();
		if (categoria == null || categoria.trim().isEmpty()) {
			throw new IllegalArgumentException(operacion + ": categoria vacia");
		}
	}
}
