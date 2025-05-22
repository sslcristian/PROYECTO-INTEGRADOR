package data;

import model.SalaPrestada;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ExcelService {

    public static final String RUTA_DESCARGAS = System.getProperty("user.home") + File.separator + "Downloads";

    // 📌 Método para crear la plantilla en Excel con formato adecuado
    public static void createExcelFormat(String filename) {
        File archivoDestino = new File(RUTA_DESCARGAS + File.separator + filename);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet hoja = workbook.createSheet("HorariosSalas");

            Row encabezado = hoja.createRow(0);
            encabezado.createCell(0).setCellValue("ID Sala");
            encabezado.createCell(1).setCellValue("Fecha Inicio (dd/MM/yyyy HH:mm)");
            encabezado.createCell(2).setCellValue("Fecha Fin (dd/MM/yyyy HH:mm)");
            encabezado.createCell(3).setCellValue("Observaciones");

            for (int i = 0; i < 4; i++) {
                hoja.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
                workbook.write(fos);
                System.out.println("✅ Plantilla creada correctamente en: " + archivoDestino.getAbsolutePath());
            }

        } catch (IOException e) {
            System.err.println("❌ Error al crear plantilla: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 📌 Método para leer datos desde Excel y convertirlos en objetos de tipo SalaPrestada
    public static ArrayList<SalaPrestada> fetchExcel(File archivoOrigen) {
        ArrayList<SalaPrestada> listaSalas = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(archivoOrigen);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet hoja = workbook.getSheetAt(0);

            for (Row fila : hoja) {
                if (fila.getRowNum() == 0) continue; // Saltar encabezado

                try {
                    // 📌 Validación de ID de Sala
                    Cell cellIdSala = fila.getCell(0);
                    if (cellIdSala == null || cellIdSala.getCellType() != CellType.NUMERIC) continue;
                    int idSala = (int) cellIdSala.getNumericCellValue();

                    // 📌 Extraer y validar fechas con hora
                    Cell cellFechaInicio = fila.getCell(1);
                    Timestamp fechaInicio = extraerFechaDesdeCelda(cellFechaInicio);

                    Cell cellFechaFin = fila.getCell(2);
                    Timestamp fechaFin = extraerFechaDesdeCelda(cellFechaFin);

                    // 📌 Validación de observaciones
                    Cell cellObservaciones = fila.getCell(3);
                    String observaciones = (cellObservaciones != null) ? obtenerValorCeldaComoTexto(cellObservaciones) : "";

                    // 📌 Validación de datos antes de agregar
                    if (fechaInicio == null || fechaFin == null || idSala <= 0) {
                        System.err.println("⚠️ Datos inválidos en fila " + fila.getRowNum());
                        continue;
                    }

                    // 📌 Imprimir datos obtenidos para depuración
                    System.out.println("Datos obtenidos: ID Sala=" + idSala + ", Fecha Inicio=" + fechaInicio + ", Fecha Fin=" + fechaFin + ", Observaciones=" + observaciones);

                 // 📌 Crear objeto y agregar a la lista
                    int idSolicitudS = 0; // Valor por defecto

                    // Convertir `Timestamp` a `Date` antes de pasarlo al constructor
                    java.sql.Date fechaInicioSQL = new java.sql.Date(fechaInicio.getTime());
                    java.sql.Date fechaFinSQL = new java.sql.Date(fechaFin.getTime());

                    SalaPrestada sala = new SalaPrestada(idSolicitudS, idSala, fechaInicioSQL, fechaFinSQL, observaciones);
                    listaSalas.add(sala);


                } catch (Exception ex) {
                    System.err.println("⚠️ Error en fila " + fila.getRowNum() + ": " + ex.getMessage());
                    continue;
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Error al leer archivo Excel: " + e.getMessage());
            e.printStackTrace();
        }

        return listaSalas;
    }

    private static Timestamp extraerFechaDesdeCelda(Cell celda) {
        if (celda == null) throw new IllegalArgumentException("Celda de fecha vacía");

        try {
            // Si es fecha nativa de Excel (NUMERIC)
            if (celda.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(celda)) {
                java.util.Date utilDate = celda.getDateCellValue();
                return Timestamp.valueOf(utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            } else if (celda.getCellType() == CellType.STRING) {
                String valor = celda.getStringCellValue().trim();
                
                // Quitar caracteres invisibles y normalizar espacios
                valor = valor.replace('\u202C', ' ').replaceAll("\\s+", " ").replaceAll("\\.", "").replaceAll("a m", "AM").replaceAll("p m", "PM")
                             .replaceAll("a\\. m\\.", "AM").replaceAll("p\\. m\\.", "PM").toUpperCase();

                // Intentar con 24h y 12h (en español e inglés)
                DateTimeFormatter[] formatos = new DateTimeFormatter[] {
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", new java.util.Locale("es", "ES"))
                };

                for (DateTimeFormatter formatter : formatos) {
                    try {
                        LocalDateTime fechaHora = LocalDateTime.parse(valor, formatter);
                        return Timestamp.valueOf(fechaHora);
                    } catch (Exception ignored) {}
                }
                System.err.println("❌ No se pudo interpretar la fecha: [" + valor + "]");
            }
        } catch (Exception e) {
            System.err.println("❌ Error procesando fecha: " + e.getMessage());
        }

        return null;
    }

    private static String obtenerValorCeldaComoTexto(Cell celda) {
        if (celda == null) {
            return "";
        }

        return switch (celda.getCellType()) {
            case STRING -> celda.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(celda)) {
                    java.util.Date fecha = celda.getDateCellValue();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    yield fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter);
                } else {
                    yield String.valueOf(celda.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(celda.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield celda.getStringCellValue();
                } catch (IllegalStateException e) {
                    yield String.valueOf(celda.getNumericCellValue());
                }
            }
            default -> "";
        };
    }
}
