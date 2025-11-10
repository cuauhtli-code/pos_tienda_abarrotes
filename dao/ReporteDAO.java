package pos_tienda_abarrotes.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import pos_tienda_abarrotes.model.DatabaseConnection;
import java.sql.*;

public class ReporteDAO {

    public List<Object[]> obtenerProductosMasVendidos(Date fechaInicio, Date fechaFin, int limite) {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT TOP " + limite + " p.nombre_producto, SUM(dv.cantidad) as cantidad_vendida, " +
                    "SUM(dv.total) as total_ventas " +
                    "FROM DetalleVentas dv " +
                    "INNER JOIN Productos p ON dv.id_producto = p.id_producto " +
                    "INNER JOIN Ventas v ON dv.id_venta = v.id_venta " +
                    "WHERE v.fecha_venta BETWEEN ? AND ? AND v.estado = 'COMPLETADA' " +
                    "GROUP BY p.nombre_producto " +
                    "ORDER BY cantidad_vendida DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, new Timestamp(fechaInicio.getTime()));
            pstmt.setTimestamp(2, new Timestamp(fechaFin.getTime()));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] fila = new Object[3];
                fila[0] = rs.getString("nombre_producto");
                fila[1] = rs.getInt("cantidad_vendida");
                fila[2] = rs.getDouble("total_ventas");
                resultados.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos más vendidos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultados;
    }
    
    public List<Object[]> obtenerVentasPorMetodoPago(Date fechaInicio, Date fechaFin) {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT metodo_pago, COUNT(*) as cantidad_transacciones, " +
                    "SUM(total) as total_ventas " +
                    "FROM Ventas " +
                    "WHERE fecha_venta BETWEEN ? AND ? AND estado = 'COMPLETADA' " +
                    "GROUP BY metodo_pago " +
                    "ORDER BY total_ventas DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, new Timestamp(fechaInicio.getTime()));
            pstmt.setTimestamp(2, new Timestamp(fechaFin.getTime()));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] fila = new Object[3];
                fila[0] = rs.getString("metodo_pago");
                fila[1] = rs.getInt("cantidad_transacciones");
                fila[2] = rs.getDouble("total_ventas");
                resultados.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por método de pago: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultados;
    }
}