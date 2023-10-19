package com.setianjay.database.excel.data;

import com.setianjay.database.entity.Employee;
import com.setianjay.database.enums.ExcelType;
import com.setianjay.database.enums.Gender;
import com.setianjay.database.excel.base.ExcelWorkbook;
import com.setianjay.database.util.MapUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EmployeeWorkbook extends ExcelWorkbook<Employee> {

    public EmployeeWorkbook(ExcelType excelType, InputStream excelFileInputStream) throws IOException {
        super(excelType, excelFileInputStream);
    }

    @Override
    public List<Employee> readDataInSingleSheet() {
        Sheet excelSheet = this.workbook.getSheetAt(0);
        int firstRow = excelSheet.getFirstRowNum();
        int lastRow = excelSheet.getLastRowNum();
        List<Employee> employees = new ArrayList<>();

        for (int rowIndex = firstRow + 1; rowIndex < lastRow; rowIndex++) {
            Row row = excelSheet.getRow(rowIndex);
            Employee employee = new Employee();
            this.mapRowToCell(row, employee);

            if (employee.getId() != null) {
                employees.add(employee);
            }
        }

        return employees;
    }

    @Override
    protected void mapCellToData(Employee employee, int cellIndex, Supplier<String> cellData) {
        if (cellData != null) {
            switch (cellIndex) {
                case 0 -> employee.setId(cellData.get());
                case 1 -> employee.setFullName(cellData.get());
                case 2 -> employee.setJobTitle(cellData.get());
                case 3 -> employee.setDepartment(cellData.get());
                case 4 -> employee.setBusinessUnit(cellData.get());
                case 5 -> employee.setGender(Gender.mapStringToGender(cellData.get()));
                case 6 -> employee.setEthnicity(cellData.get());
                case 7 -> employee.setAge(MapUtil.mapDoubleToInt(cellData.get()));
                case 8 -> employee.setHireDate(cellData.get());
                case 9 -> employee.setAnnualSalary(MapUtil.mapDoubleToInt(cellData.get()));
                case 10 -> employee.setBonus(cellData.get());
                case 11 -> employee.setCountry(cellData.get());
                case 12 -> employee.setCity(cellData.get());
                case 13 -> employee.setExitDate(cellData.get());
            }
        }
    }


}
