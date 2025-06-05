package data;

import model.SalaPrestada;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.SalaPrestadaConCedula;

public class SalaPrestadaDAO implements CRUD_Operation<SalaPrestada, Integer> {
    private Connection connection;

    public SalaPrestadaDAO(Connection connection) {
        this.connection = connection;
    }

    public void save(SalaPrestada salaPrestada) {
        String call = "{ call proyecto343.SP_INSERTAR_SALA_PRESTADA(?, ?, ?, ?, ?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setObject(1, salaPrestada.getIdSolicitudS(), java.sql.Types.INTEGER);
            cs.setInt(2, salaPrestada.getIdSala());
            cs.setTimestamp(3, new Timestamp(salaPrestada.getFechaInicio().getTime()));
            cs.setTimestamp(4, new Timestamp(salaPrestada.getFechaFin().getTime()));
            cs.setString(5, salaPrestada.getObservaciones());
            cs.execute();
            System.out.println("Sala prestada registrada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al insertar la sala prestada.");
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<SalaPrestada> fetch() {
        ArrayList<SalaPrestada> salasPrestadas = new ArrayList<>();
        String call = "{call proyecto343.sp_fetch_salas_prestadas_vi(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    SalaPrestada salaPrestada = new SalaPrestada(
                        rs.getInt("id_prestamo_s"),
                        rs.getInt("id_solicitud_s"),
                        rs.getInt("id_sala"),
                        rs.getDate("fecha_inicio"),
                        rs.getDate("fecha_fin"),
                        rs.getString("observaciones")
                    );
                    salasPrestadas.add(salaPrestada);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener las salas prestadas: " + e.getMessage());
            e.printStackTrace();
        }
        return salasPrestadas;
    }
    public ArrayList<SalaPrestada> fetchTodas() {
        ArrayList<SalaPrestada> salasPrestadas = new ArrayList<>();
        String call = "{call proyecto343.sp_fetch_todas_salas_prestadas(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    salasPrestadas.add(new SalaPrestada(
                        rs.getInt("id_prestamo_s"),
                        rs.getInt("id_solicitud_s"),
                        rs.getInt("id_sala"),
                        rs.getDate("fecha_inicio"),
                        rs.getDate("fecha_fin"),
                        rs.getString("observaciones")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las reservas: " + e.getMessage());
            e.printStackTrace();
        }
        return salasPrestadas;
    }
    public List<SalaPrestadaConCedula> fetchConCedulaUsuario() {
        List<SalaPrestadaConCedula> lista = new ArrayList<>();
        String call = "{call proyecto343.sp_salas_prestadas_cedula(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    SalaPrestadaConCedula sala = new SalaPrestadaConCedula(
                        rs.getInt("id_prestamo_s"),
                        rs.getInt("id_solicitud_s"),
                        rs.getInt("id_sala"),
                        rs.getDate("fecha_inicio"),
                        rs.getDate("fecha_fin"),
                        rs.getString("observaciones"),
                        rs.getLong("cedula_usuario")
                    );
                    lista.add(sala);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reservas con cédula: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
    public void update(SalaPrestada salaPrestada) {
        String call = "{ call proyecto343.SP_ACTUALIZAR_SALA_PRESTADA(?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, salaPrestada.getIdPrestamoS());
            cs.setObject(2, salaPrestada.getIdSolicitudS(), java.sql.Types.INTEGER);
            cs.setInt(3, salaPrestada.getIdSala());
            cs.setDate(4, salaPrestada.getFechaInicio());
            cs.setDate(5, salaPrestada.getFechaFin());
            cs.setString(6, salaPrestada.getObservaciones());
            cs.execute();
            System.out.println("Sala prestada actualizada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al actualizar la sala prestada.");
            e.printStackTrace();
        }
    }

    public void delete(Integer id) {
        String call = "{ call proyecto343.SP_ELIMINAR_SALA_PRESTADA(?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, id);
            cs.execute();
            System.out.println("Sala prestada eliminada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al eliminar la sala prestada.");
            e.printStackTrace();
        }
    }

    public boolean authenticate(Integer id) {
        String call = "{ ? = call proyecto343.FN_AUTENTICAR_SALA_PRESTADA(?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setInt(2, id);
            cs.execute();
            int existe = cs.getInt(1);
            return existe > 0;
        } catch (SQLException e) {
            System.err.println("Error al autenticar la sala prestada.");
            e.printStackTrace();
        }
        return false;
    }

    public List<SalaPrestada> obtenerHistorialSalas() throws SQLException {
        List<SalaPrestada> historial = new ArrayList<>();
        String call = "{call proyecto343.sp_historial_salas_prestadas(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    historial.add(new SalaPrestada(
                        rs.getInt("id_prestamo_s"),
                        rs.getInt("id_solicitud_s"),
                        rs.getInt("id_sala"),
                        rs.getDate("fecha_inicio"),
                        rs.getDate("fecha_fin"),
                        rs.getString("observaciones")
                    ));
                }
            }
        }
        return historial;
    }

    public SalaPrestada obtenerSalaPrestadaFromResultSet(ResultSet resultSet) throws SQLException {
        int idPrestamo = resultSet.getInt("id_prestamo_s");
        int idSolicitud = resultSet.getInt("id_solicitud_s");
        int idSala = resultSet.getInt("id_sala");
        Date fechaInicio = resultSet.getDate("fecha_inicio");
        Date fechaFin = resultSet.getDate("fecha_fin");
        String observaciones = resultSet.getString("observaciones");

        fechaInicio = (fechaInicio != null) ? fechaInicio : new Date(System.currentTimeMillis());
        fechaFin = (fechaFin != null) ? fechaFin : new Date(System.currentTimeMillis());

        return new SalaPrestada(idPrestamo, idSolicitud, idSala, fechaInicio, fechaFin, observaciones);
    }

    public List<SalaPrestada> obtenerHistorialSalasPorFecha(Date fechaInicio, Date fechaFin) {
        List<SalaPrestada> historialSalas = new ArrayList<>();
        String call = "{call proyecto343.sp_historial_salas_por_fecha(?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setDate(1, fechaInicio);
            cs.setDate(2, fechaFin);
            cs.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                while (rs.next()) {
                    SalaPrestada salaPrestada = new SalaPrestada(
                        rs.getInt("id_prestamo_s"),
                        rs.getInt("id_solicitud_s"),
                        rs.getInt("id_sala"),
                        rs.getDate("fecha_inicio"),
                        rs.getDate("fecha_fin"),
                        rs.getString("observaciones")
                    );
                    historialSalas.add(salaPrestada);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historialSalas;
    }

    public boolean existeConflictoHorario(int idSala, Date nuevoFechaInicio, Date nuevoFechaFin) {
        String call = "{ ? = call proyecto343.FN_CONFLICTO_HORARIO(?, ?, ?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setInt(2, idSala);
            cs.setTimestamp(3, new Timestamp(nuevoFechaInicio.getTime()));
            cs.setTimestamp(4, new Timestamp(nuevoFechaFin.getTime()));
            cs.execute();
            int count = cs.getInt(1);
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean salaInformaticaExiste(int idSala) {
        String call = "{ ? = call proyecto343.FN_SALA_INFORMATICA_EXISTE(?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setInt(2, idSala);
            cs.execute();
            int existe = cs.getInt(1);
            return existe > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
   
}
