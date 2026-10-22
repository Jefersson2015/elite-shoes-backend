package com.elite.elitebackend.dto;

public class UsuarioRequest {
    private String nombre;
    private String telefono;
    private String username;
    private String password;
    private String roll;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRoll() { return roll; }
    public void setRoll(String roll) { this.roll = roll; }
}