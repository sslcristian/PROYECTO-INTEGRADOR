package data;

import model.Mantenimiento_Sala;

import java.sql.*;
import java.util.ArrayList;

public class Mantenimiento_SalaDAO implements CRUD_Operation<Mantenimiento_Sala, Integer> {
    private Connection connection;

    public Mantenimiento_SalaDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Mantenimiento_Sala mantenimientoSala) {
        String call = "{call proyecto343.SP_INSERT_MANTENIMIENTO_S(?, ?, ?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, mantenimientoSala.getIdSala());
            cs.setDate(2, mantenimientoSala.getFechaMantenimiento());
            cs.setString(3, mantenimientoSala.getDetalle());
            cs.setString(4, mantenimientoSala.getTecnicoResponsable());
            cs.registerOutParameter(5, Types.INTEGER);

            cs.execute();

            int newId = cs.getInt(5);
            mantenimientoSala.setIdMantenimiento(newId);

            System.out.println("Mantenimiento de sala registrado correctamente (ID: " + newId + ").");
        } catch (SQLException e) {
            System.err.println("Error al registrar el mantenimiento de la sala.");
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Mantenimiento_Sala> fetch() {
        ArrayList<Mantenimiento_Sala> mantenimientos = new ArrayList<>();
        String call = "{call proyecto343.sp_fetch_mantenimientos_sala(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Mantenimiento_Sala mantenimiento = new Mantenimiento_Sala(
                        rs.getInt("id_mantenimiento"),
                        rs.getInt("id_sala"),
                        rs.getDate("fecha_mantenimiento"),
                        rs.getString("detalle"),
                        rs.getString("tecnico_responsable")
                    );
                    mantenimientos.add(mantenimiento);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los mantenimientos de sala.");
            e.printStackTrace();
        }
        return mantenimientos;
    }

    @Override
    public void update(Mantenimiento_Sala mantenimientoSala) {
        String call = "{call proyecto343.SP_UPDATE_MANTENIMIENTO_S(?, ?, ?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, mantenimientoSala.getIdMantenimiento());
            cs.setInt(2, mantenimientoSala.getIdSala());
            cs.setDate(3, mantenimientoSala.getFechaMantenimiento());
            cs.setString(4, mantenimientoSala.getDetalle());
            cs.setString(5, mantenimientoSala.getTecnicoResponsable());

            cs.execute();
            System.out.println("Mantenimiento de sala actualizado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al actualizar el mantenimiento de la sala.");
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String call = "{call proyecto343.SP_DELETE_MANTENIMIENTO_S(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, id);
            cs.execute();
            System.out.println("Mantenimiento de sala con ID " + id + " eliminado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al eliminar el mantenimiento de la sala.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String call = "{? = call proyecto343.FN_MANTENIMIENTO_S_EXISTS(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, id);
            cs.execute();
            int existe = cs.getInt(1);
            return existe > 0;
        } catch (SQLException e) {
            System.err.println("Error al autenticar el mantenimiento de sala.");
            e.printStackTrace();
        }
        return false;
    }

    public void actualizarEstadoSala(int idSala, String estado) {
        String call = "{call proyecto343.SP_ACTUALIZAR_ESTADO_SALA(?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, idSala);
            cs.setString(2, estado);
            cs.execute();
        } catch (SQLException e) {
            System.err.println("Error al actualizar el estado de la sala.");
            e.printStackTrace();
        }
    }

    public boolean estaEnMantenimientoOcupada(int idSala) {
        String call = "{? = call proyecto343.FN_SALA_MTO_OCUPADA(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, idSala);
            cs.execute();
            int result = cs.getInt(1);
            return result == 1;
        } catch (SQLException e) {
            System.err.println("Error al verificar si la sala está en mantenimiento u ocupada.");
            e.printStackTrace();
        }
        return false;
    }


    public ArrayList<Integer> obtenerSalasDisponibles() {
        ArrayList<Integer> salasDisponibles = new ArrayList<>();
        String call = "{call proyecto343.sp_fetch_salas_disponibles(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    salasDisponibles.add(rs.getInt("id_sala"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener las salas disponibles.");
            e.printStackTrace();
        }
        return salasDisponibles;
    }
   

}
