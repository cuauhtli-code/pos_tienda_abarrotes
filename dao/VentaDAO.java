package pos_tienda_abarrotes.dao;

import pos_tienda_abarrotes.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

public class VentaDAO {
    
    private ProductoDAO productoDAO = new ProductoDAO();
    
    private String generarFolio() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "V" + sdf.format(new java.util.Date());
    }
    
    public boolean registrarVenta(Venta venta) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            if (venta.getFolio() == null || venta.getFolio().isEmpty()) {
                venta.setFolio(generarFolio());
            }
            
            String sqlVenta = "INSERT INTO Ventas (folio, id_usuario, id_cliente, id_apertura, " +
                              "subtotal, impuestos, descuento, total, metodo_pago, estado, observaciones) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'COMPLETADA', ?)";
            
            PreparedStatement pstmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            pstmtVenta.setString(1, venta.getFolio());
            pstmtVenta.setInt(2, venta.getIdUsuario());
            
            if (venta.getIdCliente() > 0) {
                pstmtVenta.setInt(3, venta.getIdCliente());
            } else {
                pstmtVenta.setNull(3, Types.INTEGER);
            }
            
            pstmtVenta.setInt(4, venta.getIdApertura());
            pstmtVenta.setDouble(5, venta.getSubtotal());
            pstmtVenta.setDouble(6, venta.getImpuestos());
            pstmtVenta.setDouble(7, venta.getDescuento());
            pstmtVenta.setDouble(8, venta.getTotal());
            pstmtVenta.setString(9, venta.getMetodoPago());
            pstmtVenta.setString(10, venta.getObservaciones());
            
            pstmtVenta.executeUpdate();
            
            ResultSet rs = pstmtVenta.getGeneratedKeys();
            if (rs.next()) {
                venta.setIdVenta(rs.getInt(1));
            }
            
            String sqlDetalle = "INSERT INTO DetalleVentas (id_venta, id_producto, cantidad, " +
                                "precio_unitario, subtotal, descuento, total) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle);
            
            for (DetalleVenta detalle : venta.getDetalles()) {
                pstmtDetalle.setInt(1, venta.getIdVenta());
                pstmtDetalle.setInt(2, detalle.getIdProducto());
                pstmtDetalle.setInt(3, detalle.getCantidad());
                pstmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                pstmtDetalle.setDouble(5, detalle.getSubtotal());
                pstmtDetalle.setDouble(6, detalle.getDescuento());
                pstmtDetalle.setDouble(7, detalle.getTotal());
                pstmtDetalle.executeUpdate();
                
                Producto producto = productoDAO.buscarPorCodigoBarras(detalle.getProducto().getCodigoBarras());
                if (producto != null) {
                    int nuevoStock = producto.getStockActual() - detalle.getCantidad();
                    productoDAO.actualizarStock(producto.getIdProducto(), nuevoStock);
                    
                    registrarMovimientoInventario(conn, producto.getIdProducto(), "SALIDA", 
                                                  detalle.getCantidad(), producto.getStockActual(), 
                                                  nuevoStock, venta.getIdUsuario(), 
                                                  "Venta " + venta.getFolio());
                }
            }
            
            conn.commit();
            System.out.println("✓ Venta registrada exitosamente - Folio: " + venta.getFolio());
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error al registrar venta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }
    
    private void registrarMovimientoInventario(Connection conn, int idProducto, String tipoMovimiento,
                                                int cantidad, int stockAnterior, int stockNuevo,
                                                int idUsuario, String referencia) throws SQLException {
        String sql = "INSERT INTO HistorialInventario (id_producto, tipo_movimiento, cantidad, " +
                     "stock_anterior, stock_nuevo, id_usuario, referencia) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idProducto);
        pstmt.setString(2, tipoMovimiento);
        pstmt.setInt(3, cantidad);
        pstmt.setInt(4, stockAnterior);
        pstmt.setInt(5, stockNuevo);
        pstmt.setInt(6, idUsuario);
        pstmt.setString(7, referencia);
        pstmt.executeUpdate();
    }
    
    public Venta buscarPorFolio(String folio) {
        String sql = "SELECT v.id_venta, v.folio, v.id_usuario, v.id_cliente, v.id_apertura, " +
                     "v.subtotal, v.impuestos, v.descuento, v.total, v.metodo_pago, " +
                     "v.estado, v.fecha_venta, v.observaciones, " +
                     "u.nombre_completo as usuario, c.nombre_cliente " +
                     "FROM Ventas v " +
                     "INNER JOIN Usuarios u ON v.id_usuario = u.id_usuario " +
                     "LEFT JOIN Clientes c ON v.id_cliente = c.id_cliente " +
                     "WHERE v.folio = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, folio);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Venta venta = mapearVenta(rs);
                
                venta.setDetalles(obtenerDetallesPorVenta(venta.getIdVenta()));
                
                return venta;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar venta por folio: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    private List<DetalleVenta> obtenerDetallesPorVenta(int idVenta) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT dv.id_detalle, dv.id_venta, dv.id_producto, dv.cantidad, " +
                     "dv.precio_unitario, dv.subtotal, dv.descuento, dv.total, " +
                     "p.codigo_barras, p.nombre_producto " +
                     "FROM DetalleVentas dv " +
                     "INNER JOIN Productos p ON dv.id_producto = p.id_producto " +
                     "WHERE dv.id_venta = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idVenta);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setIdDetalle(rs.getInt("id_detalle"));
                detalle.setIdVenta(rs.getInt("id_venta"));
                detalle.setIdProducto(rs.getInt("id_producto"));
                detalle.setCantidad(rs.getInt("cantidad"));
                detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
                detalle.setSubtotal(rs.getDouble("subtotal"));
                detalle.setDescuento(rs.getDouble("descuento"));
                detalle.setTotal(rs.getDouble("total"));
                
                Producto producto = new Producto();
                producto.setIdProducto(rs.getInt("id_producto"));
                producto.setCodigoBarras(rs.getString("codigo_barras"));
                producto.setNombreProducto(rs.getString("nombre_producto"));
                detalle.setProducto(producto);
                
                detalles.add(detalle);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles de venta: " + e.getMessage());
            e.printStackTrace();
        }
        
        return detalles;
    }
    
    public List<Venta> obtenerPorFecha(java.util.Date fecha) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT v.id_venta, v.folio, v.id_usuario, v.id_cliente, v.id_apertura, " +
                     "v.subtotal, v.impuestos, v.descuento, v.total, v.metodo_pago, " +
                     "v.estado, v.fecha_venta, v.observaciones, " +
                     "u.nombre_completo as usuario, c.nombre_cliente " +
                     "FROM Ventas v " +
                     "INNER JOIN Usuarios u ON v.id_usuario = u.id_usuario " +
                     "LEFT JOIN Clientes c ON v.id_cliente = c.id_cliente " +
                     "WHERE CAST(v.fecha_venta AS DATE) = ? " +
                     "ORDER BY v.fecha_venta DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, (Date) fecha);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por fecha: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ventas;
    }
    
    public List<Venta> obtenerPorApertura(int idApertura) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT v.id_venta, v.folio, v.id_usuario, v.id_cliente, v.id_apertura, " +
                     "v.subtotal, v.impuestos, v.descuento, v.total, v.metodo_pago, " +
                     "v.estado, v.fecha_venta, v.observaciones, " +
                     "u.nombre_completo as usuario, c.nombre_cliente " +
                     "FROM Ventas v " +
                     "INNER JOIN Usuarios u ON v.id_usuario = u.id_usuario " +
                     "LEFT JOIN Clientes c ON v.id_cliente = c.id_cliente " +
                     "WHERE v.id_apertura = ? AND v.estado = 'COMPLETADA' " +
                     "ORDER BY v.fecha_venta DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idApertura);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por apertura: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ventas;
    }
    
    public boolean cancelarVenta(String folio, int idUsuario, String motivo) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            Venta venta = buscarPorFolio(folio);
            if (venta == null) {
                System.err.println("Venta no encontrada");
                return false;
            }
            
            if ("CANCELADA".equals(venta.getEstado())) {
                System.err.println("La venta ya está cancelada");
                return false;
            }
            
            String sqlVenta = "UPDATE Ventas SET estado = 'CANCELADA', observaciones = ? " +
                              "WHERE folio = ?";
            PreparedStatement pstmtVenta = conn.prepareStatement(sqlVenta);
            pstmtVenta.setString(1, "CANCELADA: " + motivo);
            pstmtVenta.setString(2, folio);
            pstmtVenta.executeUpdate();
            
            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto producto = productoDAO.buscarPorCodigoBarras(detalle.getProducto().getCodigoBarras());
                if (producto != null) {
                    int nuevoStock = producto.getStockActual() + detalle.getCantidad();
                    productoDAO.actualizarStock(producto.getIdProducto(), nuevoStock);
                    
                    registrarMovimientoInventario(conn, producto.getIdProducto(), "ENTRADA", 
                                                  detalle.getCantidad(), producto.getStockActual(), 
                                                  nuevoStock, idUsuario, 
                                                  "Cancelación venta " + folio);
                }
            }
            
            conn.commit();
            System.out.println("✓ Venta cancelada exitosamente");
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error al cancelar venta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }

    public double obtenerTotalVentas(java.util.Date fechaInicio, java.util.Date fechaFin) {
        String sql = "SELECT ISNULL(SUM(total), 0) as total " +
                     "FROM Ventas " +
                     "WHERE fecha_venta BETWEEN ? AND ? AND estado = 'COMPLETADA'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, (Date) fechaInicio);
            pstmt.setDate(2, (Date) fechaFin);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener total de ventas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setIdVenta(rs.getInt("id_venta"));
        venta.setFolio(rs.getString("folio"));
        venta.setIdUsuario(rs.getInt("id_usuario"));
        venta.setIdCliente(rs.getInt("id_cliente"));
        venta.setIdApertura(rs.getInt("id_apertura"));
        venta.setSubtotal(rs.getDouble("subtotal"));
        venta.setImpuestos(rs.getDouble("impuestos"));
        venta.setDescuento(rs.getDouble("descuento"));
        venta.setTotal(rs.getDouble("total"));
        venta.setMetodoPago(rs.getString("metodo_pago"));
        venta.setEstado(rs.getString("estado"));
        venta.setFechaVenta(rs.getTimestamp("fecha_venta"));
        venta.setObservaciones(rs.getString("observaciones"));
        
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNombreCompleto(rs.getString("usuario"));
        venta.setUsuario(usuario);
        
        if (rs.getInt("id_cliente") > 0) {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(rs.getInt("id_cliente"));
            cliente.setNombreCliente(rs.getString("nombre_cliente"));
            venta.setCliente(cliente);
        }
        
        return venta;
    }
}
