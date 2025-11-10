package pos_tienda_abarrotes.controller;

import pos_tienda_abarrotes.dao.UsuarioDAO;
import pos_tienda_abarrotes.model.Usuario;
import pos_tienda_abarrotes.model.Rol;

public class LoginController {
    
    private UsuarioDAO usuarioDAO;
    private static Usuario usuarioActual;
    
    public LoginController() {
        this.usuarioDAO = new UsuarioDAO();
    }
    
    public Usuario iniciarSesion(String nombreUsuario, String contrasena) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            System.err.println("El nombre de usuario es requerido");
            return null;
        }
        
        if (contrasena == null || contrasena.trim().isEmpty()) {
            System.err.println("La contraseña es requerida");
            return null;
        }
        
        Usuario usuario = usuarioDAO.validarLogin(nombreUsuario.trim(), contrasena);
        
        if (usuario != null) {
            if (!usuario.isActivo()) {
                System.err.println("El usuario está inactivo. Contacte al administrador");
                return null;
            }
            
            usuarioActual = usuario;
            
            System.out.println("✓ Inicio de sesión exitoso");
            System.out.println("  Usuario: " + usuario.getNombreCompleto());
            System.out.println("  Rol: " + usuario.getRol().getNombreRol());
            
            return usuario;
        } else {
            System.err.println("✗ Credenciales incorrectas");
            return null;
        }
    }

    public void cerrarSesion() {
        if (usuarioActual != null) {
            System.out.println("Cerrando sesión de: " + usuarioActual.getNombreCompleto());
            usuarioActual = null;
        }
    }
    
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }
    
    public static boolean tieneRol(String nombreRol) {
        if (usuarioActual == null || usuarioActual.getRol() == null) {
            return false;
        }
        return usuarioActual.getRol().getNombreRol().equalsIgnoreCase(nombreRol);
    }
    
    public static boolean esAdministrador() {
        return tieneRol("ADMINISTRADOR");
    }
    
    public static boolean esSupervisor() {
        return tieneRol("SUPERVISOR");
    }
    
    public static boolean esCajero() {
        return tieneRol("CAJERO");
    }
    
    public static boolean tienePermisosSupervisor() {
        return esAdministrador() || esSupervisor();
    }
    
    public static boolean tienePermisosAdministrador() {
        return esAdministrador();
    }
    
    public static int getIdUsuarioActual() {
        if (usuarioActual != null) {
            return usuarioActual.getIdUsuario();
        }
        return 0;
    }
    
    public static String getNombreUsuarioActual() {
        if (usuarioActual != null) {
            return usuarioActual.getNombreCompleto();
        }
        return "Sin sesión";
    }
    
    public static String getRolUsuarioActual() {
        if (usuarioActual != null && usuarioActual.getRol() != null) {
            return usuarioActual.getRol().getNombreRol();
        }
        return "Sin rol";
    }
    
    public boolean validarDatosUsuario(String nombreUsuario, String nombreCompleto, int idRol) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            System.err.println("El nombre de usuario es requerido");
            return false;
        }
        
        if (nombreUsuario.trim().length() < 3) {
            System.err.println("El nombre de usuario debe tener al menos 3 caracteres");
            return false;
        }
        
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            System.err.println("El nombre completo es requerido");
            return false;
        }
        
        if (idRol <= 0) {
            System.err.println("Debe seleccionar un rol válido");
            return false;
        }
        
        return true;
    }
    
    public boolean validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.trim().isEmpty()) {
            System.err.println("La contraseña es requerida");
            return false;
        }
        
        if (contrasena.length() < 6) {
            System.err.println("La contraseña debe tener al menos 6 caracteres");
            return false;
        }
        
        return true;
    }
}
