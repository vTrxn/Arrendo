// package: Modelo JPA de Contrato
package co.edu.ue.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idInmueble;
    private Long idArrendatario;
    private String fechaInicio;
    private String fechaFin;
    private Double canonMensual;
    private String estadoPago;

    public Contrato() {}

    public Contrato(Long idInmueble, Long idArrendatario, String fechaInicio, String fechaFin, Double canonMensual, String estadoPago) {
        this.idInmueble = idInmueble;
        this.idArrendatario = idArrendatario;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.canonMensual = canonMensual;
        this.estadoPago = estadoPago;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdInmueble() { return idInmueble; }
    public void setIdInmueble(Long idInmueble) { this.idInmueble = idInmueble; }

    public Long getIdArrendatario() { return idArrendatario; }
    public void setIdArrendatario(Long idArrendatario) { this.idArrendatario = idArrendatario; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public Double getCanonMensual() { return canonMensual; }
    public void setCanonMensual(Double canonMensual) { this.canonMensual = canonMensual; }

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
}