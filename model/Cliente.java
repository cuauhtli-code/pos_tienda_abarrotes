package pos_tienda_abarrotes.model;

import java.util.Date;

public class Cliente {
    private int idCliente;
    private String nombreCliente;
    private String telefono;
    private String email;
    private String direccion;
    private String rfc;
    private int puntosFidelidad;
    private boolean activo;
    private Date fechaRegistro;
    
    public Cliente() {}

    public Cliente(int idCliente, String nombreCliente, String telefono, String email,
                   String direccion, String rfc, int puntosFidelidad, boolean activo, Date fechaRegistro) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.rfc = rfc;
        this.puntosFidelidad = puntosFidelidad;
        this.activo = activo;
        this.fechaRegistro = fechaRegistro;
    }
    
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    
    public int getPuntosFidelidad() { return puntosFidelidad; }
    public void setPuntosFidelidad(int puntosFidelidad) { this.puntosFidelidad = puntosFidelidad; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    @Override
    public String toString() {
        return nombreCliente;
    }
}
