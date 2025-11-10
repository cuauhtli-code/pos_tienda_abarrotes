package pos_tienda_abarrotes.model;

import java.util.Date;

public class MovimientoCaja {
    private int idMovimiento;
    private int idApertura;
    private String tipoMovimiento;
    private String concepto;
    private double monto;
    private int idUsuario;
    private Usuario usuario;
    private Date fechaMovimiento;
    
    public MovimientoCaja() {}
    
    public MovimientoCaja(int idMovimiento, int idApertura, String tipoMovimiento,
                          String concepto, double monto, int idUsuario, Date fechaMovimiento) {
        this.idMovimiento = idMovimiento;
        this.idApertura = idApertura;
        this.tipoMovimiento = tipoMovimiento;
        this.concepto = concepto;
        this.monto = monto;
        this.idUsuario = idUsuario;
        this.fechaMovimiento = fechaMovimiento;
    }
    
    public int getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(int idMovimiento) { this.idMovimiento = idMovimiento; }
    
    public int getIdApertura() { return idApertura; }
    public void setIdApertura(int idApertura) { this.idApertura = idApertura; }
    
    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
    
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public Date getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(Date fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
    
    @Override
    public String toString() {
        return tipoMovimiento + ": " + concepto + " - $" + monto;
    }
}
