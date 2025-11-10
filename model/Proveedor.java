package pos_tienda_abarrotes.model;

import java.util.Date;

public class Proveedor {
    private int idProveedor;
    private String nombreProveedor;
    private String rfc;
    private String telefono;
    private String email;
    private String direccion;
    private String contacto;
    private boolean activo;
    private Date fechaCreacion;
    
    public Proveedor() {}
    
    public Proveedor(int idProveedor, String nombreProveedor, String rfc, String telefono,
                     String email, String direccion, String contacto, boolean activo, Date fechaCreacion) {
        this.idProveedor = idProveedor;
        this.nombreProveedor = nombreProveedor;
        this.rfc = rfc;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.contacto = contacto;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }
    
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }
    
    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }
    
    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    @Override
    public String toString() {
        return nombreProveedor;
    }
}
