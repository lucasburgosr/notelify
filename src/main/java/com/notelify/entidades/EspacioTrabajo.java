package com.notelify.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "espacios_de_trabajo")
public class EspacioTrabajo implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id_espacio")
    private String id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @OneToMany
    @JoinColumn(name = "id_espacio_trabajo")
    private List<Tarea> listaTareas;

    @OneToOne
    @JoinColumn(name = "fondo")
    private Imagen fondo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_finalizacion")
    private Date fechaFinalizacion;

    @ManyToMany
    private List<Usuario> listaUsuarios;

    @Column(name = "activo")
    private Boolean activo;

    public EspacioTrabajo(String id, String nombre, List<Tarea> listaTareas, Imagen fondo, Date fechaCreacion, Date fechaFinalizacion,
            List<Usuario> listaUsuarios, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.listaTareas = listaTareas;
        this.fondo = fondo;
        this.fechaCreacion = fechaCreacion;
        this.fechaFinalizacion = fechaFinalizacion;
        this.listaUsuarios = listaUsuarios;
        this.activo = activo;
    }

    public EspacioTrabajo() {
        listaUsuarios = new ArrayList<>();
        listaTareas = new ArrayList<>();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the listaTareas
     */
    public List<Tarea> getListaTareas() {
        return listaTareas;
    }

    /**
     * @param listaTareas the tareas to set
     */
    public void setListaTareas(List<Tarea> listaTareas) {
        this.listaTareas = listaTareas;
    }

    /**
     * @return the fondo
     */
    public Imagen getFondo() {
        return fondo;
    }

    /**
     * @param fondo the fondo to set
     */
    public void setFondo(Imagen fondo) {
        this.fondo = fondo;
    }

    /**
     * @return the fechaCreacion
     */
    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * @param fechaCreacion the fechaCreacion to set
     */
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * @return the listaUsuarios
     */
    public List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    /**
     * @param listaUsuarios the usuarios to set
     */
    public void setListaUsuarios(List<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    /**
     * @return the fechaFinalizacion
     */
    public Date getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    /**
     * @param fechaFinalizacion the fechaFinalizacion to set
     */
    public void setFechaFinalizacion(Date fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    /**
     * @return the activo
     */
    public Boolean getActivo() {
        return activo;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "EspacioTrabajo{" + "id=" + id + ", nombre=" + nombre + ", listaTareas=" + listaTareas + ", fondo=" + fondo + ", fechaCreacion=" + fechaCreacion + ", fechaFinalizacion=" + getFechaFinalizacion() + ", listaUsuarios=" + listaUsuarios + ", activo=" + activo + '}';
    }
}
