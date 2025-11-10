package pos_tienda_abarrotes.dao;

import pos_tienda_abarrotes.model.DatabaseConnection;
import pos_tienda_abarrotes.model.CierreCaja;
import java.sql.*;

public class CierreCajaDAO {
    
    public boolean cerrarCaja(CierreCaja cierre) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            String sqlCierre = "INSERT INTO CierreCaja (id_apertura, monto_final_sistema, " +
                               "monto_final_fisico, diferencia, total_ventas, total_efectivo, " +
                               "total_tarjeta, total_transferencia, total_vales, observaciones) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement pstmtCierre = conn.prepareStatement(sqlCierre, Statement.RETURN_GENERATED_KEYS);
            pstmtCierre.setInt(1, cierre.getIdApertura());
            pstmtCierre.setDouble(2, cierre.getMontoFinalSistema());
            pstmtCierre.setDouble(3, cierre.getMontoFinalFisico());
            pstmtCierre.setDouble(4, cierre.getDiferencia());
            pstmtCierre.setDouble(5, cierre.getTotalVentas());
            pstmtCierre.setDouble(6, cierre.getTotalEfectivo());
            pstmtCierre.setDouble(7, cierre.getTotalTarjeta());
            pstmtCierre.setDouble(8, cierre.getTotalTransferencia());
            pstmtCierre.setDouble(9, cierre.getTotalVales());
            pstmtCierre.setString(10, cierre.getObservaciones());
            
            pstmtCierre.executeUpdate();
            
            ResultSet rs = pstmtCierre.getGeneratedKeys();
            if (rs.next()) {
                cierre.setIdCierre(rs.getInt(1));
            }
            
            String sqlApertura = "UPDATE AperturaCaja SET estado = 'CERRADA' WHERE id_apertura = ?";
            PreparedStatement pstmtApertura = conn.prepareStatement(sqlApertura);
            pstmtApertura.setInt(1, cierre.getIdApertura());
            pstmtApertura.executeUpdate();
            
            conn.commit();
            System.out.println("✓ Caja cerrada exitosamente");
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error al cerrar caja: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }
    
    public CierreCaja calcularResumenCierre(int idApertura) {
        String sql = "SELECT " +
                     "a.monto_inicial, " +
                     "ISNULL(SUM(v.total), 0) as total_ventas, " +
                     "ISNULL(SUM(CASE WHEN v.metodo_pago = 'EFECTIVO' THEN v.total ELSE 0 END), 0) as total_efectivo, " +
                     "ISNULL(SUM(CASE WHEN v.metodo_pago = 'TARJETA' THEN v.total ELSE 0 END), 0) as total_tarjeta, " +
                     "ISNULL(SUM(CASE WHEN v.metodo_pago = 'TRANSFERENCIA' THEN v.total ELSE 0 END), 0) as total_transferencia, " +
                     "ISNULL(SUM(CASE WHEN v.metodo_pago = 'VALES' THEN v.total ELSE 0 END), 0) as total_vales " +
                     "FROM AperturaCaja a " +
                     "LEFT JOIN Ventas v ON a.id_apertura = v.id_apertura AND v.estado = 'COMPLETADA' " +
                     "WHERE a.id_apertura = ? " +
                     "GROUP BY a.monto_inicial";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idApertura);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                CierreCaja cierre = new CierreCaja();
                cierre.setIdApertura(idApertura);
                
                double montoInicial = rs.getDouble("monto_inicial");
                double totalVentas = rs.getDouble("total_ventas");
                double totalEfectivo = rs.getDouble("total_efectivo");
                double totalTarjeta = rs.getDouble("total_tarjeta");
                double totalTransferencia = rs.getDouble("total_transferencia");
                double totalVales = rs.getDouble("total_vales");
                
                cierre.setTotalVentas(totalVentas);
                cierre.setTotalEfectivo(totalEfectivo);
                cierre.setTotalTarjeta(totalTarjeta);
                cierre.setTotalTransferencia(totalTransferencia);
                cierre.setTotalVales(totalVales);
                
                cierre.setMontoFinalSistema(montoInicial + totalEfectivo);
                
                return cierre;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al calcular resumen de cierre: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}