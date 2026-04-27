package com.supermarkets.helpers;

public class LoadStats {
    public int totalLineas;
    public int productosExitosos;
    public int erroresLinea;
    public int erroresDuplicados;
    public int erroresFecha;
    public int erroresNumeros;
    public int erroresOtros;

    public LoadStats() {
        this.totalLineas = 0;
        this.productosExitosos = 0;
        this.erroresLinea = 0;
        this.erroresDuplicados = 0;
        this.erroresFecha = 0;
        this.erroresNumeros = 0;
        this.erroresOtros = 0;
    }

    public LoadStats(LoadStats other) {
        this.totalLineas = other.totalLineas;
        this.productosExitosos = other.productosExitosos;
        this.erroresLinea = other.erroresLinea;
        this.erroresDuplicados = other.erroresDuplicados;
        this.erroresFecha = other.erroresFecha;
        this.erroresNumeros = other.erroresNumeros;
        this.erroresOtros = other.erroresOtros;
    }
}
