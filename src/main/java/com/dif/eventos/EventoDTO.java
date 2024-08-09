package com.dif.eventos;

import java.time.LocalDateTime;

public class EventoDTO {
    public String id;
    public String nombreDeEvento;
    public String fechaDeEvento;
    public String horaDeEvento;
    public String lugarDeEvento;
    public String organizador;

    public EventoDTO() {
        this.id = "";
        this.nombreDeEvento = "";
        this.fechaDeEvento = "";
        this.horaDeEvento = "";
        this.lugarDeEvento = "";
        this.organizador = "";
    }

    public EventoDTO(String id, String nombreDeEvento, String fechaDeEvento, String horaDeEvento, String lugarDeEvento, String organizador) {
        this.id = id;
        this.nombreDeEvento = nombreDeEvento;
        this.fechaDeEvento = fechaDeEvento;
        this.horaDeEvento = horaDeEvento;
        this.lugarDeEvento = lugarDeEvento;
        this.organizador = organizador;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreDeEvento() {
        return nombreDeEvento;
    }

    public void setNombreDeEvento(String nombreDeEvento) {
        this.nombreDeEvento = nombreDeEvento;
    }

    public String getFechaDeEvento() {
        return fechaDeEvento;
    }

    public void setFechaDeEvento(String fechaDeEvento) {
        this.fechaDeEvento = fechaDeEvento;
    }

    public String getHoraDeEvento() {
        return horaDeEvento;
    }

    public void setHoraDeEvento(String horaDeEvento) {
        this.horaDeEvento = horaDeEvento;
    }

    public String getLugarDeEvento() {
        return lugarDeEvento;
    }

    public void setLugarDeEvento(String lugarDeEvento) {
        this.lugarDeEvento = lugarDeEvento;
    }

    public String getOrganizador() {
        return organizador;
    }

    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }

    @Override
    public String toString() {
        return "EventoDTO{" +
                "id='" + id + '\'' +
                ", nombreDeEvento='" + nombreDeEvento + '\'' +
                ", fechaDeEvento='" + fechaDeEvento + '\'' +
                ", horaDeEvento='" + horaDeEvento + '\'' +
                ", lugarDeEvento='" + lugarDeEvento + '\'' +
                ", organizador='" + organizador + '\'' +
                '}';
    }
}
