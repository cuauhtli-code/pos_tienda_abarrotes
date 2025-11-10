package pos_tienda_abarrotes.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private int idVenta;
    private String folio;
    private int idUsuario;
    private Usuario usuario;
    private int idCliente;
    private Cliente cliente;
    private int idApertura;
    private AperturaCaja aperturaCaja;
    private double subtotal;
    private double impuestos;
    private double descuento;
    private double total;
    private String metodoPago;
    private String estado;
    private Date fechaVenta;
    private String observaciones;
    private List<DetalleVenta> detalles;
    
    public Venta() {
        this.detalles = new ArrayList<>();
    }

    public Venta(int idVenta, String folio, int idUsuario, int idCliente, int idApertura,
                 double subtotal, double impuestos, double descuento, double total,
                 String metodoPago, String estado, Date fechaVenta, String observaciones) {
        this.idVenta = idVenta;
        this.folio = folio;
        this.idUsuario = idUsuario;
        this.idCliente = idCliente;
        this.idApertura = idApertura;
        this.subtotal = subtotal;
        this.impuestos = impuestos;
        this.descuento = descuento;
        this.total = total;
        this.metodoPago = metodoPago;
        this.estado = estado;
        this.fechaVenta = fechaVenta;
        this.observaciones = observaciones;
        this.detalles = new ArrayList<>();
    }
   
    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }
    
    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public int getIdApertura() { return idApertura; }
    public void setIdApertura(int idApertura) { this.idApertura = idApertura; }
    
    public AperturaCaja getAperturaCaja() { return aperturaCaja; }
    public void setAperturaCaja(AperturaCaja aperturaCaja) { this.aperturaCaja = aperturaCaja; }
    
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    
    public double getImpuestos() { return impuestos; }
    public void setImpuestos(double impuestos) { this.impuestos = impuestos; }
    
    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }
    
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public Date getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(Date fechaVenta) { this.fechaVenta = fechaVenta; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
    
    // Métodos de utilidad
    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
    }
    
    public void calcularTotales() {
        subtotal = 0;
        for (DetalleVenta detalle : detalles) {
            subtotal += detalle.getSubtotal();
        }
        total = subtotal + impuestos - descuento;
    }
    
    @Override
    public String toString() {
        return "Venta " + folio + " - $" + total;
    }
}
