package data;

import model.SalaInformatica;

import java.sql.*;
import java.util.ArrayList;

public class SalaInformaticaDAO implements CRUD_Operation<SalaInformatica, Integer> {

    private final Connection connection;

    public SalaInformaticaDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(SalaInformatica sala) {
        if (exists(sala.getIdSala())) {
            System.err.println("⚠️ No se pudo guardar: ya existe una sala con ID " + sala.getIdSala());
            return;
        }
      
        String call = "{call proyecto343.SP_INSERT_SALA(?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, sala.getIdSala());
            cs.setString(2, sala.getNombreSala());
            cs.setInt(3, sala.getCapacidad());
            cs.setString(4, sala.getSoftwareDisponible());
            cs.setString(5, sala.getHardwareEspecial());
            cs.setString(6, sala.getUbicacion());
            cs.setString(7, sala.getEstado());
            cs.execute();
            System.out.println("✅ Sala insertada correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar sala: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void actualizarEstadoSegunReservas(int idSala) throws SQLException {
        String call = "{call proyecto343.SP_ACTUALIZAR_ESTADO_SALA(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, idSala);
            cs.execute();
        }
    }

    public boolean actualizarEstadoSala(int idSala, String estado) {
        
        String call = "{call proyecto343.SP_ACTUALIZAR_ESTADO_SALA(?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, idSala);
            cs.setString(2, estado);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar estado de sala: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<SalaInformatica> fetch() {
        ArrayList<SalaInformatica> salas = new ArrayList<>();
        String call = "{call proyecto343.SP_FETCH_SALAS_INFORMATICA(?)}";

        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            ResultSet rs = (ResultSet) cs.getObject(1);

            while (rs.next()) {
                salas.add(new SalaInformatica(
                        rs.getInt("id_sala"),
                        rs.getString("nombre_sala"),
                        rs.getInt("capacidad"),
                        rs.getString("software_disponible"),
                        rs.getString("hardware_especial"),
                        rs.getString("ubicacion"),
                        rs.getString("estado")
                ));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener salas: " + e.getMessage());
            e.printStackTrace();
        }
        return salas;
    }

    @Override
    public void update(SalaInformatica sala) {
        
        String call = "{call proyecto343.SP_UPDATE_SALA(?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, sala.getIdSala());
            cs.setString(2, sala.getNombreSala());
            cs.setInt(3, sala.getCapacidad());
            cs.setString(4, sala.getSoftwareDisponible());
            cs.setString(5, sala.getHardwareEspecial());
            cs.setString(6, sala.getUbicacion());
            cs.setString(7, sala.getEstado());
            cs.execute();
            System.out.println("✅ Sala actualizada correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar sala: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer idSala) {
        
        String call = "{call proyecto343.SP_DELETE_SALA(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, idSala);
            cs.execute();
            System.out.println("✅ Sala eliminada correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar sala: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer idSala) {
        throw new UnsupportedOperationException("El método authenticate no aplica para SalaInformaticaDAO");
    }

    public boolean exists(Integer idSala) {
        
        String call = "{? = call proyecto343.FN_SALA_EXISTS(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, idSala);
            cs.execute();
            return cs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia de sala: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public SalaInformatica findById(int idSala) {
        String call = "{call proyecto343.SP_FIND_SALA_INFORMATICA_BY_ID(?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, idSala);
            cs.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                if (rs.next()) {
                    return new SalaInformatica(
                        rs.getInt("id_sala"),
                        rs.getString("nombre_sala"),
                        rs.getInt("capacidad"),
                        rs.getString("software_disponible"),
                        rs.getString("hardware_especial"),
                        rs.getString("ubicacion"),
                        rs.getString("estado")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar sala por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<SalaInformatica> fetchDisponibles() throws SQLException {
        ArrayList<SalaInformatica> salas = new ArrayList<>();
        String call = "{call proyecto343.SP_FETCH_SALAS_DISPONIBLES(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    salas.add(new SalaInformatica(
                            rs.getInt("id_sala"),
                            rs.getString("nombre_sala"),
                            rs.getInt("capacidad"),
                            rs.getString("software_disponible"),
                            rs.getString("hardware_especial"),
                            rs.getString("ubicacion"),
                            rs.getString("estado")
                    ));
                }
            }
        }
        return salas;
    }

    public boolean salaDisponible(int idSala, Timestamp inicio, Timestamp fin) {
        
        String call = "{? = call proyecto343.FN_SALA_DISPONIBLE(?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, idSala);
            cs.setTimestamp(3, inicio);
            cs.setTimestamp(4, fin);
            cs.execute();
            return cs.getInt(1) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<SalaInformatica> fetchAll() throws SQLException {
        ArrayList<SalaInformatica> salas = new ArrayList<>();
        String call = "{call proyecto343.SP_FETCH_ALL_SALAS_INFORMATICA(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    salas.add(new SalaInformatica(
                            rs.getInt("id_sala"),
                            rs.getString("nombre_sala"),
                            rs.getInt("capacidad"),
                            rs.getString("software_disponible"),
                            rs.getString("hardware_especial"),
                            rs.getString("ubicacion"),
                            rs.getString("estado")
                    ));
                }
            }
        }
        return salas;
    }
    
}

