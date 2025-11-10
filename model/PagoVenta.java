package pos_tienda_abarrotes.model;

import java.util.Date;

public class PagoVenta {
    private int idPago;
    private int idVenta;
    private String metodoPago;
    private double monto;
    private String referencia;
    private Date fechaPago;
    
    public PagoVenta() {}
    
    public PagoVenta(int idPago, int idVenta, String metodoPago, double monto,
                     String referencia, Date fechaPago) {
        this.idPago = idPago;
        this.idVenta = idVenta;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.referencia = referencia;
        this.fechaPago = fechaPago;
    }
    
    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }
    
    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }
    
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    
    public Date getFechaPago() { return fechaPago; }
    public void setFechaPago(Date fechaPago) { this.fechaPago = fechaPago; }
    
    @Override
    public String toString() {
        return metodoPago + ": $" + monto;
    }
}
