package com.example.agenda.Objetos;

public class Nota {

    String id_nota, uid_usuario, correo, fecha_hora_actual, titulo, descripcion, fecha_nota, hora_nota, notificacion, categoria, contacto, estado;

    public Nota() {
    }

    public Nota(String id_nota, String uid_usuario, String correo, String fecha_hora_actual, String titulo, String descripcion, String fecha_nota, String hora_nota, String notificacion, String categoria, String contacto, String estado) {
        this.id_nota = id_nota;
        this.uid_usuario = uid_usuario;
        this.correo = correo;
        this.fecha_hora_actual = fecha_hora_actual;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha_nota = fecha_nota;
        this.hora_nota = hora_nota;
        this.notificacion = notificacion;
        this.categoria = categoria;
        this.contacto = contacto;
        this.estado = estado;
    }

    public String getId_nota() {
        return id_nota;
    }

    public void setId_nota(String id_nota) {
        this.id_nota = id_nota;
    }

    public String getUid_usuario() {
        return uid_usuario;
    }

    public void setUid_usuario(String uid_usuario) {
        this.uid_usuario = uid_usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFecha_hora_actual() {
        return fecha_hora_actual;
    }

    public void setFecha_hora_actual(String fecha_hora_actual) {
        this.fecha_hora_actual = fecha_hora_actual;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha_nota() {
        return fecha_nota;
    }

    public void setFecha_nota(String fecha_nota) {
        this.fecha_nota = fecha_nota;
    }

    public String getHora_nota() {
        return hora_nota;
    }

    public void setHora_nota(String hora_nota) {
        this.hora_nota = hora_nota;
    }

    public String getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(String notificacion) {
        this.notificacion = notificacion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
