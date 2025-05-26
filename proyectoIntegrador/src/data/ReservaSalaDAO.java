package data;

import model.ReservaSala;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ReservaSalaDAO {

    private final ObservableList<ReservaSala> reservas = FXCollections.observableArrayList();

    // Obtener todas las reservas
    public ObservableList<ReservaSala> getAll() {
        return reservas;
    }

    // Guardar una nueva reserva
    public void save(ReservaSala reserva) {
        reservas.add(reserva);
    }

    // Actualizar una reserva (ejemplo: cambiar estado)
    public void update(ReservaSala reserva) {
        // Aquí no hacemos nada especial porque la lista tiene la referencia directa
        // Solo debes llamar tabla.refresh() en el controlador para actualizar vista
    }

    // Buscar reserva por ID (opcional)
    public ReservaSala findById(int id) {
        for (ReservaSala r : reservas) {
            if (r.getId() == id) return r;
        }
        return null;
    }
}
