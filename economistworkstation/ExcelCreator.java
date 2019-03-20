/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economistworkstation;

import economistworkstation.Entity.Month;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author fnajer
 */
public class ExcelCreator {
    public static void printAccountPayment(Month month) throws IOException {
       
        File file = new File("C:\\Users\\fnajer\\Desktop\\workbook.xls");

        HSSFWorkbook workbook;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            workbook = new HSSFWorkbook(inputStream);
            
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        CellType cellType = cell.getCellType();
                        
                        if (cellType == STRING) {
                            String cellString = cell.getStringCellValue();
                            
                            Pattern pattern = Pattern.compile("<\\w+>");
                            Matcher matcher = pattern.matcher(cellString);
                            while(matcher.find()) {
                                String foundedTag = matcher.group();
                                System.out.println(foundedTag);
                                if ("<date>".equals(foundedTag)) {
                                    LocalDate date = LocalDate.now();
                                    String resultString = cellString.replaceAll(foundedTag, date.toString());
                                    cell.setCellValue(resultString);
                                    cellString = resultString;
                                }
                                if ("<numAcc>".equals(foundedTag)) {
                                    String resultString = cellString.replaceAll(foundedTag, "acc");
                                    cell.setCellValue(resultString);
                                    cellString = resultString;
                                } 
                            }
                        }
                    }
                }
            }
//            HSSFSheet sheet = workbook.getSheetAt(0);
//            HSSFCell cell = sheet.getRow(1).getCell(2);
//            cell.setCellValue(cell.getNumericCellValue() * 2);
//            cell = sheet.getRow(2).getCell(2);
//            cell.setCellValue(cell.getNumericCellValue() * 2);
//            cell = sheet.getRow(3).getCell(2);
//            cell.setCellValue(cell.getNumericCellValue() * 2);
        }

        try (OutputStream out = new FileOutputStream("C:\\Users\\fnajer\\Desktop\\workbookNew.xls")) {
            workbook.write(out);
        }
        System.out.println("Обновлен документ 'Счет для оплаты'");
        
        
//        Workbook wb = new HSSFWorkbook ();
    
        //Sheet sheet = wb.createSheet("Счёт");
        
//        // Создать строку и поместить в нее несколько ячеек. Строки основаны на 0.
//        Row row = sheet.createRow(0);
//        // Создаем ячейку и помещаем в нее значение.
//        Cell cell = row.createCell(0);
//        cell.setCellValue(1);
//
//        // Или сделать это в одной строке.
//        row.createCell(2).setCellValue(1.2);
//        row.createCell(3).setCellValue(true);
        
//        try (OutputStream fileOut = new FileOutputStream ("C:\\Users\\fnajer\\Desktop\\workbook.xls")) {
//            wb.write (fileOut);
//            System.out.println("Создан документ 'Счет для оплаты'");
//        }
//        wb.close ();
    }
}