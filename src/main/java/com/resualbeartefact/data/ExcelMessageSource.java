package com.resualbeartefact.data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ExcelMessageSource {
    private MultipartFile excelFile;
    public ExcelMessageSource(MultipartFile excelFile) {
        this.excelFile = excelFile;
    }
    public List<Map<String, String>> getMessages() throws Exception {
        List<Map<String, String>> excelData = new ArrayList<>();
        if (excelFile != null && !excelFile.isEmpty()) {
            try (InputStream is = excelFile.getInputStream();
                 Workbook workbook = new XSSFWorkbook(is)) {
                Sheet sheet = workbook.getSheetAt(0);

                Row headerRow = sheet.getRow(0);
                List<String> headers = new ArrayList<>();
                for (Cell cell : headerRow) {
                    headers.add(cell.getStringCellValue());
                }

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    Map<String, String> rowData = new LinkedHashMap<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j);
                        String cellValue = "";
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case STRING:
                                    cellValue = cell.getStringCellValue();
                                    break;
                                case NUMERIC:
                                    if (DateUtil.isCellDateFormatted(cell)) {
                                        cellValue = cell.getDateCellValue().toString();
                                    } else {
                                        cellValue = Double.toString(cell.getNumericCellValue());
                                    }
                                    break;
                                case BOOLEAN:
                                    cellValue = Boolean.toString(cell.getBooleanCellValue());
                                    break;
                                default:
                                    cellValue = "";
                            }
                        }
                        rowData.put(headers.get(j), cellValue);
                    }
                    excelData.add(rowData);
                }
                System.out.println("Excel Data: " + excelData);
            } catch (IOException e) {
//                return ResponseEntity.badRequest().body("Invalid Excel file");
            }
        }

        return excelData;
    }
}

