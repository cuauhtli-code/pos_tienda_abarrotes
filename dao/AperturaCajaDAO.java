package pos_tienda_abarrotes.dao;

import pos_tienda_abarrotes.model.DatabaseConnection;
import pos_tienda_abarrotes.model.AperturaCaja;
import pos_tienda_abarrotes.model.Caja;
import java.sql.*;

public class AperturaCajaDAO {

    public boolean abrirCaja(AperturaCaja apertura) {
        String sql = "INSERT INTO AperturaCaja (id_caja, id_usuario, monto_inicial, estado) " +
                     "VALUES (?, ?, ?, 'ABIERTA')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, apertura.getIdCaja());
            pstmt.setInt(2, apertura.getIdUsuario());
            pstmt.setDouble(3, apertura.getMontoInicial());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    apertura.setIdApertura(rs.getInt(1));
                }
                System.out.println("✓ Caja abierta exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al abrir caja: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public AperturaCaja obtenerCajaAbierta(int idUsuario) {
        String sql = "SELECT a.id_apertura, a.id_caja, a.id_usuario, a.monto_inicial, " +
                     "a.fecha_apertura, a.estado, c.nombre_caja, c.ubicacion " +
                     "FROM AperturaCaja a " +
                     "INNER JOIN Cajas c ON a.id_caja = c.id_caja " +
                     "WHERE a.id_usuario = ? AND a.estado = 'ABIERTA' " +
                     "ORDER BY a.fecha_apertura DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                AperturaCaja apertura = new AperturaCaja();
                apertura.setIdApertura(rs.getInt("id_apertura"));
                apertura.setIdCaja(rs.getInt("id_caja"));
                apertura.setIdUsuario(rs.getInt("id_usuario"));
                apertura.setMontoInicial(rs.getDouble("monto_inicial"));
                apertura.setFechaApertura(rs.getTimestamp("fecha_apertura"));
                apertura.setEstado(rs.getString("estado"));
                
                Caja caja = new Caja();
                caja.setIdCaja(rs.getInt("id_caja"));
                caja.setNombreCaja(rs.getString("nombre_caja"));
                caja.setUbicacion(rs.getString("ubicacion"));
                apertura.setCaja(caja);
                
                return apertura;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener caja abierta: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean cajaEstaAbierta(int idCaja) {
        String sql = "SELECT COUNT(*) as total FROM AperturaCaja " +
                     "WHERE id_caja = ? AND estado = 'ABIERTA'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCaja);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar estado de caja: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public AperturaCaja obtenerPorId(int idApertura) {
        String sql = "SELECT a.id_apertura, a.id_caja, a.id_usuario, a.monto_inicial, " +
                     "a.fecha_apertura, a.estado, c.nombre_caja " +
                     "FROM AperturaCaja a " +
                     "INNER JOIN Cajas c ON a.id_caja = c.id_caja " +
                     "WHERE a.id_apertura = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idApertura);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                AperturaCaja apertura = new AperturaCaja();
                apertura.setIdApertura(rs.getInt("id_apertura"));
                apertura.setIdCaja(rs.getInt("id_caja"));
                apertura.setIdUsuario(rs.getInt("id_usuario"));
                apertura.setMontoInicial(rs.getDouble("monto_inicial"));
                apertura.setFechaApertura(rs.getTimestamp("fecha_apertura"));
                apertura.setEstado(rs.getString("estado"));
                
                Caja caja = new Caja();
                caja.setIdCaja(rs.getInt("id_caja"));
                caja.setNombreCaja(rs.getString("nombre_caja"));
                apertura.setCaja(caja);
                
                return apertura;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener apertura: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}
