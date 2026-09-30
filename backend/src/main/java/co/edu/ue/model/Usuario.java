// package: Modelo JPA de Usuario para la base de datos PostgreSQL
package co.edu.ue.model;

import jakarta.persistence.*;

// @Entity: Mapea la clase a la tabla "usuarios" en PostgreSQL
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clave primaria autoincremental
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombreUsuario;

    @Column(nullable = false)
    private String claveEncriptada; // Contraseña encriptada con BCrypt

    private String rol;

    public Usuario() {}

    public Usuario(String nombreUsuario, String claveEncriptada, String rol) {
        this.nombreUsuario = nombreUsuario;
        this.claveEncriptada = claveEncriptada;
        this.rol = rol;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getClaveEncriptada() { return claveEncriptada; }
    public void setClaveEncriptada(String claveEncriptada) { this.claveEncriptada = claveEncriptada; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}