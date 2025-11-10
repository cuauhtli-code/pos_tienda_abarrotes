package pos_tienda_abarrotes.dao;

import java.util.ArrayList;
import java.util.List;

import pos_tienda_abarrotes.model.DatabaseConnection;
import pos_tienda_abarrotes.model.Rol;

import java.sql.*;

public class RolDAO {
   
    public List<Rol> obtenerTodos() {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT id_rol, nombre_rol, descripcion, activo, fecha_creacion " +
                     "FROM Roles WHERE activo = 1 ORDER BY nombre_rol";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setNombreRol(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
                rol.setActivo(rs.getBoolean("activo"));
                rol.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                roles.add(rol);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener roles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return roles;
    }
    
    public Rol obtenerPorId(int idRol) {
        String sql = "SELECT id_rol, nombre_rol, descripcion, activo, fecha_creacion " +
                     "FROM Roles WHERE id_rol = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRol);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setNombreRol(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
                rol.setActivo(rs.getBoolean("activo"));
                rol.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                return rol;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener rol: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}
