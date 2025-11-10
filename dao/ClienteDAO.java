package pos_tienda_abarrotes.dao;

import java.util.ArrayList;
import java.util.List;
import pos_tienda_abarrotes.model.DatabaseConnection;
import java.sql.*;
import pos_tienda_abarrotes.model.Cliente;

public class ClienteDAO {
    
    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre_cliente, telefono, email, direccion, rfc, " +
                     "puntos_fidelidad, activo, fecha_registro " +
                     "FROM Clientes WHERE activo = 1 ORDER BY nombre_cliente";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clientes;
    }
    
    public List<Cliente> buscarPorNombre(String nombre) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre_cliente, telefono, email, direccion, rfc, " +
                     "puntos_fidelidad, activo, fecha_registro " +
                     "FROM Clientes WHERE nombre_cliente LIKE ? AND activo = 1 " +
                     "ORDER BY nombre_cliente";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nombre + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar clientes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clientes;
    }
    
    public Cliente buscarPorTelefono(String telefono) {
        String sql = "SELECT id_cliente, nombre_cliente, telefono, email, direccion, rfc, " +
                     "puntos_fidelidad, activo, fecha_registro " +
                     "FROM Clientes WHERE telefono = ? AND activo = 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, telefono);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearCliente(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por teléfono: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Cliente obtenerPorId(int idCliente) {
        String sql = "SELECT id_cliente, nombre_cliente, telefono, email, direccion, rfc, " +
                     "puntos_fidelidad, activo, fecha_registro " +
                     "FROM Clientes WHERE id_cliente = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCliente);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearCliente(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener cliente: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean crear(Cliente cliente) {
        String sql = "INSERT INTO Clientes (nombre_cliente, telefono, email, direccion, rfc, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, cliente.getNombreCliente());
            pstmt.setString(2, cliente.getTelefono());
            pstmt.setString(3, cliente.getEmail());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getRfc());
            pstmt.setBoolean(6, cliente.isActivo());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    cliente.setIdCliente(rs.getInt(1));
                }
                System.out.println("✓ Cliente creado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al crear cliente: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE Clientes SET nombre_cliente = ?, telefono = ?, email = ?, " +
                     "direccion = ?, rfc = ?, activo = ? WHERE id_cliente = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cliente.getNombreCliente());
            pstmt.setString(2, cliente.getTelefono());
            pstmt.setString(3, cliente.getEmail());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getRfc());
            pstmt.setBoolean(6, cliente.isActivo());
            pstmt.setInt(7, cliente.getIdCliente());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Cliente actualizado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean actualizarPuntosFidelidad(int idCliente, int puntos) {
        String sql = "UPDATE Clientes SET puntos_fidelidad = puntos_fidelidad + ? " +
                     "WHERE id_cliente = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, puntos);
            pstmt.setInt(2, idCliente);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar puntos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("id_cliente"));
        cliente.setNombreCliente(rs.getString("nombre_cliente"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setEmail(rs.getString("email"));
        cliente.setDireccion(rs.getString("direccion"));
        cliente.setRfc(rs.getString("rfc"));
        cliente.setPuntosFidelidad(rs.getInt("puntos_fidelidad"));
        cliente.setActivo(rs.getBoolean("activo"));
        cliente.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return cliente;
    }
}
