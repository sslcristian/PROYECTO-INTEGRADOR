package data;

import model.SalaPrestada;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.Date;
import java.time.ZoneId;
import java.util.ArrayList;

public class ExcelService {

    public static final String RUTA_DESCARGAS = System.getProperty("user.home") + File.separator + "Downloads";

    // ✅ Crear plantilla Excel
    public static void createExcelFormat(String filename) {
        File archivoDestino = new File(RUTA_DESCARGAS + File.separator + filename);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet hoja = workbook.createSheet("HorariosSalas");

            Row encabezado = hoja.createRow(0);
            encabezado.createCell(0).setCellValue("ID Sala");
            encabezado.createCell(1).setCellValue("Fecha Inicio (yyyy-MM-dd)");
            encabezado.createCell(2).setCellValue("Fecha Fin (yyyy-MM-dd)");
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

    // ✅ Leer datos desde archivo Excel
    public static ArrayList<SalaPrestada> fetchExcel(File archivoOrigen) {
        ArrayList<SalaPrestada> listaSalas = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(archivoOrigen);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet hoja = workbook.getSheetAt(0);

            for (Row fila : hoja) {
                if (fila.getRowNum() == 0) continue; // Saltar encabezado

                try {
                    // Obtener ID Sala
                    Cell cellIdSala = fila.getCell(0);
                    int idSala = (int) cellIdSala.getNumericCellValue();

                    // Obtener Fecha Inicio
                    Cell cellFechaInicio = fila.getCell(1);
                    Date fechaInicio = extraerFechaDesdeCelda(cellFechaInicio);

                    // Obtener Fecha Fin
                    Cell cellFechaFin = fila.getCell(2);
                    Date fechaFin = extraerFechaDesdeCelda(cellFechaFin);

                    // Obtener Observaciones
                    Cell cellObservaciones = fila.getCell(3);
                    String observaciones = (cellObservaciones != null) ? obtenerValorCeldaComoTexto(cellObservaciones) : "";

                    // Crear objeto y agregar a la lista
                    int idSolicitudS = 0; // Valor por defecto si no viene desde Excel
                    SalaPrestada sala = new SalaPrestada(idSolicitudS, idSala, fechaInicio, fechaFin, observaciones);

                } catch (Exception ex) {
                    System.err.println("⚠️ Error en fila " + fila.getRowNum() + ": " + ex.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Error al leer archivo Excel: " + e.getMessage());
            e.printStackTrace();
        }

        return listaSalas;
    }

    // ✅ Extrae una fecha desde una celda (tipo Date o String)
    private static Date extraerFechaDesdeCelda(Cell celda) {
        if (celda == null) throw new IllegalArgumentException("Celda de fecha vacía");

        if (celda.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(celda)) {
            java.util.Date utilDate = celda.getDateCellValue();
            return new Date(utilDate.getTime());
        } else if (celda.getCellType() == CellType.STRING) {
            return Date.valueOf(celda.getStringCellValue().trim());
        } else {
            throw new IllegalArgumentException("Tipo de celda de fecha inválido");
        }
    }

    // ✅ Convierte cualquier celda a texto
    private static String obtenerValorCeldaComoTexto(Cell celda) {
        return switch (celda.getCellType()) {
            case STRING -> celda.getStringCellValue();
            case NUMERIC -> String.valueOf(celda.getNumericCellValue());
            case BOOLEAN -> String.valueOf(celda.getBooleanCellValue());
            case FORMULA -> celda.getCellFormula();
            default -> "";
        };
    }
}

