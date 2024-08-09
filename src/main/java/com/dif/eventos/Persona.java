package com.dif.eventos;

public class Persona {
    public String id;
    public String pNombre;
    public String sNombre;
    public String pApellido;
    public String mApellido;
    public String correo;
    public boolean esAsistente;
    public boolean esOrganizador;

    public Persona() {
        this.id = "";
        this.pNombre = "";
        this.sNombre = "";
        this.pApellido = "";
        this.mApellido = "";
        this.correo = "";
        this.esAsistente = false;
        this.esOrganizador = false;
    }

    public Persona(String id, String pNombre, String sNombre, String pApellido, String mApellido, String correo, boolean esAsistente, boolean esOrganizador) {
        this.id = id;
        this.pNombre = pNombre;
        this.sNombre = sNombre;
        this.pApellido = pApellido;
        this.mApellido = mApellido;
        this.correo = correo;
        this.esAsistente = esAsistente;
        this.esOrganizador = esOrganizador;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPNombre() {
        return pNombre;
    }

    public void setpNombre(String pNombre) {
        this.pNombre = pNombre;
    }

    public String getSNombre() {
        return sNombre;
    }

    public void setsNombre(String sNombre) {
        this.sNombre = sNombre;
    }

    public String getPApellido() {
        return pApellido;
    }

    public void setpApellido(String pApellido) {
        this.pApellido = pApellido;
    }

    public String getMApellido() {
        return mApellido;
    }

    public void setmApellido(String mApellido) {
        this.mApellido = mApellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean getEsAsistente() {
        return esAsistente;
    }

    public void setEsAsistente(boolean esAsistente) {
        this.esAsistente = esAsistente;
    }

    public boolean getEsOrganizador() {
        return esOrganizador;
    }

    public void setEsOrganizador(boolean esOrganizador) {
        this.esOrganizador = esOrganizador;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "id='" + id + '\'' +
                ", pNombre='" + pNombre + '\'' +
                ", sNombre='" + sNombre + '\'' +
                ", pApellido='" + pApellido + '\'' +
                ", mApellido='" + mApellido + '\'' +
                ", correo='" + correo + '\'' +
                ", esAsistente=" + esAsistente +
                ", esOrganizador=" + esOrganizador +
                '}';
    }
}
