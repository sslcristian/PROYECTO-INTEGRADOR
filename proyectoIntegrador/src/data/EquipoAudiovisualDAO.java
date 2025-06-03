package data;

import model.EquipoAudiovisual;

import java.sql.*;
import java.util.ArrayList;

public class EquipoAudiovisualDAO implements CRUD_Operation<EquipoAudiovisual, Integer> {
    private Connection connection;

    public EquipoAudiovisualDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(EquipoAudiovisual equipo) {
        if (equipo == null) {
            System.out.println("El objeto EquipoAudiovisual es nulo. No se puede guardar.");
            return;
        }
        if (exists(equipo.getIdEquipo())) {
            System.out.println("El ID de equipo ya está registrado en la base de datos.");
            return;
        }
        String call = "{call proyecto343.SP_INSERT_EQUIPO(?,?,?,?,?,?,?,?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, equipo.getIdEquipo());
            cs.setString(2, equipo.getNombre());
            cs.setString(3, equipo.getTipo());
            cs.setString(4, equipo.getEstado());
            cs.setString(5, equipo.getUbicacion());
            cs.setString(6, equipo.getMarca());
            cs.setString(7, equipo.getModelo());
            cs.setDate(8, equipo.getFechaAdquisicion());
            cs.execute();
            System.out.println("✅ Equipo audiovisual registrado correctamente con ID: " + equipo.getIdEquipo());
        } catch (SQLException e) {
            System.err.println("❌ Error al registrar el equipo: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<EquipoAudiovisual> fetch() {
        ArrayList<EquipoAudiovisual> equipos = new ArrayList<>();
        String call = "{call proyecto343.SP_FETCH_EQUIPOS(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, Types.REF_CURSOR); 
            cs.execute();
            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                EquipoAudiovisual equipo = new EquipoAudiovisual(
                    rs.getInt("id_equipo"),
                    rs.getString("nombre"),
                    rs.getString("tipo"),
                    rs.getString("estado"),
                    rs.getString("ubicacion"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getDate("fecha_adquisicion")
                );
                equipos.add(equipo);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener equipos: " + e.getMessage());
        }
        return equipos;
    }

    public ArrayList<EquipoAudiovisual> fetchDisponibles() {
        ArrayList<EquipoAudiovisual> equipos = new ArrayList<>();
        String call = "{call proyecto343.SP_FETCH_EQUIPOS_DISPONIBLES(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();
            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                equipos.add(new EquipoAudiovisual(
                    rs.getInt("id_equipo"),
                    rs.getString("nombre"),
                    rs.getString("tipo"),
                    rs.getString("estado"),
                    rs.getString("ubicacion"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getDate("fecha_adquisicion")
                ));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener equipos disponibles: " + e.getMessage());
        }
        return equipos;
    }

    @Override
    public void update(EquipoAudiovisual equipo) {
        if (equipo == null) {
            System.out.println("El objeto EquipoAudiovisual es nulo. No se puede actualizar.");
            return;
        }
        String call = "{call proyecto343.SP_UPDATE_EQUIPO(?,?,?,?,?,?,?,?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, equipo.getIdEquipo());
            cs.setString(2, equipo.getNombre());
            cs.setString(3, equipo.getTipo());
            cs.setString(4, equipo.getEstado());
            cs.setString(5, equipo.getUbicacion());
            cs.setString(6, equipo.getMarca());
            cs.setString(7, equipo.getModelo());
            cs.setDate(8, equipo.getFechaAdquisicion());
            cs.execute();
            System.out.println("✅ Equipo actualizado con ID: " + equipo.getIdEquipo());
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar el equipo: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer id) {
        String call = "{call proyecto343.SP_DELETE_EQUIPO(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, id);
            cs.execute();
            System.out.println("✅ Equipo con ID " + id + " eliminado correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar el equipo: " + e.getMessage());
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String call = "{? = call proyecto343.FN_EQUIPO_EXISTS(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, id);
            cs.execute();
            return cs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error en authenticate: " + e.getMessage());
        }
        return false;
    }

    public boolean exists(Integer idEquipo) {
        // Alias de authenticate para evitar duplicados
        return authenticate(idEquipo);
    }

    public EquipoAudiovisual findById(Integer idEquipo) {
        String query = "SELECT * FROM proyecto343.TBL_EQUIPO WHERE id_equipo=?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idEquipo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new EquipoAudiovisual(
                        rs.getInt("id_equipo"),
                        rs.getString("nombre"),
                        rs.getString("tipo"),
                        rs.getString("estado"),
                        rs.getString("ubicacion"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getDate("fecha_adquisicion")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error en findById: " + e.getMessage());
        }
        return null;
    }
    
}
