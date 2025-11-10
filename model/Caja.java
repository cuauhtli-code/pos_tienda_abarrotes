package pos_tienda_abarrotes.model;

import java.util.Date;

public class Caja {
    private int idCaja;
    private String nombreCaja;
    private String ubicacion;
    private boolean activo;
    private Date fechaCreacion;
    
    public Caja() {}
    
    public Caja(int idCaja, String nombreCaja, String ubicacion, boolean activo, Date fechaCreacion) {
        this.idCaja = idCaja;
        this.nombreCaja = nombreCaja;
        this.ubicacion = ubicacion;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }
    
    public int getIdCaja() { return idCaja; }
    public void setIdCaja(int idCaja) { this.idCaja = idCaja; }
    
    public String getNombreCaja() { return nombreCaja; }
    public void setNombreCaja(String nombreCaja) { this.nombreCaja = nombreCaja; }
    
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    @Override
    public String toString() {
        return nombreCaja;
    }
}
