package pos_tienda_abarrotes.dao;

import java.util.ArrayList;
import java.util.List;
import pos_tienda_abarrotes.model.DatabaseConnection;
import pos_tienda_abarrotes.model.Rol;
import pos_tienda_abarrotes.model.Usuario;
import java.sql.*;

public class UsuarioDAO {
    
    public Usuario validarLogin(String nombreUsuario, String contrasena) {
        String sql = "SELECT u.id_usuario, u.nombre_usuario, u.contrasena, u.nombre_completo, " +
                     "u.id_rol, u.activo, u.fecha_creacion, u.ultimo_acceso, " +
                     "r.nombre_rol, r.descripcion " +
                     "FROM Usuarios u " +
                     "INNER JOIN Roles r ON u.id_rol = r.id_rol " +
                     "WHERE u.nombre_usuario = ? AND u.contrasena = ? AND u.activo = 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreUsuario);
            pstmt.setString(2, contrasena);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setNombreCompleto(rs.getString("nombre_completo"));
                usuario.setIdRol(rs.getInt("id_rol"));
                usuario.setActivo(rs.getBoolean("activo"));
                usuario.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                usuario.setUltimoAcceso(rs.getTimestamp("ultimo_acceso"));
                
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setNombreRol(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
                usuario.setRol(rol);
                
                actualizarUltimoAcceso(usuario.getIdUsuario());
                
                return usuario;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al validar login: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    private void actualizarUltimoAcceso(int idUsuario) {
        String sql = "UPDATE Usuarios SET ultimo_acceso = GETDATE() WHERE id_usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idUsuario);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar último acceso: " + e.getMessage());
        }
    }
    
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.nombre_usuario, u.nombre_completo, u.id_rol, " +
                     "u.activo, u.fecha_creacion, r.nombre_rol " +
                     "FROM Usuarios u " +
                     "INNER JOIN Roles r ON u.id_rol = r.id_rol " +
                     "ORDER BY u.nombre_completo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                usuario.setNombreCompleto(rs.getString("nombre_completo"));
                usuario.setIdRol(rs.getInt("id_rol"));
                usuario.setActivo(rs.getBoolean("activo"));
                usuario.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setNombreRol(rs.getString("nombre_rol"));
                usuario.setRol(rol);
                
                usuarios.add(usuario);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        
        return usuarios;
    }
    
    public boolean crear(Usuario usuario) {
        String sql = "INSERT INTO Usuarios (nombre_usuario, contrasena, nombre_completo, id_rol, activo) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setString(3, usuario.getNombreCompleto());
            pstmt.setInt(4, usuario.getIdRol());
            pstmt.setBoolean(5, usuario.isActivo());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                }
                System.out.println("✓ Usuario creado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE Usuarios SET nombre_usuario = ?, nombre_completo = ?, " +
                     "id_rol = ?, activo = ? WHERE id_usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getNombreCompleto());
            pstmt.setInt(3, usuario.getIdRol());
            pstmt.setBoolean(4, usuario.isActivo());
            pstmt.setInt(5, usuario.getIdUsuario());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Usuario actualizado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean actualizarContrasena(int idUsuario, String nuevaContrasena) {
        String sql = "UPDATE Usuarios SET contrasena = ? WHERE id_usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuevaContrasena);
            pstmt.setInt(2, idUsuario);
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Contraseña actualizada exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean desactivar(int idUsuario) {
        String sql = "UPDATE Usuarios SET activo = 0 WHERE id_usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idUsuario);
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Usuario desactivado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al desactivar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean existeNombreUsuario(String nombreUsuario) {
        String sql = "SELECT COUNT(*) as total FROM Usuarios WHERE nombre_usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreUsuario);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar nombre de usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public Usuario obtenerPorId(int idUsuario) {
        String sql = "SELECT u.id_usuario, u.nombre_usuario, u.contrasena, u.nombre_completo, " +
                     "u.id_rol, u.activo, u.fecha_creacion, u.ultimo_acceso, " +
                     "r.nombre_rol, r.descripcion " +
                     "FROM Usuarios u " +
                     "INNER JOIN Roles r ON u.id_rol = r.id_rol " +
                     "WHERE u.id_usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setNombreCompleto(rs.getString("nombre_completo"));
                usuario.setIdRol(rs.getInt("id_rol"));
                usuario.setActivo(rs.getBoolean("activo"));
                usuario.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                usuario.setUltimoAcceso(rs.getTimestamp("ultimo_acceso"));
                
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setNombreRol(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
                usuario.setRol(rol);
                
                return usuario;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}
