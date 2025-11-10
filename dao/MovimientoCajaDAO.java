package pos_tienda_abarrotes.dao;

import java.util.ArrayList;
import java.util.List;
import pos_tienda_abarrotes.model.DatabaseConnection;
import java.sql.*;
import pos_tienda_abarrotes.model.MovimientoCaja;
import pos_tienda_abarrotes.model.Usuario;

public class MovimientoCajaDAO {

    public boolean registrar(MovimientoCaja movimiento) {
        String sql = "INSERT INTO MovimientosCaja (id_apertura, tipo_movimiento, concepto, " +
                     "monto, id_usuario) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, movimiento.getIdApertura());
            pstmt.setString(2, movimiento.getTipoMovimiento());
            pstmt.setString(3, movimiento.getConcepto());
            pstmt.setDouble(4, movimiento.getMonto());
            pstmt.setInt(5, movimiento.getIdUsuario());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    movimiento.setIdMovimiento(rs.getInt(1));
                }
                System.out.println("✓ Movimiento registrado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al registrar movimiento: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public List<MovimientoCaja> obtenerPorApertura(int idApertura) {
        List<MovimientoCaja> movimientos = new ArrayList<>();
        String sql = "SELECT m.id_movimiento, m.id_apertura, m.tipo_movimiento, m.concepto, " +
                     "m.monto, m.id_usuario, m.fecha_movimiento, u.nombre_completo " +
                     "FROM MovimientosCaja m " +
                     "INNER JOIN Usuarios u ON m.id_usuario = u.id_usuario " +
                     "WHERE m.id_apertura = ? " +
                     "ORDER BY m.fecha_movimiento DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idApertura);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                MovimientoCaja movimiento = new MovimientoCaja();
                movimiento.setIdMovimiento(rs.getInt("id_movimiento"));
                movimiento.setIdApertura(rs.getInt("id_apertura"));
                movimiento.setTipoMovimiento(rs.getString("tipo_movimiento"));
                movimiento.setConcepto(rs.getString("concepto"));
                movimiento.setMonto(rs.getDouble("monto"));
                movimiento.setIdUsuario(rs.getInt("id_usuario"));
                movimiento.setFechaMovimiento(rs.getTimestamp("fecha_movimiento"));
                
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombreCompleto(rs.getString("nombre_completo"));
                movimiento.setUsuario(usuario);
                
                movimientos.add(movimiento);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener movimientos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return movimientos;
    }
}