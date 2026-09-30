// package: Modelo JPA de Inmueble
package co.edu.ue.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inmuebles")
public class Inmueble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String direccion;
    private String tipoInmueble;
    private Double precioArriendo;
    private String estado;
    private String uriImagen;

    public Inmueble() {}

    public Inmueble(String direccion, String tipoInmueble, Double precioArriendo, String estado, String uriImagen) {
        this.direccion = direccion;
        this.tipoInmueble = tipoInmueble;
        this.precioArriendo = precioArriendo;
        this.estado = estado;
        this.uriImagen = uriImagen;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTipoInmueble() { return tipoInmueble; }
    public void setTipoInmueble(String tipoInmueble) { this.tipoInmueble = tipoInmueble; }

    public Double getPrecioArriendo() { return precioArriendo; }
    public void setPrecioArriendo(Double precioArriendo) { this.precioArriendo = precioArriendo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getUriImagen() { return uriImagen; }
    public void setUriImagen(String uriImagen) { this.uriImagen = uriImagen; }
}