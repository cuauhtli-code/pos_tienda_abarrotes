package pos_tienda_abarrotes.model;

public class DetalleVenta {
    
	private int idDetalle;
    private int idVenta;
    private int idProducto;
    private Producto producto; // Objeto completo de producto
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private double descuento;
    private double total;
    
    // Constructor vacío
    public DetalleVenta() {}
    
    // Constructor completo
    public DetalleVenta(int idDetalle, int idVenta, int idProducto, int cantidad,
                        double precioUnitario, double subtotal, double descuento, double total) {
        this.idDetalle = idDetalle;
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.total = total;
    }
    
    // Getters y Setters
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }
    
    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }
    
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    
    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }
    
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    
    // Método de utilidad
    public void calcularTotal() {
        this.subtotal = this.cantidad * this.precioUnitario;
        this.total = this.subtotal - this.descuento;
    }
    
    @Override
    public String toString() {
        return cantidad + "x " + (producto != null ? producto.getNombreProducto() : "Producto") + " - $" + total;
    }
}