package pos_tienda_abarrotes.model;

public class DetalleDevolucion {
    private int idDetalleDevolucion;
    private int idDevolucion;
    private int idProducto;
    private Producto producto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    
    public DetalleDevolucion() {}
    
    public DetalleDevolucion(int idDetalleDevolucion, int idDevolucion, int idProducto,
                             int cantidad, double precioUnitario, double subtotal) {
        this.idDetalleDevolucion = idDetalleDevolucion;
        this.idDevolucion = idDevolucion;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }
    
    public int getIdDetalleDevolucion() { return idDetalleDevolucion; }
    public void setIdDetalleDevolucion(int idDetalleDevolucion) { this.idDetalleDevolucion = idDetalleDevolucion; }
    
    public int getIdDevolucion() { return idDevolucion; }
    public void setIdDevolucion(int idDevolucion) { this.idDevolucion = idDevolucion; }
    
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
    
    @Override
    public String toString() {
        return cantidad + "x " + (producto != null ? producto.getNombreProducto() : "Producto");
    }
}