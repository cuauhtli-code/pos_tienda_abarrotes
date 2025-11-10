package pos_tienda_abarrotes.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import pos_tienda_abarrotes.model.DatabaseConnection;
import pos_tienda_abarrotes.model.Devolucion;
import pos_tienda_abarrotes.model.DetalleDevolucion;
import pos_tienda_abarrotes.model.Producto;
import pos_tienda_abarrotes.model.Usuario;

public class DevolucionDAO {
    
    private ProductoDAO productoDAO = new ProductoDAO();
    
    public boolean registrarDevolucion(Devolucion devolucion) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            String sqlDevolucion = "INSERT INTO Devoluciones (id_venta, id_usuario, motivo, monto_devuelto) " +
                                  "VALUES (?, ?, ?, ?)";
            
            PreparedStatement pstmtDevolucion = conn.prepareStatement(sqlDevolucion, 
                                                                      Statement.RETURN_GENERATED_KEYS);
            pstmtDevolucion.setInt(1, devolucion.getIdVenta());
            pstmtDevolucion.setInt(2, devolucion.getIdUsuario());
            pstmtDevolucion.setString(3, devolucion.getMotivo());
            pstmtDevolucion.setDouble(4, devolucion.getMontoDevuelto());
            
            pstmtDevolucion.executeUpdate();
            
            ResultSet rs = pstmtDevolucion.getGeneratedKeys();
            if (rs.next()) {
                devolucion.setIdDevolucion(rs.getInt(1));
            }
            
            String sqlDetalle = "INSERT INTO DetalleDevolucion (id_devolucion, id_producto, " +
                               "cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle);
            
            for (DetalleDevolucion detalle : devolucion.getDetalles()) {
                pstmtDetalle.setInt(1, devolucion.getIdDevolucion());
                pstmtDetalle.setInt(2, detalle.getIdProducto());
                pstmtDetalle.setInt(3, detalle.getCantidad());
                pstmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                pstmtDetalle.setDouble(5, detalle.getSubtotal());
                pstmtDetalle.executeUpdate();
                
                Producto producto = productoDAO.obtenerPorId(detalle.getIdProducto());
                if (producto != null) {
                    int nuevoStock = producto.getStockActual() + detalle.getCantidad();
                    productoDAO.actualizarStock(producto.getIdProducto(), nuevoStock);
                    
                    registrarMovimientoInventario(conn, producto.getIdProducto(), "DEVOLUCION",
                                                 detalle.getCantidad(), producto.getStockActual(),
                                                 nuevoStock, devolucion.getIdUsuario(),
                                                 "Devolución #" + devolucion.getIdDevolucion());
                }
            }
            
            conn.commit();
            System.out.println("✓ Devolución registrada exitosamente");
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error al registrar devolución: " + e.getMessage());
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
    
    private void registrarMovimientoInventario(Connection conn, int idProducto,
                                               String tipoMovimiento, int cantidad,
                                               int stockAnterior, int stockNuevo,
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
    
    public List<Devolucion> obtenerPorVenta(int idVenta) {
        List<Devolucion> devoluciones = new ArrayList<>();
        String sql = "SELECT d.id_devolucion, d.id_venta, d.id_usuario, d.motivo, " +
                    "d.monto_devuelto, d.fecha_devolucion, u.nombre_completo " +
                    "FROM Devoluciones d " +
                    "INNER JOIN Usuarios u ON d.id_usuario = u.id_usuario " +
                    "WHERE d.id_venta = ? " +
                    "ORDER BY d.fecha_devolucion DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idVenta);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Devolucion devolucion = new Devolucion();
                devolucion.setIdDevolucion(rs.getInt("id_devolucion"));
                devolucion.setIdVenta(rs.getInt("id_venta"));
                devolucion.setIdUsuario(rs.getInt("id_usuario"));
                devolucion.setMotivo(rs.getString("motivo"));
                devolucion.setMontoDevuelto(rs.getDouble("monto_devuelto"));
                devolucion.setFechaDevolucion(rs.getTimestamp("fecha_devolucion"));
                
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombreCompleto(rs.getString("nombre_completo"));
                devolucion.setUsuario(usuario);
                
                devolucion.setDetalles(obtenerDetallesPorDevolucion(devolucion.getIdDevolucion()));
                
                devoluciones.add(devolucion);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener devoluciones: " + e.getMessage());
            e.printStackTrace();
        }
        
        return devoluciones;
    }
    
    private List<DetalleDevolucion> obtenerDetallesPorDevolucion(int idDevolucion) {
        List<DetalleDevolucion> detalles = new ArrayList<>();
        String sql = "SELECT dd.id_detalle_devolucion, dd.id_devolucion, dd.id_producto, " +
                    "dd.cantidad, dd.precio_unitario, dd.subtotal, " +
                    "p.codigo_barras, p.nombre_producto " +
                    "FROM DetalleDevolucion dd " +
                    "INNER JOIN Productos p ON dd.id_producto = p.id_producto " +
                    "WHERE dd.id_devolucion = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idDevolucion);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                DetalleDevolucion detalle = new DetalleDevolucion();
                detalle.setIdDetalleDevolucion(rs.getInt("id_detalle_devolucion"));
                detalle.setIdDevolucion(rs.getInt("id_devolucion"));
                detalle.setIdProducto(rs.getInt("id_producto"));
                detalle.setCantidad(rs.getInt("cantidad"));
                detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
                detalle.setSubtotal(rs.getDouble("subtotal"));
                
                Producto producto = new Producto();
                producto.setIdProducto(rs.getInt("id_producto"));
                producto.setCodigoBarras(rs.getString("codigo_barras"));
                producto.setNombreProducto(rs.getString("nombre_producto"));
                detalle.setProducto(producto);
                
                detalles.add(detalle);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles de devolución: " + e.getMessage());
            e.printStackTrace();
        }
        
        return detalles;
    }
    
    public Devolucion obtenerPorId(int idDevolucion) {
        String sql = "SELECT d.id_devolucion, d.id_venta, d.id_usuario, d.motivo, " +
                    "d.monto_devuelto, d.fecha_devolucion, u.nombre_completo " +
                    "FROM Devoluciones d " +
                    "INNER JOIN Usuarios u ON d.id_usuario = u.id_usuario " +
                    "WHERE d.id_devolucion = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idDevolucion);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Devolucion devolucion = new Devolucion();
                devolucion.setIdDevolucion(rs.getInt("id_devolucion"));
                devolucion.setIdVenta(rs.getInt("id_venta"));
                devolucion.setIdUsuario(rs.getInt("id_usuario"));
                devolucion.setMotivo(rs.getString("motivo"));
                devolucion.setMontoDevuelto(rs.getDouble("monto_devuelto"));
                devolucion.setFechaDevolucion(rs.getTimestamp("fecha_devolucion"));
                
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombreCompleto(rs.getString("nombre_completo"));
                devolucion.setUsuario(usuario);
                
                devolucion.setDetalles(obtenerDetallesPorDevolucion(idDevolucion));
                
                return devolucion;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener devolución: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}