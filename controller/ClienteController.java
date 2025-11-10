package pos_tienda_abarrotes.controller;

import java.util.List;
import pos_tienda_abarrotes.dao.ClienteDAO;
import pos_tienda_abarrotes.model.Cliente;

class ClienteController {
    
    private ClienteDAO clienteDAO;
    
    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }
    
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteDAO.obtenerTodos();
    }
    
    public List<Cliente> buscarClientesPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("El nombre es requerido para la búsqueda");
            return null;
        }
        
        List<Cliente> clientes = clienteDAO.buscarPorNombre(nombre.trim());
        
        if (clientes.isEmpty()) {
            System.out.println("No se encontraron clientes con el nombre: " + nombre);
        }
        
        return clientes;
    }
    
    
    public Cliente buscarClientePorTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            System.err.println("El teléfono es requerido");
            return null;
        }
        
        Cliente cliente = clienteDAO.buscarPorTelefono(telefono.trim());
        
        if (cliente == null) {
            System.out.println("No se encontró cliente con teléfono: " + telefono);
        }
        
        return cliente;
    }
    
    public Cliente obtenerClientePorId(int idCliente) {
        if (idCliente <= 0) {
            System.err.println("ID de cliente inválido");
            return null;
        }
        
        return clienteDAO.obtenerPorId(idCliente);
    }

    public boolean crearCliente(Cliente cliente) {
        if (!validarCliente(cliente)) {
            return false;
        }
        
        if (cliente.getTelefono() != null && !cliente.getTelefono().trim().isEmpty()) {
            Cliente clienteExistente = clienteDAO.buscarPorTelefono(cliente.getTelefono());
            if (clienteExistente != null) {
                System.err.println("Ya existe un cliente con el teléfono: " + cliente.getTelefono());
                System.out.println("Cliente existente: " + clienteExistente.getNombreCliente());
                return false;
            }
        }
        
        boolean resultado = clienteDAO.crear(cliente);
        
        if (resultado) {
            System.out.println("✓ Cliente creado: " + cliente.getNombreCliente());
        }
        
        return resultado;
    }
    
    public boolean actualizarCliente(Cliente cliente) {
        if (!validarCliente(cliente)) {
            return false;
        }
        
        if (cliente.getIdCliente() <= 0) {
            System.err.println("ID de cliente inválido");
            return false;
        }
        
        boolean resultado = clienteDAO.actualizar(cliente);
        
        if (resultado) {
            System.out.println("✓ Cliente actualizado: " + cliente.getNombreCliente());
        }
        
        return resultado;
    }
    
    public boolean agregarPuntosFidelidad(int idCliente, int puntos) {
        if (idCliente <= 0) {
            System.err.println("ID de cliente inválido");
            return false;
        }
        
        if (puntos <= 0) {
            System.err.println("Los puntos deben ser mayores a cero");
            return false;
        }
        
        boolean resultado = clienteDAO.actualizarPuntosFidelidad(idCliente, puntos);
        
        if (resultado) {
            System.out.println("✓ Puntos agregados: +" + puntos);
        }
        
        return resultado;
    }
    
    public int calcularPuntosPorVenta(double totalVenta) {
        if (totalVenta <= 0) {
            return 0;
        }
        
        int puntos = (int) (totalVenta / 10);
        
        return puntos;
    }
    
    private boolean validarCliente(Cliente cliente) {
        if (cliente == null) {
            System.err.println("El cliente no puede ser nulo");
            return false;
        }
        
        if (cliente.getNombreCliente() == null || cliente.getNombreCliente().trim().isEmpty()) {
            System.err.println("El nombre del cliente es requerido");
            return false;
        }
        
        if (cliente.getTelefono() != null && !cliente.getTelefono().trim().isEmpty()) {
            String telefono = cliente.getTelefono().trim();
            if (telefono.length() < 10) {
                System.err.println("El teléfono debe tener al menos 10 dígitos");
                return false;
            }
        }
        
        if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty()) {
            String email = cliente.getEmail().trim();
            if (!email.contains("@") || !email.contains(".")) {
                System.err.println("El formato del email no es válido");
                return false;
            }
        }
        
        return true;
    }
    
    public boolean validarRFC(String rfc) {
        if (rfc == null || rfc.trim().isEmpty()) {
            return true;
        }
        
        String rfcLimpio = rfc.trim().toUpperCase();
        
        if (rfcLimpio.length() != 12 && rfcLimpio.length() != 13) {
            System.err.println("El RFC debe tener 12 o 13 caracteres");
            return false;
        }
        
        if (!rfcLimpio.matches("^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$")) {
            System.err.println("El formato del RFC no es válido");
            return false;
        }
        return true;
    }
}
