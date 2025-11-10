package pos_tienda_abarrotes.dao;

import java.util.ArrayList;
import java.util.List;
import pos_tienda_abarrotes.model.DatabaseConnection;
import pos_tienda_abarrotes.model.Caja;
import java.sql.*;

public class CajaDAO {

    public List<Caja> obtenerTodas() {
        List<Caja> cajas = new ArrayList<>();
        String sql = "SELECT id_caja, nombre_caja, ubicacion, activo, fecha_creacion " +
                     "FROM Cajas WHERE activo = 1 ORDER BY nombre_caja";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Caja caja = new Caja();
                caja.setIdCaja(rs.getInt("id_caja"));
                caja.setNombreCaja(rs.getString("nombre_caja"));
                caja.setUbicacion(rs.getString("ubicacion"));
                caja.setActivo(rs.getBoolean("activo"));
                caja.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                cajas.add(caja);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener cajas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return cajas;
    }

    public Caja obtenerPorId(int idCaja) {
        String sql = "SELECT id_caja, nombre_caja, ubicacion, activo, fecha_creacion " +
                     "FROM Cajas WHERE id_caja = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCaja);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Caja caja = new Caja();
                caja.setIdCaja(rs.getInt("id_caja"));
                caja.setNombreCaja(rs.getString("nombre_caja"));
                caja.setUbicacion(rs.getString("ubicacion"));
                caja.setActivo(rs.getBoolean("activo"));
                caja.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                return caja;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener caja: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}
