package pos_tienda_abarrotes.controller;

import java.util.List;
import pos_tienda_abarrotes.dao.DevolucionDAO;
import pos_tienda_abarrotes.dao.VentaDAO;
import pos_tienda_abarrotes.dao.AperturaCajaDAO;
import pos_tienda_abarrotes.model.Devolucion;
import pos_tienda_abarrotes.model.DetalleDevolucion;
import pos_tienda_abarrotes.model.DetalleVenta;
import pos_tienda_abarrotes.model.Venta;
import pos_tienda_abarrotes.model.AperturaCaja;

public class DevolucionController {
    private DevolucionDAO devolucionDAO;
    private VentaDAO ventaDAO;
    private AperturaCajaDAO aperturaCajaDAO;

    private static final int DIAS_LIMITE_DEVOLUCION = 30;
   
    public DevolucionController() {
        this.devolucionDAO = new DevolucionDAO();
        this.ventaDAO = new VentaDAO();
        this.aperturaCajaDAO = new AperturaCajaDAO();
    }
    
    public boolean registrarDevolucion(String folioVenta, int idUsuario, String motivo,
                                      List<Integer> idsProductos, List<Integer> cantidades) {
    
        if (!LoginController.haySesionActiva()) {
            System.err.println("❌ No hay sesión activa. Debe iniciar sesión primero");
            return false;
        }
        
        if (LoginController.getIdUsuarioActual() != idUsuario) {
            System.err.println("❌ El usuario no coincide con la sesión actual");
            System.err.println("   Usuario solicitado: " + idUsuario);
            System.err.println("   Usuario en sesión: " + LoginController.getIdUsuarioActual());
            return false;
        }
        
        AperturaCaja aperturaActual = aperturaCajaDAO.obtenerCajaAbierta(idUsuario);
        if (aperturaActual == null) {
            System.err.println("❌ Debe tener una caja abierta para procesar devoluciones");
            System.out.println("   Inicie su turno abriendo una caja primero");
            return false;
        }
        
        if (!"ABIERTA".equals(aperturaActual.getEstado())) {
            System.err.println("❌ La caja no está en estado ABIERTA");
            System.err.println("   Estado actual: " + aperturaActual.getEstado());
            return false;
        }
        
        System.out.println("✓ Validaciones de flujo: OK");
        System.out.println("  Caja: " + aperturaActual.getCaja().getNombreCaja());
        System.out.println("  Usuario: " + LoginController.getNombreUsuarioActual());
        
        if (folioVenta == null || folioVenta.trim().isEmpty()) {
            System.err.println("❌ El folio de la venta es requerido");
            return false;
        }
        
        if (idUsuario <= 0) {
            System.err.println("❌ ID de usuario inválido");
            return false;
        }
        
        if (motivo == null || motivo.trim().isEmpty()) {
            System.err.println("❌ El motivo de la devolución es requerido");
            return false;
        }
        
        if (motivo.trim().length() < 10) {
            System.err.println("❌ El motivo debe tener al menos 10 caracteres");
            return false;
        }
        
        if (idsProductos == null || idsProductos.isEmpty() ||
            cantidades == null || cantidades.isEmpty() ||
            idsProductos.size() != cantidades.size()) {
            System.err.println("❌ Debe especificar los productos y cantidades a devolver");
            return false;
        }
        
        Venta venta = ventaDAO.buscarPorFolio(folioVenta.trim());
        if (venta == null) {
            System.err.println("❌ No se encontró la venta con folio: " + folioVenta);
            return false;
        }
        
        if ("CANCELADA".equals(venta.getEstado())) {
            System.err.println("❌ No se puede hacer devolución de una venta cancelada");
            System.err.println("   La venta " + folioVenta + " fue cancelada previamente");
            return false;
        }
        
        long diasTranscurridos = calcularDiasDesdeVenta(venta.getFechaVenta());
        
        if (diasTranscurridos > DIAS_LIMITE_DEVOLUCION) {
            System.err.println("⚠ La venta tiene más de " + DIAS_LIMITE_DEVOLUCION + " días");
            System.err.println("  Fecha de venta: " + venta.getFechaVenta());
            System.err.println("  Días transcurridos: " + diasTranscurridos);
            
            if (!LoginController.esAdministrador()) {
                System.err.println("❌ Requiere autorización de ADMINISTRADOR para devoluciones fuera de plazo");
                System.err.println("   Su rol actual: " + LoginController.getRolUsuarioActual());
                return false;
            } else {
                System.out.println("✓ Autorización de ADMINISTRADOR: Devolución fuera de plazo aprobada");
                System.out.println("  Autorizado por: " + LoginController.getNombreUsuarioActual());
            }
        } else {
            System.out.println("✓ Devolución dentro del plazo permitido (" + diasTranscurridos + " días)");
        }
        
        List<Devolucion> devolucionesPrevias = devolucionDAO.obtenerPorVenta(venta.getIdVenta());
        if (devolucionesPrevias != null && !devolucionesPrevias.isEmpty()) {
            System.out.println("\n⚠ ADVERTENCIA: Esta venta ya tiene " + devolucionesPrevias.size() + " devolución(es) previa(s)");
            
            for (Devolucion devPrev : devolucionesPrevias) {
                System.out.println("  - Devolución #" + devPrev.getIdDevolucion() + 
                                 " por $" + String.format("%.2f", devPrev.getMontoDevuelto()) +
                                 " - " + devPrev.getFechaDevolucion());
            }
            System.out.println();
        }
        
        Devolucion devolucion = new Devolucion();
        devolucion.setIdVenta(venta.getIdVenta());
        devolucion.setIdUsuario(idUsuario);
        devolucion.setMotivo(motivo.trim());
        
        double montoDevuelto = 0;
        int totalProductosDevueltos = 0;
        
        for (int i = 0; i < idsProductos.size(); i++) {
            int idProducto = idsProductos.get(i);
            int cantidad = cantidades.get(i);
            
            if (cantidad <= 0) {
                System.err.println("❌ La cantidad a devolver debe ser mayor a cero");
                return false;
            }
            
            DetalleVenta detalleVenta = buscarDetalleVenta(venta, idProducto);
            
            if (detalleVenta == null) {
                System.err.println("❌ El producto con ID " + idProducto + " no está en la venta original");
                return false;
            }
            
            int cantidadYaDevuelta = obtenerCantidadYaDevuelta(devolucionesPrevias, idProducto);
            int cantidadDisponible = detalleVenta.getCantidad() - cantidadYaDevuelta;
            
            if (cantidad > cantidadDisponible) {
                System.err.println("❌ Cantidad a devolver excede lo disponible");
                System.err.println("   Producto: " + detalleVenta.getProducto().getNombreProducto());
                System.err.println("   Vendido originalmente: " + detalleVenta.getCantidad());
                System.err.println("   Ya devuelto: " + cantidadYaDevuelta);
                System.err.println("   Disponible para devolución: " + cantidadDisponible);
                System.err.println("   Solicitado: " + cantidad);
                return false;
            }
            
            DetalleDevolucion detalle = new DetalleDevolucion();
            detalle.setIdProducto(idProducto);
            detalle.setProducto(detalleVenta.getProducto());
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(detalleVenta.getPrecioUnitario());
            detalle.setSubtotal(cantidad * detalleVenta.getPrecioUnitario());
            
            devolucion.agregarDetalle(detalle);
            montoDevuelto += detalle.getSubtotal();
            totalProductosDevueltos += cantidad;
            
            System.out.println("  ✓ Producto agregado: " + detalleVenta.getProducto().getNombreProducto() +
                             " x" + cantidad + " = $" + String.format("%.2f", detalle.getSubtotal()));
        }
        
        devolucion.setMontoDevuelto(montoDevuelto);
        
        if (montoDevuelto > venta.getTotal()) {
            System.err.println("❌ El monto de devolución no puede exceder el total de la venta");
            System.err.println("   Total venta: $" + String.format("%.2f", venta.getTotal()));
            System.err.println("   Monto devolución: $" + String.format("%.2f", montoDevuelto));
            return false;
        }
        
        System.out.println("\n⏳ Registrando devolución en el sistema...");
        boolean resultado = devolucionDAO.registrarDevolucion(devolucion);
        
        if (resultado) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("✅ DEVOLUCIÓN REGISTRADA EXITOSAMENTE");
            System.out.println("=".repeat(50));
            System.out.println("ID Devolución: #" + devolucion.getIdDevolucion());
            System.out.println("Venta original: " + folioVenta);
            System.out.println("Fecha venta: " + venta.getFechaVenta());
            System.out.println("Días desde venta: " + diasTranscurridos);
            System.out.println("-".repeat(50));
            System.out.println("Procesado por: " + LoginController.getNombreUsuarioActual());
            System.out.println("Rol: " + LoginController.getRolUsuarioActual());
            System.out.println("Caja: " + aperturaActual.getCaja().getNombreCaja());
            System.out.println("-".repeat(50));
            System.out.println("Productos devueltos: " + devolucion.getDetalles().size() + " tipo(s)");
            System.out.println("Unidades totales: " + totalProductosDevueltos);
            System.out.println("Monto a devolver: $" + String.format("%.2f", montoDevuelto));
            System.out.println("-".repeat(50));
            System.out.println("Motivo: " + motivo);
            System.out.println("=".repeat(50) + "\n");
            
            System.out.println("DETALLE DE PRODUCTOS DEVUELTOS:");
            for (DetalleDevolucion det : devolucion.getDetalles()) {
                System.out.println("  • " + det.getProducto().getNombreProducto() +
                                 " - " + det.getCantidad() + " unidad(es) x $" +
                                 String.format("%.2f", det.getPrecioUnitario()) +
                                 " = $" + String.format("%.2f", det.getSubtotal()));
            }
            System.out.println();
        } else {
            System.err.println("\n❌ ERROR: No se pudo registrar la devolución");
            System.err.println("   Verifique los logs del sistema para más detalles");
        }
        
        return resultado;
    }
    
    private long calcularDiasDesdeVenta(java.util.Date fechaVenta) {
        if (fechaVenta == null) {
            return 0;
        }
        
        long milisegundosTranscurridos = System.currentTimeMillis() - fechaVenta.getTime();
        return milisegundosTranscurridos / (1000 * 60 * 60 * 24);
    }
    
    private int obtenerCantidadYaDevuelta(List<Devolucion> devoluciones, int idProducto) {
        if (devoluciones == null || devoluciones.isEmpty()) {
            return 0;
        }
        
        int totalDevuelto = 0;
        for (Devolucion dev : devoluciones) {
            if (dev.getDetalles() != null) {
                for (DetalleDevolucion det : dev.getDetalles()) {
                    if (det.getIdProducto() == idProducto) {
                        totalDevuelto += det.getCantidad();
                    }
                }
            }
        }
        
        return totalDevuelto;
    }
    
    private DetalleVenta buscarDetalleVenta(Venta venta, int idProducto) {
        if (venta == null || venta.getDetalles() == null) {
            return null;
        }
        
        for (DetalleVenta detalle : venta.getDetalles()) {
            if (detalle.getIdProducto() == idProducto) {
                return detalle;
            }
        }
        return null;
    }
    
    public List<Devolucion> obtenerDevolucionesPorVenta(int idVenta) {
        if (!LoginController.haySesionActiva()) {
            System.err.println("❌ No hay sesión activa");
            return null;
        }
        
        if (idVenta <= 0) {
            System.err.println("❌ ID de venta inválido");
            return null;
        }
        
        List<Devolucion> devoluciones = devolucionDAO.obtenerPorVenta(idVenta);
        
        if (devoluciones != null && !devoluciones.isEmpty()) {
            System.out.println("\n✓ Se encontraron " + devoluciones.size() + " devolución(es)");
        } else {
            System.out.println("\nℹ No hay devoluciones para esta venta");
        }
        
        return devoluciones;
    }
   
    public boolean validarDevolucion(String folioVenta) {
   
        if (!LoginController.haySesionActiva()) {
            System.err.println("❌ No hay sesión activa");
            return false;
        }
        
        if (folioVenta == null || folioVenta.trim().isEmpty()) {
            System.err.println("❌ El folio es requerido");
            return false;
        }
        
        Venta venta = ventaDAO.buscarPorFolio(folioVenta.trim());
        if (venta == null) {
            System.err.println("❌ Venta no encontrada: " + folioVenta);
            return false;
        }
        
        if ("CANCELADA".equals(venta.getEstado())) {
            System.err.println("❌ La venta está cancelada. No se pueden procesar devoluciones");
            return false;
        }
        
        long diasTranscurridos = calcularDiasDesdeVenta(venta.getFechaVenta());
        if (diasTranscurridos > DIAS_LIMITE_DEVOLUCION && !LoginController.esAdministrador()) {
            System.err.println("❌ La venta tiene más de " + DIAS_LIMITE_DEVOLUCION + " días");
            System.err.println("   Requiere autorización de administrador");
            System.err.println("   Su rol: " + LoginController.getRolUsuarioActual());
            return false;
        }
        
        System.out.println("✓ La venta es elegible para devolución");
        return true;
    }
    
    public boolean puedeRealizarDevolucion() {
        if (!LoginController.haySesionActiva()) {
            System.err.println("❌ No hay sesión activa");
            System.out.println("   Debe iniciar sesión primero");
            return false;
        }
        
        int idUsuario = LoginController.getIdUsuarioActual();
        AperturaCaja apertura = aperturaCajaDAO.obtenerCajaAbierta(idUsuario);
        
        if (apertura == null) {
            System.err.println("❌ Debe abrir una caja para procesar devoluciones");
            System.out.println("   Vaya al menú de apertura de caja");
            return false;
        }
        
        if (!"ABIERTA".equals(apertura.getEstado())) {
            System.err.println("❌ La caja no está abierta");
            System.err.println("   Estado actual: " + apertura.getEstado());
            return false;
        }
        
        System.out.println("✓ Puede realizar devoluciones");
        System.out.println("  Usuario: " + LoginController.getNombreUsuarioActual());
        System.out.println("  Caja: " + apertura.getCaja().getNombreCaja());
        return true;
    }
    
    public Venta obtenerVentaParaDevolucion(String folioVenta) {
        if (folioVenta == null || folioVenta.trim().isEmpty()) {
            System.err.println("❌ El folio es requerido");
            return null;
        }
        
        if (!LoginController.haySesionActiva()) {
            System.err.println("❌ No hay sesión activa");
            return null;
        }
        
        Venta venta = ventaDAO.buscarPorFolio(folioVenta.trim());
        
        if (venta == null) {
            System.err.println("❌ No se encontró la venta: " + folioVenta);
            return null;
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("INFORMACIÓN DE VENTA");
        System.out.println("=".repeat(50));
        System.out.println("Folio: " + venta.getFolio());
        System.out.println("Fecha: " + venta.getFechaVenta());
        System.out.println("Estado: " + venta.getEstado());
        System.out.println("-".repeat(50));
        System.out.println("Subtotal: $" + String.format("%.2f", venta.getSubtotal()));
        System.out.println("Descuento: $" + String.format("%.2f", venta.getDescuento()));
        System.out.println("IVA: $" + String.format("%.2f", venta.getImpuestos()));
        System.out.println("TOTAL: $" + String.format("%.2f", venta.getTotal()));
        System.out.println("-".repeat(50));
        System.out.println("Método de pago: " + venta.getMetodoPago());
        
        if (venta.getCliente() != null) {
            System.out.println("Cliente: " + venta.getCliente().getNombreCliente());
        }
        
        System.out.println("-".repeat(50));
        System.out.println("Productos: " + venta.getDetalles().size());
        
        int contador = 1;
        for (DetalleVenta detalle : venta.getDetalles()) {
            System.out.println("  " + contador + ". " + detalle.getProducto().getNombreProducto() +
                             " - " + detalle.getCantidad() + " x $" +
                             String.format("%.2f", detalle.getPrecioUnitario()) +
                             " = $" + String.format("%.2f", detalle.getTotal()));
            contador++;
        }
        
        System.out.println("-".repeat(50));
        
        long dias = calcularDiasDesdeVenta(venta.getFechaVenta());
        System.out.println("Días desde la venta: " + dias);
        
        if (dias > DIAS_LIMITE_DEVOLUCION) {
            System.out.println("⚠ FUERA DEL PLAZO ESTÁNDAR (" + DIAS_LIMITE_DEVOLUCION + " días)");
            if (LoginController.esAdministrador()) {
                System.out.println("✓ Puede autorizar como administrador");
            } else {
                System.out.println("❌ Requiere autorización de administrador");
            }
        } else {
            System.out.println("✓ Dentro del plazo para devolución");
        }
        
        List<Devolucion> devolucionesPrevias = devolucionDAO.obtenerPorVenta(venta.getIdVenta());
        if (devolucionesPrevias != null && !devolucionesPrevias.isEmpty()) {
            System.out.println("-".repeat(50));
            System.out.println("⚠ DEVOLUCIONES PREVIAS: " + devolucionesPrevias.size());
            
            double totalDevuelto = 0;
            for (Devolucion dev : devolucionesPrevias) {
                System.out.println("  • Devolución #" + dev.getIdDevolucion());
                System.out.println("    Fecha: " + dev.getFechaDevolucion());
                System.out.println("    Monto: $" + String.format("%.2f", dev.getMontoDevuelto()));
                System.out.println("    Motivo: " + dev.getMotivo());
                totalDevuelto += dev.getMontoDevuelto();
            }
            
            System.out.println("  Total devuelto: $" + String.format("%.2f", totalDevuelto));
            System.out.println("  Saldo restante: $" + String.format("%.2f", venta.getTotal() - totalDevuelto));
        }
        
        System.out.println("=".repeat(50) + "\n");
        
        return venta;
    }
    
    public int getDiasLimiteDevolucion() {
        return DIAS_LIMITE_DEVOLUCION;
    }
    
    public boolean puedeAutorizarDevolucionFueraDePlazo() {
        if (!LoginController.haySesionActiva()) {
            return false;
        }
        return LoginController.esAdministrador();
    }
}