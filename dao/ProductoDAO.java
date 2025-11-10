package pos_tienda_abarrotes.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import pos_tienda_abarrotes.model.DatabaseConnection;
import pos_tienda_abarrotes.model.Producto;
import pos_tienda_abarrotes.model.Categoria;
import pos_tienda_abarrotes.model.Proveedor;

public class ProductoDAO {

    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.codigo_barras, p.nombre_producto, p.descripcion, " +
                    "p.id_categoria, p.id_proveedor, p.precio_compra, p.precio_venta, " +
                    "p.stock_actual, p.stock_minimo, p.activo, p.fecha_creacion, p.fecha_modificacion, " +
                    "c.nombre_categoria, pr.nombre_proveedor " +
                    "FROM Productos p " +
                    "LEFT JOIN Categorias c ON p.id_categoria = c.id_categoria " +
                    "LEFT JOIN Proveedores pr ON p.id_proveedor = pr.id_proveedor " +
                    "WHERE p.activo = 1 " +
                    "ORDER BY p.nombre_producto";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return productos;
    }
    
    public Producto buscarPorCodigoBarras(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            return null;
        }
        
        String sql = "SELECT p.id_producto, p.codigo_barras, p.nombre_producto, p.descripcion, " +
                    "p.id_categoria, p.id_proveedor, p.precio_compra, p.precio_venta, " +
                    "p.stock_actual, p.stock_minimo, p.activo, p.fecha_creacion, p.fecha_modificacion, " +
                    "c.nombre_categoria, pr.nombre_proveedor " +
                    "FROM Productos p " +
                    "LEFT JOIN Categorias c ON p.id_categoria = c.id_categoria " +
                    "LEFT JOIN Proveedores pr ON p.id_proveedor = pr.id_proveedor " +
                    "WHERE p.codigo_barras = ? AND p.activo = 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigoBarras.trim());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearProducto(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar producto por código de barras: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.codigo_barras, p.nombre_producto, p.descripcion, " +
                    "p.id_categoria, p.id_proveedor, p.precio_compra, p.precio_venta, " +
                    "p.stock_actual, p.stock_minimo, p.activo, p.fecha_creacion, p.fecha_modificacion, " +
                    "c.nombre_categoria, pr.nombre_proveedor " +
                    "FROM Productos p " +
                    "LEFT JOIN Categorias c ON p.id_categoria = c.id_categoria " +
                    "LEFT JOIN Proveedores pr ON p.id_proveedor = pr.id_proveedor " +
                    "WHERE p.nombre_producto LIKE ? AND p.activo = 1 " +
                    "ORDER BY p.nombre_producto";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nombre + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos por nombre: " + e.getMessage());
            e.printStackTrace();
        }
        
        return productos;
    }
    
    public Producto obtenerPorId(int idProducto) {
        String sql = "SELECT p.id_producto, p.codigo_barras, p.nombre_producto, p.descripcion, " +
                    "p.id_categoria, p.id_proveedor, p.precio_compra, p.precio_venta, " +
                    "p.stock_actual, p.stock_minimo, p.activo, p.fecha_creacion, p.fecha_modificacion, " +
                    "c.nombre_categoria, pr.nombre_proveedor " +
                    "FROM Productos p " +
                    "LEFT JOIN Categorias c ON p.id_categoria = c.id_categoria " +
                    "LEFT JOIN Proveedores pr ON p.id_proveedor = pr.id_proveedor " +
                    "WHERE p.id_producto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idProducto);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearProducto(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean crear(Producto producto) {
        String sql = "INSERT INTO Productos (codigo_barras, nombre_producto, descripcion, " +
                    "id_categoria, id_proveedor, precio_compra, precio_venta, " +
                    "stock_actual, stock_minimo, activo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, producto.getCodigoBarras());
            pstmt.setString(2, producto.getNombreProducto());
            pstmt.setString(3, producto.getDescripcion());
            
            if (producto.getIdCategoria() > 0) {
                pstmt.setInt(4, producto.getIdCategoria());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            
            if (producto.getIdProveedor() > 0) {
                pstmt.setInt(5, producto.getIdProveedor());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            
            pstmt.setDouble(6, producto.getPrecioCompra());
            pstmt.setDouble(7, producto.getPrecioVenta());
            pstmt.setInt(8, producto.getStockActual());
            pstmt.setInt(9, producto.getStockMinimo());
            pstmt.setBoolean(10, producto.isActivo());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    producto.setIdProducto(rs.getInt(1));
                }
                System.out.println("✓ Producto creado exitosamente");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al crear producto: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE Productos SET codigo_barras = ?, nombre_producto = ?, " +
                    "descripcion = ?, id_categoria = ?, id_proveedor = ?, " +
                    "precio_compra = ?, precio_venta = ?, stock_actual = ?, " +
                    "stock_minimo = ?, activo = ?, fecha_modificacion = GETDATE() " +
                    "WHERE id_producto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, producto.getCodigoBarras());
            pstmt.setString(2, producto.getNombreProducto());
            pstmt.setString(3, producto.getDescripcion());
            
            if (producto.getIdCategoria() > 0) {
                pstmt.setInt(4, producto.getIdCategoria());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            
            if (producto.getIdProveedor() > 0) {
                pstmt.setInt(5, producto.getIdProveedor());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            
            pstmt.setDouble(6, producto.getPrecioCompra());
            pstmt.setDouble(7, producto.getPrecioVenta());
            pstmt.setInt(8, producto.getStockActual());
            pstmt.setInt(9, producto.getStockMinimo());
            pstmt.setBoolean(10, producto.isActivo());
            pstmt.setInt(11, producto.getIdProducto());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Producto actualizado exitosamente");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean actualizarStock(int idProducto, int nuevoStock) {
        String sql = "UPDATE Productos SET stock_actual = ?, fecha_modificacion = GETDATE() " +
                    "WHERE id_producto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nuevoStock);
            pstmt.setInt(2, idProducto);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public List<Producto> obtenerProductosStockBajo() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.codigo_barras, p.nombre_producto, p.descripcion, " +
                    "p.id_categoria, p.id_proveedor, p.precio_compra, p.precio_venta, " +
                    "p.stock_actual, p.stock_minimo, p.activo, p.fecha_creacion, p.fecha_modificacion, " +
                    "c.nombre_categoria, pr.nombre_proveedor " +
                    "FROM Productos p " +
                    "LEFT JOIN Categorias c ON p.id_categoria = c.id_categoria " +
                    "LEFT JOIN Proveedores pr ON p.id_proveedor = pr.id_proveedor " +
                    "WHERE p.stock_actual <= p.stock_minimo AND p.activo = 1 " +
                    "ORDER BY p.stock_actual ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos con stock bajo: " + e.getMessage());
            e.printStackTrace();
        }
        
        return productos;
    }
    
    public List<Producto> obtenerPorCategoria(int idCategoria) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.codigo_barras, p.nombre_producto, p.descripcion, " +
                    "p.id_categoria, p.id_proveedor, p.precio_compra, p.precio_venta, " +
                    "p.stock_actual, p.stock_minimo, p.activo, p.fecha_creacion, p.fecha_modificacion, " +
                    "c.nombre_categoria, pr.nombre_proveedor " +
                    "FROM Productos p " +
                    "LEFT JOIN Categorias c ON p.id_categoria = c.id_categoria " +
                    "LEFT JOIN Proveedores pr ON p.id_proveedor = pr.id_proveedor " +
                    "WHERE p.id_categoria = ? AND p.activo = 1 " +
                    "ORDER BY p.nombre_producto";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCategoria);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos por categoría: " + e.getMessage());
            e.printStackTrace();
        }
        
        return productos;
    }
    
    public boolean existeCodigoBarras(String codigoBarras) {
        String sql = "SELECT COUNT(*) as total FROM Productos WHERE codigo_barras = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigoBarras);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar código de barras: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean desactivar(int idProducto) {
        String sql = "UPDATE Productos SET activo = 0, fecha_modificacion = GETDATE() " +
                    "WHERE id_producto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idProducto);
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Producto desactivado exitosamente");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al desactivar producto: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setIdProducto(rs.getInt("id_producto"));
        producto.setCodigoBarras(rs.getString("codigo_barras"));
        producto.setNombreProducto(rs.getString("nombre_producto"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setIdCategoria(rs.getInt("id_categoria"));
        producto.setIdProveedor(rs.getInt("id_proveedor"));
        producto.setPrecioCompra(rs.getDouble("precio_compra"));
        producto.setPrecioVenta(rs.getDouble("precio_venta"));
        producto.setStockActual(rs.getInt("stock_actual"));
        producto.setStockMinimo(rs.getInt("stock_minimo"));
        producto.setActivo(rs.getBoolean("activo"));
        producto.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        producto.setFechaModificacion(rs.getTimestamp("fecha_modificacion"));
        
        if (rs.getInt("id_categoria") > 0) {
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(rs.getInt("id_categoria"));
            categoria.setNombreCategoria(rs.getString("nombre_categoria"));
            producto.setCategoria(categoria);
        }
        
        if (rs.getInt("id_proveedor") > 0) {
            Proveedor proveedor = new Proveedor();
            proveedor.setIdProveedor(rs.getInt("id_proveedor"));
            proveedor.setNombreProveedor(rs.getString("nombre_proveedor"));
            producto.setProveedor(proveedor);
        }
        
        return producto;
    }
}