package pos_tienda_abarrotes.controller;

import java.util.Date;
import java.util.List;
import pos_tienda_abarrotes.dao.ReporteDAO;
import pos_tienda_abarrotes.dao.VentaDAO;
import pos_tienda_abarrotes.model.Venta;

public class ReporteController {
    
    private ReporteDAO reporteDAO;
    private VentaDAO ventaDAO;
    
    public ReporteController() {
        this.reporteDAO = new ReporteDAO();
        this.ventaDAO = new VentaDAO();
    }
    
    public List<Object[]> generarReporteProductosMasVendidos(Date fechaInicio, Date fechaFin, int limite) {
        if (fechaInicio == null || fechaFin == null) {
            System.err.println("Las fechas son requeridas");
            return null;
        }
        
        if (limite <= 0) {
            limite = 10; // valor por defecto
        }
        
        List<Object[]> resultados = reporteDAO.obtenerProductosMasVendidos(fechaInicio, fechaFin, limite);
        
        if (resultados.isEmpty()) {
            System.out.println("No hay datos para el período especificado");
        } else {
            System.out.println("\n=== PRODUCTOS MÁS VENDIDOS ===");
            System.out.println("Período: " + fechaInicio + " al " + fechaFin);
            System.out.println("\nRanking:");
            
            int posicion = 1;
            for (Object[] fila : resultados) {
                String nombreProducto = (String) fila[0];
                int cantidadVendida = (Integer) fila[1];
                double totalVentas = (Double) fila[2];
                
                System.out.println(posicion + ". " + nombreProducto);
                System.out.println("   Unidades vendidas: " + cantidadVendida);
                System.out.println("   Total ventas: $" + String.format("%.2f", totalVentas));
                System.out.println();
                
                posicion++;
            }
        }
        
        return resultados;
    }
    
    public List<Object[]> generarReporteVentasPorMetodoPago(Date fechaInicio, Date fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            System.err.println("Las fechas son requeridas");
            return null;
        }
        
        List<Object[]> resultados = reporteDAO.obtenerVentasPorMetodoPago(fechaInicio, fechaFin);
        
        if (resultados.isEmpty()) {
            System.out.println("No hay datos para el período especificado");
        } else {
            System.out.println("\n=== VENTAS POR MÉTODO DE PAGO ===");
            System.out.println("Período: " + fechaInicio + " al " + fechaFin);
            System.out.println();
            
            double totalGeneral = 0;
            int transaccionesTotales = 0;
            
            for (Object[] fila : resultados) {
                String metodoPago = (String) fila[0];
                int cantidadTransacciones = (Integer) fila[1];
                double totalVentas = (Double) fila[2];
                
                System.out.println(metodoPago + ":");
                System.out.println("  Transacciones: " + cantidadTransacciones);
                System.out.println("  Total: $" + String.format("%.2f", totalVentas));
                
                double porcentaje = 0;
                if (totalGeneral > 0) {
                    porcentaje = (totalVentas / totalGeneral) * 100;
                }
                
                totalGeneral += totalVentas;
                transaccionesTotales += cantidadTransacciones;
            }
            
            System.out.println("\nRESUMEN GENERAL:");
            System.out.println("  Total de transacciones: " + transaccionesTotales);
            System.out.println("  Total de ventas: $" + String.format("%.2f", totalGeneral));
        }
        
        return resultados;
    }
    
    public List<Venta> generarReporteVentasDiarias(Date fecha) {
        if (fecha == null) {
            System.err.println("La fecha es requerida");
            return null;
        }
        
        List<Venta> ventas = ventaDAO.obtenerPorFecha(fecha);
        
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas para la fecha: " + fecha);
        } else {
            System.out.println("\n=== REPORTE DE VENTAS DIARIAS ===");
            System.out.println("Fecha: " + fecha);
            System.out.println("Total de ventas: " + ventas.size());
            System.out.println();
            
            double totalDia = 0;
            
            for (Venta venta : ventas) {
                System.out.println("Folio: " + venta.getFolio());
                System.out.println("  Hora: " + venta.getFechaVenta());
                System.out.println("  Total: $" + String.format("%.2f", venta.getTotal()));
                System.out.println("  Método: " + venta.getMetodoPago());
                System.out.println("  Estado: " + venta.getEstado());
                System.out.println();
                
                if ("COMPLETADA".equals(venta.getEstado())) {
                    totalDia += venta.getTotal();
                }
            }
            
            System.out.println("TOTAL DEL DÍA: $" + String.format("%.2f", totalDia));
        }
        
        return ventas;
    }
    
    public List<Venta> generarReporteVentasPorApertura(int idApertura) {
        if (idApertura <= 0) {
            System.err.println("ID de apertura inválido");
            return null;
        }
        
        List<Venta> ventas = ventaDAO.obtenerPorApertura(idApertura);
        
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas para esta apertura");
        } else {
            System.out.println("\n=== REPORTE DE VENTAS POR TURNO ===");
            System.out.println("ID Apertura: " + idApertura);
            System.out.println("Total de ventas: " + ventas.size());
            System.out.println();
            
            double totalTurno = 0;
            int totalProductos = 0;
            
            for (Venta venta : ventas) {
                System.out.println("Folio: " + venta.getFolio() + " - $" + 
                                 String.format("%.2f", venta.getTotal()));
                
                totalTurno += venta.getTotal();
                if (venta.getDetalles() != null) {
                    totalProductos += venta.getDetalles().size();
                }
            }
            
            System.out.println("\nRESUMEN:");
            System.out.println("  Total de ventas: " + ventas.size());
            System.out.println("  Productos vendidos: " + totalProductos);
            System.out.println("  Total del turno: $" + String.format("%.2f", totalTurno));
            
            if (ventas.size() > 0) {
                double ticketPromedio = totalTurno / ventas.size();
                System.out.println("  Ticket promedio: $" + String.format("%.2f", ticketPromedio));
            }
        }
        
        return ventas;
    }
    
    public double calcularTotalVentas(Date fechaInicio, Date fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            System.err.println("Las fechas son requeridas");
            return 0;
        }
        
        double total = ventaDAO.obtenerTotalVentas(fechaInicio, fechaFin);
        
        System.out.println("\n=== TOTAL DE VENTAS ===");
        System.out.println("Período: " + fechaInicio + " al " + fechaFin);
        System.out.println("Total: $" + String.format("%.2f", total));
        
        return total;
    }
    
    public void generarEstadisticasVentas(Date fechaInicio, Date fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            System.err.println("Las fechas son requeridas");
            return;
        }
        
        System.out.println("\n=== ESTADÍSTICAS DE VENTAS ===");
        System.out.println("Período: " + fechaInicio + " al " + fechaFin);
        System.out.println();
        
        double totalVentas = ventaDAO.obtenerTotalVentas(fechaInicio, fechaFin);
        System.out.println("💰 Total de ventas: $" + String.format("%.2f", totalVentas));
        
        System.out.println("\n📊 Distribución por método de pago:");
        List<Object[]> ventasPorMetodo = reporteDAO.obtenerVentasPorMetodoPago(fechaInicio, fechaFin);
        
        for (Object[] fila : ventasPorMetodo) {
            String metodo = (String) fila[0];
            int cantidad = (Integer) fila[1];
            double total = (Double) fila[2];
            double porcentaje = (total / totalVentas) * 100;
            
			System.out.println("  - " + metodo + ": " + cantidad + " ventas, $" + String.format("%.2f", total) + " ("
					+ String.format("%.2f", porcentaje) + "%)");
        }
        
        System.out.println("\n🏆 Top 5 productos más vendidos:");
        List<Object[]> topProductos = reporteDAO.obtenerProductosMasVendidos(fechaInicio, fechaFin, 5);
        
        int posicion = 1;
        for (Object[] fila : topProductos) {
            String producto = (String) fila[0];
            int cantidad = (Integer) fila[1];
            
            System.out.println("  " + posicion + ". " + producto + " (" + cantidad + " unidades)");
            posicion++;
        }
    }
}