package pos_tienda_abarrotes.controller;

import java.util.List;
import pos_tienda_abarrotes.dao.RolDAO;
import pos_tienda_abarrotes.dao.UsuarioDAO;
import pos_tienda_abarrotes.model.Rol;
import pos_tienda_abarrotes.model.Usuario;

public class UsuarioController {
    
    private UsuarioDAO usuarioDAO;
    private RolDAO rolDAO;
    
    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
        this.rolDAO = new RolDAO();
    }
    
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioDAO.obtenerTodos();
    }
    
    public Usuario obtenerUsuarioPorId(int idUsuario) {
        if (idUsuario <= 0) {
            System.err.println("ID de usuario inválido");
            return null;
        }
        
        return usuarioDAO.obtenerPorId(idUsuario);
    }
    
    public boolean crearUsuario(Usuario usuario) {
        if (!validarUsuario(usuario)) {
            return false;
        }

        if (usuarioDAO.existeNombreUsuario(usuario.getNombreUsuario())) {
            System.err.println("El nombre de usuario ya existe: " + usuario.getNombreUsuario());
            return false;
        }
        
        if (usuario.getContrasena() == null || usuario.getContrasena().length() < 6) {
            System.err.println("La contraseña debe tener al menos 6 caracteres");
            return false;
        }
        
        boolean resultado = usuarioDAO.crear(usuario);
        
        if (resultado) {
            System.out.println("✓ Usuario creado: " + usuario.getNombreUsuario());
        }
        
        return resultado;
    }
    
    public boolean actualizarUsuario(Usuario usuario) {
        // Validar datos del usuario
        if (!validarUsuario(usuario)) {
            return false;
        }
        
        if (usuario.getIdUsuario() <= 0) {
            System.err.println("ID de usuario inválido");
            return false;
        }
        
        boolean resultado = usuarioDAO.actualizar(usuario);
        
        if (resultado) {
            System.out.println("✓ Usuario actualizado: " + usuario.getNombreUsuario());
        }
        
        return resultado;
    }
    
    public boolean cambiarContrasena(int idUsuario, String nuevaContrasena) {
        if (idUsuario <= 0) {
            System.err.println("ID de usuario inválido");
            return false;
        }
        
        if (nuevaContrasena == null || nuevaContrasena.length() < 6) {
            System.err.println("La contraseña debe tener al menos 6 caracteres");
            return false;
        }
        
        boolean resultado = usuarioDAO.actualizarContrasena(idUsuario, nuevaContrasena);
        
        if (resultado) {
            System.out.println("✓ Contraseña actualizada");
        }
        
        return resultado;
    }
    
    public boolean desactivarUsuario(int idUsuario) {
        if (idUsuario <= 0) {
            System.err.println("ID de usuario inválido");
            return false;
        }
        
        if (LoginController.haySesionActiva() && 
            LoginController.getIdUsuarioActual() == idUsuario) {
            System.err.println("No puede desactivar su propio usuario");
            return false;
        }
        
        boolean resultado = usuarioDAO.desactivar(idUsuario);
        
        if (resultado) {
            System.out.println("✓ Usuario desactivado");
        }
        
        return resultado;
    }

    public List<Rol> obtenerRoles() {
        return rolDAO.obtenerTodos();
    }
    
    public Rol obtenerRolPorId(int idRol) {
        if (idRol <= 0) {
            System.err.println("ID de rol inválido");
            return null;
        }
        
        return rolDAO.obtenerPorId(idRol);
    }
    
    private boolean validarUsuario(Usuario usuario) {
        if (usuario == null) {
            System.err.println("El usuario no puede ser nulo");
            return false;
        }
        
        if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().trim().isEmpty()) {
            System.err.println("El nombre de usuario es requerido");
            return false;
        }
        
        if (usuario.getNombreUsuario().trim().length() < 3) {
            System.err.println("El nombre de usuario debe tener al menos 3 caracteres");
            return false;
        }
        
        if (usuario.getNombreCompleto() == null || usuario.getNombreCompleto().trim().isEmpty()) {
            System.err.println("El nombre completo es requerido");
            return false;
        }
        
        if (usuario.getIdRol() <= 0) {
            System.err.println("Debe seleccionar un rol válido");
            return false;
        }
        
        return true;
    }
}