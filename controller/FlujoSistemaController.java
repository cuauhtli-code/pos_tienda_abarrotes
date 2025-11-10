package pos_tienda_abarrotes.controller;

import pos_tienda_abarrotes.dao.AperturaCajaDAO;
import pos_tienda_abarrotes.dao.CierreCajaDAO;
import pos_tienda_abarrotes.dao.MovimientoCajaDAO;
import pos_tienda_abarrotes.model.AperturaCaja;
import pos_tienda_abarrotes.model.CierreCaja;

public class FlujoSistemaController {
    
    private AperturaCajaDAO aperturaCajaDAO;
    private CierreCajaDAO cierreCajaDAO;
    private static AperturaCaja aperturaActual;
    
    public FlujoSistemaController() {
        this.aperturaCajaDAO = new AperturaCajaDAO();
        this.cierreCajaDAO = new CierreCajaDAO();
    }
   
    public boolean abrirCaja(int idCaja, double montoInicial) {
        if (!LoginController.haySesionActiva()) {
            System.err.println("Debe iniciar sesión primero");
            return false;
        }
        
        int idUsuario = LoginController.getIdUsuarioActual();
        
        if (tieneAperturaActiva()) {
            System.err.println("Ya tiene una caja abierta. Debe cerrarla primero");
            System.out.println("Caja actual: " + aperturaActual.getCaja().getNombreCaja());
            return false;
        }
        
        if (montoInicial < 0) {
            System.err.println("El monto inicial no puede ser negativo");
            return false;
        }
        
        if (aperturaCajaDAO.cajaEstaAbierta(idCaja)) {
            System.err.println("La caja ya está abierta por otro usuario");
            return false;
        }
        
        AperturaCaja apertura = new AperturaCaja();
        apertura.setIdCaja(idCaja);
        apertura.setIdUsuario(idUsuario);
        apertura.setMontoInicial(montoInicial);
        
        boolean resultado = aperturaCajaDAO.abrirCaja(apertura);
        
        if (resultado) {
            aperturaActual = aperturaCajaDAO.obtenerPorId(apertura.getIdApertura());
            System.out.println("\n✓ Caja abierta exitosamente");
            System.out.println("=================================");
            System.out.println("Usuario: " + LoginController.getNombreUsuarioActual());
            System.out.println("Caja: " + aperturaActual.getCaja().getNombreCaja());
            System.out.println("Monto inicial: $" + String.format("%.2f", montoInicial));
            System.out.println("Fecha: " + aperturaActual.getFechaApertura());
            System.out.println("=================================\n");
        }
        
        return resultado;
    }
    
    public boolean cerrarCaja(double montoFisicoContado, String observaciones) {
        if (!LoginController.haySesionActiva()) {
            System.err.println("No hay sesión activa");
            return false;
        }
        
        if (!tieneAperturaActiva()) {
            System.err.println("No tiene una caja abierta");
            return false;
        }
        
        if (montoFisicoContado < 0) {
            System.err.println("El monto físico no puede ser negativo");
            return false;
        }
        
        CierreCaja cierre = cierreCajaDAO.calcularResumenCierre(aperturaActual.getIdApertura());
        
        if (cierre == null) {
            System.err.println("Error al calcular el resumen de cierre");
            return false;
        }
        
        cierre.setMontoFinalFisico(montoFisicoContado);
        double diferencia = montoFisicoContado - cierre.getMontoFinalSistema();
        cierre.setDiferencia(diferencia);
        cierre.setObservaciones(observaciones);
        
        System.out.println("\n=== RESUMEN DE CIERRE DE CAJA ===");
        System.out.println("Caja: " + aperturaActual.getCaja().getNombreCaja());
        System.out.println("Usuario: " + LoginController.getNombreUsuarioActual());
        System.out.println("\n--- VENTAS DEL TURNO ---");
        System.out.println("Total ventas: $" + String.format("%.2f", cierre.getTotalVentas()));
        System.out.println("  Efectivo: $" + String.format("%.2f", cierre.getTotalEfectivo()));
        System.out.println("  Tarjeta: $" + String.format("%.2f", cierre.getTotalTarjeta()));
        System.out.println("  Transferencia: $" + String.format("%.2f", cierre.getTotalTransferencia()));
        System.out.println("  Vales: $" + String.format("%.2f", cierre.getTotalVales()));
        System.out.println("\n--- CONCILIACIÓN ---");
        System.out.println("Monto inicial: $" + String.format("%.2f", aperturaActual.getMontoInicial()));
        System.out.println("Efectivo de ventas: $" + String.format("%.2f", cierre.getTotalEfectivo()));
        System.out.println("Monto esperado (sistema): $" + String.format("%.2f", cierre.getMontoFinalSistema()));
        System.out.println("Monto físico contado: $" + String.format("%.2f", montoFisicoContado));
        System.out.println("Diferencia: $" + String.format("%.2f", diferencia));
        
        if (Math.abs(diferencia) > 0.01) {
            if (diferencia > 0) {
                System.out.println("⚠ SOBRANTE de $" + String.format("%.2f", diferencia));
            } else {
                System.out.println("⚠ FALTANTE de $" + String.format("%.2f", Math.abs(diferencia)));
            }
        } else {
            System.out.println("✓ Caja cuadrada (sin diferencia)");
        }
        System.out.println("=================================\n");

        boolean resultado = cierreCajaDAO.cerrarCaja(cierre);
        
        if (resultado) {
            aperturaActual = null;
            System.out.println("✓ Caja cerrada exitosamente");
        }
        
        return resultado;
    }

    public boolean tieneAperturaActiva() {
        if (!LoginController.haySesionActiva()) {
            return false;
        }
        
        if (aperturaActual != null) {
            AperturaCaja verificacion = aperturaCajaDAO.obtenerPorId(aperturaActual.getIdApertura());
            if (verificacion != null && "ABIERTA".equals(verificacion.getEstado())) {
                return true;
            } else {
                aperturaActual = null;
            }
        }
        
        int idUsuario = LoginController.getIdUsuarioActual();
        aperturaActual = aperturaCajaDAO.obtenerCajaAbierta(idUsuario);
        
        return aperturaActual != null;
    }
    
    public static AperturaCaja getAperturaActual() {
        return aperturaActual;
    }
    
    public static int getIdAperturaActual() {
        if (aperturaActual != null) {
            return aperturaActual.getIdApertura();
        }
        return 0;
    }
    
    public static boolean validarCajaAbierta() {
        if (!LoginController.haySesionActiva()) {
            System.err.println("No hay sesión activa");
            return false;
        }
        
        if (aperturaActual == null) {
            System.err.println("Debe abrir una caja antes de realizar esta operación");
            return false;
        }
        
        return true;
    }
    
    public boolean registrarMovimientoCaja(String tipoMovimiento, String concepto, double monto) {
        if (!validarCajaAbierta()) {
            return false;
        }
        
        if (!"INGRESO".equals(tipoMovimiento) && !"EGRESO".equals(tipoMovimiento)) {
            System.err.println("Tipo de movimiento inválido (debe ser INGRESO o EGRESO)");
            return false;
        }
        
        if (concepto == null || concepto.trim().isEmpty()) {
            System.err.println("El concepto es requerido");
            return false;
        }
        
        if (monto <= 0) {
            System.err.println("El monto debe ser mayor a cero");
            return false;
        }
        
        MovimientoCajaDAO movimientoDAO = new MovimientoCajaDAO();
        pos_tienda_abarrotes.model.MovimientoCaja movimiento = 
            new pos_tienda_abarrotes.model.MovimientoCaja();
        
        movimiento.setIdApertura(aperturaActual.getIdApertura());
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setConcepto(concepto);
        movimiento.setMonto(monto);
        movimiento.setIdUsuario(LoginController.getIdUsuarioActual());
        
        return movimientoDAO.registrar(movimiento);
    }
    
    public boolean puedecerrarSesion() {
        if (!tieneAperturaActiva()) {
            return true;
        }
        
        System.err.println("No puede cerrar sesión con una caja abierta");
        System.out.println("Debe cerrar la caja primero");
        return false;
    }
    
    public void mostrarInformacionCajaActual() {
        if (aperturaActual == null) {
            System.out.println("No hay caja abierta");
            return;
        }
        
        System.out.println("\n=== INFORMACIÓN DE CAJA ACTUAL ===");
        System.out.println("Caja: " + aperturaActual.getCaja().getNombreCaja());
        System.out.println("Usuario: " + LoginController.getNombreUsuarioActual());
        System.out.println("Fecha apertura: " + aperturaActual.getFechaApertura());
        System.out.println("Monto inicial: $" + String.format("%.2f", aperturaActual.getMontoInicial()));
        System.out.println("Estado: " + aperturaActual.getEstado());
        System.out.println("===================================\n");
    }
}
