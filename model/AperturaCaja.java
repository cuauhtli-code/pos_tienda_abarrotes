package pos_tienda_abarrotes.model;

import java.util.Date;

public class AperturaCaja {
    private int idApertura;
    private int idCaja;
    private Caja caja;
    private int idUsuario;
    private Usuario usuario;
    private double montoInicial;
    private Date fechaApertura;
    private String estado;

    public AperturaCaja() {}
    
    public AperturaCaja(int idApertura, int idCaja, int idUsuario, double montoInicial,
                        Date fechaApertura, String estado) {
        this.idApertura = idApertura;
        this.idCaja = idCaja;
        this.idUsuario = idUsuario;
        this.montoInicial = montoInicial;
        this.fechaApertura = fechaApertura;
        this.estado = estado;
    }
    
    public int getIdApertura() { return idApertura; }
    public void setIdApertura(int idApertura) { this.idApertura = idApertura; }
    
    public int getIdCaja() { return idCaja; }
    public void setIdCaja(int idCaja) { this.idCaja = idCaja; }
    
    public Caja getCaja() { return caja; }
    public void setCaja(Caja caja) { this.caja = caja; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public double getMontoInicial() { return montoInicial; }
    public void setMontoInicial(double montoInicial) { this.montoInicial = montoInicial; }
    
    public Date getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(Date fechaApertura) { this.fechaApertura = fechaApertura; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    @Override
    public String toString() {
        return "Apertura #" + idApertura + " - " + estado;
    }
}
