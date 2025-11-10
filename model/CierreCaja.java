package pos_tienda_abarrotes.model;

import java.util.Date;

public class CierreCaja {
    private int idCierre;
    private int idApertura;
    private AperturaCaja aperturaCaja;
    private double montoFinalSistema;
    private double montoFinalFisico;
    private double diferencia;
    private double totalVentas;
    private double totalEfectivo;
    private double totalTarjeta;
    private double totalTransferencia;
    private double totalVales;
    private Date fechaCierre;
    private String observaciones;
    
    public CierreCaja() {}
    
    public CierreCaja(int idCierre, int idApertura, double montoFinalSistema, double montoFinalFisico,
                      double diferencia, double totalVentas, double totalEfectivo, double totalTarjeta,
                      double totalTransferencia, double totalVales, Date fechaCierre, String observaciones) {
        this.idCierre = idCierre;
        this.idApertura = idApertura;
        this.montoFinalSistema = montoFinalSistema;
        this.montoFinalFisico = montoFinalFisico;
        this.diferencia = diferencia;
        this.totalVentas = totalVentas;
        this.totalEfectivo = totalEfectivo;
        this.totalTarjeta = totalTarjeta;
        this.totalTransferencia = totalTransferencia;
        this.totalVales = totalVales;
        this.fechaCierre = fechaCierre;
        this.observaciones = observaciones;
    }
    
    public int getIdCierre() { return idCierre; }
    public void setIdCierre(int idCierre) { this.idCierre = idCierre; }
    
    public int getIdApertura() { return idApertura; }
    public void setIdApertura(int idApertura) { this.idApertura = idApertura; }
    
    public AperturaCaja getAperturaCaja() { return aperturaCaja; }
    public void setAperturaCaja(AperturaCaja aperturaCaja) { this.aperturaCaja = aperturaCaja; }
    
    public double getMontoFinalSistema() { return montoFinalSistema; }
    public void setMontoFinalSistema(double montoFinalSistema) { this.montoFinalSistema = montoFinalSistema; }
    
    public double getMontoFinalFisico() { return montoFinalFisico; }
    public void setMontoFinalFisico(double montoFinalFisico) { this.montoFinalFisico = montoFinalFisico; }
    
    public double getDiferencia() { return diferencia; }
    public void setDiferencia(double diferencia) { this.diferencia = diferencia; }
    
    public double getTotalVentas() { return totalVentas; }
    public void setTotalVentas(double totalVentas) { this.totalVentas = totalVentas; }
    
    public double getTotalEfectivo() { return totalEfectivo; }
    public void setTotalEfectivo(double totalEfectivo) { this.totalEfectivo = totalEfectivo; }
    
    public double getTotalTarjeta() { return totalTarjeta; }
    public void setTotalTarjeta(double totalTarjeta) { this.totalTarjeta = totalTarjeta; }
    
    public double getTotalTransferencia() { return totalTransferencia; }
    public void setTotalTransferencia(double totalTransferencia) { this.totalTransferencia = totalTransferencia; }
    
    public double getTotalVales() { return totalVales; }
    public void setTotalVales(double totalVales) { this.totalVales = totalVales; }
    
    public Date getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(Date fechaCierre) { this.fechaCierre = fechaCierre; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    @Override
    public String toString() {
        return "Cierre #" + idCierre + " - Diferencia: $" + diferencia;
    }
}
