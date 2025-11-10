package pos_tienda_abarrotes.controller;

import pos_tienda_abarrotes.dao.VentaDAO;
import pos_tienda_abarrotes.dao.ProductoDAO;
import pos_tienda_abarrotes.dao.ClienteDAO;
import pos_tienda_abarrotes.dao.AperturaCajaDAO;
import pos_tienda_abarrotes.model.Venta;
import pos_tienda_abarrotes.model.DetalleVenta;
import pos_tienda_abarrotes.model.Producto;
import pos_tienda_abarrotes.model.Cliente;
import pos_tienda_abarrotes.model.AperturaCaja;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

public class VentasController {
    private VentaDAO ventaDAO;
    private ProductoDAO productoDAO;
    private ClienteDAO clienteDAO;
    private AperturaCajaDAO aperturaCajaDAO;
    private Venta ventaActual;
    private static final double IVA = 0.16; // 16% de IVA
    
    public VentasController() {
        this.ventaDAO = new VentaDAO();
        this.productoDAO = new ProductoDAO();
        this.clienteDAO = new ClienteDAO();
        this.aperturaCajaDAO = new AperturaCajaDAO();
        this.ventaActual = null;
    }
    
    public boolean iniciarNuevaVenta(int idUsuario, int idApertura) {
        if (!LoginController.haySesionActiva()) {
            System.err.println("No hay sesión activa. Debe iniciar sesión primero");
            return false;
        }
        
        if (LoginController.getIdUsuarioActual() != idUsuario) {
            System.err.println("El usuario no coincide con la sesión actual");
            return false;
        }
        
        if (idUsuario <= 0) {
            System.err.println("ID de usuario inválido");
            return false;
        }
        
        if (idApertura <= 0) {
            System.err.println("ID de apertura inválido");
            return false;
        }
        
        AperturaCaja apertura = aperturaCajaDAO.obtenerPorId(idApertura);
        if (apertura == null) {
            System.err.println("Apertura de caja no encontrada");
            return false;
        }
        
        if (!"ABIERTA".equals(apertura.getEstado())) {
            System.err.println("La caja no está abierta. Estado: " + apertura.getEstado());
            return false;
        }
        
        if (apertura.getIdUsuario() != idUsuario) {
            System.err.println("La apertura de caja no pertenece al usuario actual");
            return false;
        }
        
        ventaActual = new Venta();
        ventaActual.setIdUsuario(idUsuario);
        ventaActual.setIdApertura(idApertura);
        ventaActual.setSubtotal(0);
        ventaActual.setImpuestos(0);
        ventaActual.setDescuento(0);
        ventaActual.setTotal(0);
        ventaActual.setEstado("COMPLETADA");
        
        System.out.println("✓ Nueva venta iniciada");
        return true;
    }
    
    public boolean agregarProducto(String codigoBarras, int cantidad) {
        if (ventaActual == null) {
            System.err.println("No hay una venta activa. Debe iniciar una venta primero");
            return false;
        }
        
        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            System.err.println("El código de barras es requerido");
            return false;
        }
        
        if (cantidad <= 0) {
            System.err.println("La cantidad debe ser mayor a cero");
            return false;
        }
        
        Producto producto = productoDAO.buscarPorCodigoBarras(codigoBarras.trim());
        if (producto == null) {
            System.err.println("Producto no encontrado: " + codigoBarras);
            return false;
        }
        
        if (!producto.isActivo()) {
            System.err.println("El producto está inactivo: " + producto.getNombreProducto());
            return false;
        }
        
        if (producto.getStockActual() < cantidad) {
            System.err.println("Stock insuficiente para " + producto.getNombreProducto());
            System.err.println("  Disponible: " + producto.getStockActual());
            System.err.println("  Solicitado: " + cantidad);
            return false;
        }
        
        DetalleVenta detalleExistente = buscarDetalleEnVenta(producto.getIdProducto());
        
        if (detalleExistente != null) {
            int nuevaCantidad = detalleExistente.getCantidad() + cantidad;
            if (producto.getStockActual() < nuevaCantidad) {
                System.err.println("Stock insuficiente para agregar más unidades");
                return false;
            }
            detalleExistente.setCantidad(nuevaCantidad);
            detalleExistente.calcularTotal();
            System.out.println("✓ Cantidad actualizada: " + producto.getNombreProducto() +
                             " (Total: " + nuevaCantidad + ")");
        } else {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdProducto(producto.getIdProducto());
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecioVenta());
            detalle.setDescuento(0);
            detalle.calcularTotal();
            
            ventaActual.agregarDetalle(detalle);
            System.out.println("✓ Producto agregado: " + producto.getNombreProducto() +
                             " x" + cantidad + " = $" + String.format("%.2f", detalle.getTotal()));
        }
        
        calcularTotalesVenta();
        return true;
    }
    
    private DetalleVenta buscarDetalleEnVenta(int idProducto) {
        if (ventaActual == null || ventaActual.getDetalles() == null) {
            return null;
        }
        
        for (DetalleVenta detalle : ventaActual.getDetalles()) {
            if (detalle.getIdProducto() == idProducto) {
                return detalle;
            }
        }
        return null;
    }
    
    public boolean eliminarProducto(int idProducto) {
        if (ventaActual == null) {
            System.err.println("No hay una venta activa");
            return false;
        }
        
        DetalleVenta detalle = buscarDetalleEnVenta(idProducto);
        if (detalle == null) {
            System.err.println("El producto no está en la venta");
            return false;
        }
        
        ventaActual.getDetalles().remove(detalle);
        System.out.println("✓ Producto eliminado: " + detalle.getProducto().getNombreProducto());
        
        calcularTotalesVenta();
        return true;
    }

    public boolean modificarCantidadProducto(int idProducto, int nuevaCantidad) {
        if (ventaActual == null) {
            System.err.println("No hay una venta activa");
            return false;
        }
        
        if (nuevaCantidad <= 0) {
            System.err.println("La cantidad debe ser mayor a cero");
            return false;
        }
        
        DetalleVenta detalle = buscarDetalleEnVenta(idProducto);
        if (detalle == null) {
            System.err.println("El producto no está en la venta");
            return false;
        }
        
        if (detalle.getProducto().getStockActual() < nuevaCantidad) {
            System.err.println("Stock insuficiente");
            return false;
        }
        
        detalle.setCantidad(nuevaCantidad);
        detalle.calcularTotal();
        System.out.println("✓ Cantidad actualizada: " + detalle.getProducto().getNombreProducto() +
                         " = " + nuevaCantidad);
        
        calcularTotalesVenta();
        return true;
    }
    
    public boolean aplicarDescuentoVenta(double descuento) {
        if (ventaActual == null) {
            System.err.println("No hay una venta activa");
            return false;
        }
        
        if (descuento < 0) {
            System.err.println("El descuento no puede ser negativo");
            return false;
        }
        
        if (descuento > ventaActual.getSubtotal()) {
            System.err.println("El descuento no puede ser mayor al subtotal");
            return false;
        }
        
        double porcentajeDescuento = (descuento / ventaActual.getSubtotal()) * 100;
        if (porcentajeDescuento > 10 && !LoginController.tienePermisosSupervisor()) {
            System.err.println("Descuentos mayores al 10% requieren autorización de supervisor");
            System.err.println("Descuento solicitado: " + String.format("%.1f", porcentajeDescuento) + "%");
            return false;
        }
        
        ventaActual.setDescuento(descuento);
        System.out.println("✓ Descuento aplicado: $" + String.format("%.2f", descuento) +
                         " (" + String.format("%.1f", porcentajeDescuento) + "%)");
        
        calcularTotalesVenta();
        return true;
    }
    
    public boolean aplicarDescuentoProducto(int idProducto, double descuento) {
        if (ventaActual == null) {
            System.err.println("No hay una venta activa");
            return false;
        }
        
        if (descuento < 0) {
            System.err.println("El descuento no puede ser negativo");
            return false;
        }
        
        DetalleVenta detalle = buscarDetalleEnVenta(idProducto);
        if (detalle == null) {
            System.err.println("El producto no está en la venta");
            return false;
        }
        
        if (descuento > detalle.getSubtotal()) {
            System.err.println("El descuento no puede ser mayor al subtotal del producto");
            return false;
        }
        
        detalle.setDescuento(descuento);
        detalle.calcularTotal();
        System.out.println("✓ Descuento aplicado al producto: $" + String.format("%.2f", descuento));
        
        calcularTotalesVenta();
        return true;
    }
    
    private void calcularTotalesVenta() {
        if (ventaActual == null) {
            return;
        }
        
        double subtotal = 0;
        
        for (DetalleVenta detalle : ventaActual.getDetalles()) {
            subtotal += detalle.getSubtotal();
        }
        
        ventaActual.setSubtotal(subtotal);
        
        double baseImponible = subtotal - ventaActual.getDescuento();
        double impuestos = baseImponible * IVA;
        ventaActual.setImpuestos(impuestos);
        
        double total = baseImponible + impuestos;
        ventaActual.setTotal(total);
    }
    
    public boolean asociarCliente(int idCliente) {
        if (ventaActual == null) {
            System.err.println("No hay una venta activa");
            return false;
        }
        
        if (idCliente <= 0) {
            ventaActual.setIdCliente(0);
            ventaActual.setCliente(null);
            System.out.println("✓ Cliente desasociado de la venta");
            return true;
        }

        Cliente cliente = clienteDAO.obtenerPorId(idCliente);
        if (cliente == null) {
            System.err.println("Cliente no encontrado");
            return false;
        }
        
        ventaActual.setIdCliente(idCliente);
        ventaActual.setCliente(cliente);
        System.out.println("✓ Cliente asociado: " + cliente.getNombreCliente());
        return true;
    }
    
    public String finalizarVenta(String metodoPago, String observaciones) {
        if (!LoginController.haySesionActiva()) {
            System.err.println("No hay sesión activa");
            return null;
        }
        
        if (ventaActual == null) {
            System.err.println("No hay una venta activa");
            return null;
        }
        
        AperturaCaja apertura = aperturaCajaDAO.obtenerPorId(ventaActual.getIdApertura());
        if (apertura == null || !"ABIERTA".equals(apertura.getEstado())) {
            System.err.println("La caja ya no está abierta. No se puede completar la venta");
            return null;
        }
        
        if (ventaActual.getDetalles() == null || ventaActual.getDetalles().isEmpty()) {
            System.err.println("La venta no tiene productos");
            return null;
        }
        
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            System.err.println("El método de pago es requerido");
            return null;
        }
        
        String[] metodosValidos = {"EFECTIVO", "TARJETA", "TRANSFERENCIA", "VALES", "MIXTO"};
        boolean metodoValido = false;
        for (String metodo : metodosValidos) {
            if (metodo.equals(metodoPago.toUpperCase())) {
                metodoValido = true;
                break;
            }
        }
        
        if (!metodoValido) {
            System.err.println("Método de pago inválido. Métodos válidos: EFECTIVO, TARJETA, TRANSFERENCIA, VALES, MIXTO");
            return null;
        }
        
        ventaActual.setMetodoPago(metodoPago.toUpperCase());
        ventaActual.setObservaciones(observaciones);
        
        boolean resultado = ventaDAO.registrarVenta(ventaActual);
        
        if (resultado) {
            String folio = ventaActual.getFolio();
            System.out.println("\n✓ Venta registrada exitosamente");
            System.out.println("=== RESUMEN DE VENTA ===");
            System.out.println("Folio: " + folio);
            System.out.println("Fecha: " + ventaActual.getFechaVenta());
            System.out.println("Cajero: " + LoginController.getNombreUsuarioActual());
            System.out.println("Productos: " + ventaActual.getDetalles().size());
            System.out.println("Subtotal: $" + String.format("%.2f", ventaActual.getSubtotal()));
            System.out.println("Descuento: $" + String.format("%.2f", ventaActual.getDescuento()));
            System.out.println("IVA (16%): $" + String.format("%.2f", ventaActual.getImpuestos()));
            System.out.println("TOTAL: $" + String.format("%.2f", ventaActual.getTotal()));
            System.out.println("Método de pago: " + ventaActual.getMetodoPago());
            if (ventaActual.getCliente() != null) {
                System.out.println("Cliente: " + ventaActual.getCliente().getNombreCliente());
            }
            System.out.println("========================\n");
            
            ventaActual = null;
            return folio;
        } else {
            System.err.println("Error al registrar la venta");
            return null;
        }
    }
    
    public void cancelarVentaActual() {
        if (ventaActual != null) {
            System.out.println("✓ Venta cancelada");
            ventaActual = null;
        }
    }
    
    public Venta getVentaActual() {
        return ventaActual;
    }
    
    public Venta buscarVentaPorFolio(String folio) {
        if (folio == null || folio.trim().isEmpty()) {
            System.err.println("El folio es requerido");
            return null;
        }
        
        Venta venta = ventaDAO.buscarPorFolio(folio.trim());
        if (venta == null) {
            System.out.println("No se encontró venta con folio: " + folio);
        }
        return venta;
    }
    
    public List<Venta> obtenerVentasPorFecha(Date fecha) {
        if (fecha == null) {
            System.err.println("La fecha es requerida");
            return new ArrayList<>();
        }
        return ventaDAO.obtenerPorFecha(fecha);
    }
    
    public List<Venta> obtenerVentasPorApertura(int idApertura) {
        if (idApertura <= 0) {
            System.err.println("ID de apertura inválido");
            return new ArrayList<>();
        }
        return ventaDAO.obtenerPorApertura(idApertura);
    }
    
    public boolean cancelarVentaRegistrada(String folio, int idUsuario, String motivo) {
        if (!LoginController.tienePermisosSupervisor()) {
            System.err.println("No tiene permisos para cancelar ventas");
            System.err.println("Esta operación requiere permisos de supervisor o administrador");
            return false;
        }
        
        if (!LoginController.haySesionActiva()) {
            System.err.println("No hay sesión activa");
            return false;
        }
        
        if (folio == null || folio.trim().isEmpty()) {
            System.err.println("El folio es requerido");
            return false;
        }
        
        if (motivo == null || motivo.trim().isEmpty()) {
            System.err.println("El motivo de cancelación es requerido");
            return false;
        }
        
        Venta venta = ventaDAO.buscarPorFolio(folio.trim());
        if (venta == null) {
            System.err.println("Venta no encontrada");
            return false;
        }
        
        if ("CANCELADA".equals(venta.getEstado())) {
            System.err.println("La venta ya está cancelada");
            return false;
        }
        
        boolean resultado = ventaDAO.cancelarVenta(folio.trim(), idUsuario, motivo.trim());
        
        if (resultado) {
            System.out.println("✓ Venta cancelada: " + folio);
            System.out.println("  Motivo: " + motivo);
            System.out.println("  Autorizado por: " + LoginController.getNombreUsuarioActual() +
                             " (" + LoginController.getRolUsuarioActual() + ")");
        }
        
        return resultado;
    }
    
    public double calcularTotalVentas(Date fechaInicio, Date fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            System.err.println("Las fechas son requeridas");
            return 0;
        }
        return ventaDAO.obtenerTotalVentas(fechaInicio, fechaFin);
    }
    
    public int getCantidadProductosVentaActual() {
        if (ventaActual == null || ventaActual.getDetalles() == null) {
            return 0;
        }
        return ventaActual.getDetalles().size();
    }
}