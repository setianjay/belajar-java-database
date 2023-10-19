package com.setianjay.database.excel.base;

import com.setianjay.database.enums.ExcelType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

public abstract class ExcelWorkbook<T> {
    protected final Workbook workbook;

    protected ExcelWorkbook(ExcelType excelType, InputStream excelFileInputStream) throws IOException {
        this.workbook = createWorkbook(excelType, excelFileInputStream);
    }

    private boolean isExtensionXlsx(ExcelType excelType) {
        return excelType == ExcelType.xlsx;
    }

    private Workbook createWorkbook(ExcelType excelType, InputStream inputStream) throws IOException {
        return isExtensionXlsx(excelType) ? new XSSFWorkbook(inputStream)
                : new HSSFWorkbook(inputStream);
    }

    protected CellType getCellType(Cell cell) {
        return cell.getCellType().equals(CellType.FORMULA) ? cell.getCachedFormulaResultType() :
                cell.getCellType();
    }

    private void mapStringCell(T data, int cellIndex, Cell cell) {
        Supplier<String> cellData = cell::getStringCellValue;
        mapCellToData(data, cellIndex, cellData);
    }

    private void mapNumericCell(T data, int cellIndex, Cell cell) {
        Supplier<String> cellData = null;

        if (DateUtil.isCellDateFormatted(cell)) {
            // get string date and format to default format sql for date
            Date cellValueDate = cell.getDateCellValue();
            if (cellValueDate != null) {
                SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                cellData = () -> defaultDateFormat.format(cellValueDate);
            }
        } else {
            cellData = () -> Double.toString(cell.getNumericCellValue());
        }

        if (cellData != null){
            mapCellToData(data, cellIndex, cellData);
        }
    }

    protected void mapRowToCell (Row row, T data) {
        for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
            Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellType cellType = getCellType(cell);

            if (cellType.equals(CellType.STRING)) {
                mapStringCell(data, cellIndex, cell);
            }

            if (cellType.equals(CellType.NUMERIC)) {
                mapNumericCell(data, cellIndex, cell);
            }
        }
    }

    public abstract List<T> readDataInSingleSheet();

    protected abstract void mapCellToData(T data, int cellIndex, Supplier<String> cellData);
}
