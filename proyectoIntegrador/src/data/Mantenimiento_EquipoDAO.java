package data;

import model.Mantenimiento_Equipo;

import java.sql.*;
import java.util.ArrayList;

public class Mantenimiento_EquipoDAO implements CRUD_Operation<Mantenimiento_Equipo, Integer> {
    private Connection connection;

    public Mantenimiento_EquipoDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Mantenimiento_Equipo mantenimientoEquipo) {
        String call = "{call proyecto343.SP_INSERT_MANTENIMIENTO_E(?, ?, ?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, mantenimientoEquipo.getIdEquipo());
            cs.setDate(2, mantenimientoEquipo.getFechaMantenimiento());
            cs.setString(3, mantenimientoEquipo.getDetalle());
            cs.setString(4, mantenimientoEquipo.getTecnicoResponsable());
            cs.registerOutParameter(5, Types.INTEGER);

            cs.execute();

            int newId = cs.getInt(5);
            mantenimientoEquipo.setIdMantenimiento(newId);

            System.out.println("Mantenimiento de equipo registrado correctamente (ID: " + newId + ").");
        } catch (SQLException e) {
            System.err.println("Error al registrar el mantenimiento de equipo.");
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Mantenimiento_Equipo> fetch() {
        ArrayList<Mantenimiento_Equipo> mantenimientos = new ArrayList<>();
        String call = "{call proyecto343.sp_fetch_mantenimientos_equipo(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Mantenimiento_Equipo mantenimiento = new Mantenimiento_Equipo(
                        rs.getInt("id_mantenimiento"),
                        rs.getInt("id_equipo"),
                        rs.getDate("fecha_mantenimiento"),
                        rs.getString("detalle"),
                        rs.getString("tecnico_responsable")
                    );
                    mantenimientos.add(mantenimiento);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mantenimientos;
    }

    @Override
    public void update(Mantenimiento_Equipo mantenimientoEquipo) {
        String call = "{call proyecto343.SP_UPDATE_MANTENIMIENTO_E(?, ?, ?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, mantenimientoEquipo.getIdMantenimiento());
            cs.setInt(2, mantenimientoEquipo.getIdEquipo());
            cs.setDate(3, mantenimientoEquipo.getFechaMantenimiento());
            cs.setString(4, mantenimientoEquipo.getDetalle());
            cs.setString(5, mantenimientoEquipo.getTecnicoResponsable());
            cs.execute();
            System.out.println("Mantenimiento de equipo actualizado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al actualizar el mantenimiento de equipo.");
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String call = "{call proyecto343.SP_DELETE_MANTENIMIENTO_E(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, id);
            cs.execute();
            System.out.println("Mantenimiento de equipo con ID " + id + " eliminado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al eliminar el mantenimiento de equipo.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String call = "{? = call proyecto343.FN_MANTENIMIENTO_E_EXISTS(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, id);
            cs.execute();
            int existe = cs.getInt(1);
            return existe > 0;
        } catch (SQLException e) {
            System.err.println("Error al autenticar el mantenimiento de equipo.");
            e.printStackTrace();
        }
        return false;
    }
 public void actualizarEstadoEquipo(int idEquipo, String estado) {
    String call = "{call proyecto343.SP_ACTUALIZAR_ESTADO_EQUIPO(?, ?)}";
    try (CallableStatement cs = connection.prepareCall(call)) {
        cs.setInt(1, idEquipo);
        cs.setString(2, estado);
        cs.execute();
    } catch (SQLException e) {
        System.err.println("Error al actualizar el estado del equipo.");
        e.printStackTrace();
    }
}

 public ArrayList<Integer> obtenerEquiposDisponibles() {
	    ArrayList<Integer> equiposDisponibles = new ArrayList<>();
	    String call = "{call proyecto343.sp_fetch_equipos_disponibles(?)}";
	    try (CallableStatement cs = connection.prepareCall(call)) {
	        cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
	        cs.execute();
	        try (ResultSet rs = (ResultSet) cs.getObject(1)) {
	            while (rs.next()) {
	                equiposDisponibles.add(rs.getInt("id_equipo"));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error al obtener los equipos disponibles.");
	        e.printStackTrace();
	    }
	    return equiposDisponibles;
	}
    public boolean estaEnMantenimiento(int idEquipo) {
        String call = "{? = call proyecto343.FN_ESTA_EN_MANTENIMIENTO(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, idEquipo);
            cs.execute();
            int resultado = cs.getInt(1);
            return resultado == 1;
        } catch (SQLException e) {
            System.err.println("Error al verificar el estado de mantenimiento.");
            e.printStackTrace();
        }
        return false;
    }





}
