package com.notelify.enums;

/*
    Definición de estados por los que pasarán las tareas.
**/
public enum Estado {
    TODO("Pendientes"), IN_PROGRESS("En proceso"), FINISHED("Realizadas"), PAUSED("En pausa");

    private final String valor;

    private Estado(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
