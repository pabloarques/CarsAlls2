package com.example.carsalls.ui;

public class Usuario {

    String Nombre;
    String Correo;
    String imagenPerfil;

    public Usuario(String nommbre, String correo, String imagenPerfil) {
        Nombre = nommbre;
        Correo = correo;
        this.imagenPerfil = imagenPerfil;
    }


    public Usuario(){

    }


    @Override
    public String toString() {
        return "Usuario{" +
                "Nommbre='" + Nombre + '\'' +
                ", Correo='" + Correo + '\'' +
                ", imagenPerfil='" + imagenPerfil + '\'' +
                '}';
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }
}
