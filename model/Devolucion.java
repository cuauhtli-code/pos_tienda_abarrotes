package pos_tienda_abarrotes.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Devolucion {
    private int idDevolucion;
    private int idVenta;
    private Venta venta;
    private int idUsuario;
    private Usuario usuario;
    private String motivo;
    private double montoDevuelto;
    private Date fechaDevolucion;
    private List<DetalleDevolucion> detalles;
    
    public Devolucion() {
        this.detalles = new ArrayList<>();
    }
    
    public Devolucion(int idDevolucion, int idVenta, int idUsuario, String motivo,
                     double montoDevuelto, Date fechaDevolucion) {
        this.idDevolucion = idDevolucion;
        this.idVenta = idVenta;
        this.idUsuario = idUsuario;
        this.motivo = motivo;
        this.montoDevuelto = montoDevuelto;
        this.fechaDevolucion = fechaDevolucion;
        this.detalles = new ArrayList<>();
    }
    
    public int getIdDevolucion() { return idDevolucion; }
    
    public void setIdDevolucion(int idDevolucion) { 
        this.idDevolucion = idDevolucion; 
    }
    
    public int getIdVenta() { return idVenta; }
    
    public void setIdVenta(int idVenta) { 
        this.idVenta = idVenta; 
    }
    
    public Venta getVenta() { return venta; }
    
    public void setVenta(Venta venta) { 
        this.venta = venta; 
    }
    
    public int getIdUsuario() { return idUsuario; }
    
    public void setIdUsuario(int idUsuario) { 
        this.idUsuario = idUsuario; 
    }
    
    public Usuario getUsuario() { return usuario; }
    
    public void setUsuario(Usuario usuario) { 
        this.usuario = usuario; 
    }
    
    public String getMotivo() { return motivo; }
    
    public void setMotivo(String motivo) { 
        this.motivo = motivo; 
    }
    
    public double getMontoDevuelto() { return montoDevuelto; }
    
    public void setMontoDevuelto(double montoDevuelto) { 
        this.montoDevuelto = montoDevuelto; 
    }
    
    public Date getFechaDevolucion() { return fechaDevolucion; }
    
    public void setFechaDevolucion(Date fechaDevolucion) { 
        this.fechaDevolucion = fechaDevolucion; 
    }
    
    public List<DetalleDevolucion> getDetalles() { return detalles; }
    
    public void setDetalles(List<DetalleDevolucion> detalles) { 
        this.detalles = detalles; 
    }
    
    public void agregarDetalle(DetalleDevolucion detalle) {
        this.detalles.add(detalle);
    }
    
    @Override
    public String toString() {
        return "Devolución #" + idDevolucion + " - $" + montoDevuelto;
    }
}