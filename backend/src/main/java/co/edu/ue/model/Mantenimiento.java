// package: Modelo JPA de Mantenimiento
package co.edu.ue.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mantenimientos")
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idInmueble;
    private String descripcionDano;
    private String fechaReporte;
    private String rutaFoto;
    private String estadoMantenimiento;

    public Mantenimiento() {}

    public Mantenimiento(Long idInmueble, String descripcionDano, String fechaReporte, String rutaFoto, String estadoMantenimiento) {
        this.idInmueble = idInmueble;
        this.descripcionDano = descripcionDano;
        this.fechaReporte = fechaReporte;
        this.rutaFoto = rutaFoto;
        this.estadoMantenimiento = estadoMantenimiento;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdInmueble() { return idInmueble; }
    public void setIdInmueble(Long idInmueble) { this.idInmueble = idInmueble; }

    public String getDescripcionDano() { return descripcionDano; }
    public void setDescripcionDano(String descripcionDano) { this.descripcionDano = descripcionDano; }

    public String getFechaReporte() { return fechaReporte; }
    public void setFechaReporte(String fechaReporte) { this.fechaReporte = fechaReporte; }

    public String getRutaFoto() { return rutaFoto; }
    public void setRutaFoto(String rutaFoto) { this.rutaFoto = rutaFoto; }

    public String getEstadoMantenimiento() { return estadoMantenimiento; }
    public void setEstadoMantenimiento(String estadoMantenimiento) { this.estadoMantenimiento = estadoMantenimiento; }
}