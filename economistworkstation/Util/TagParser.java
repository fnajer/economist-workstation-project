/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economistworkstation.Util;

import economistworkstation.ContractData;
import economistworkstation.Entity.Building;
import economistworkstation.Entity.Contract;
import economistworkstation.Entity.Month;
import economistworkstation.Entity.Renter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 *
 * @author fnajer
 */
public class TagParser {
    
    public static void convertTags (ContractData data) {
        Cell cell = data.getCell();
        Month month = data.getMonth();
        Renter renter = data.getRenter();
        Building building = data.getBuilding();
        Contract contract = data.getContract();
        String cellString = cell.getStringCellValue();
                            
        Pattern pattern = Pattern.compile("<\\w+>");
        Matcher matcher = pattern.matcher(cellString);
        while(matcher.find()) {
            String resultString;
            String newValue = "<Tag not founded>";
            String foundedTag = matcher.group();
            System.out.println(foundedTag);
            
            if ("<date>".equals(foundedTag)) {
                LocalDate date = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                String formattedString = date.format(formatter);
                newValue = formattedString;
            }
            if ("<numRentAcc>".equals(foundedTag)) {
                newValue = Integer.toString(month.getNumberRentAcc());
            }
            if ("<numCommunalAcc>".equals(foundedTag)) {
                newValue = Integer.toString(month.getNumberCommunalAcc());
            }
            if ("<subject>".equals(foundedTag)) {
                newValue = renter.getSubject();
            }
            if ("<fullName>".equals(foundedTag)) {
                newValue = renter.getFullName();
            }
            if ("<numContract>".equals(foundedTag)) {
                newValue = Integer.toString(contract.getId());
            }
            if ("<dateStartContract>".equals(foundedTag)) {
                LocalDate date = LocalDate.parse(contract.getDateStart());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                String formattedString = date.format(formatter);
                newValue = formattedString;
            }
            if ("<square>".equals(foundedTag)) {
                newValue = Double.toString(building.getSquare());
            }
            if ("<monthName>".equals(foundedTag)) {
                LocalDate date = LocalDate.parse(month.getDate());
                int monthNum = date.getMonth().minus(1).getValue();
                String monthName = Month.getMonthName(monthNum);
                int monthYear = date.getYear();
                if (monthNum == 12) {
                    monthYear--;
                }
                newValue = monthName + ' ' + Integer.toString(monthYear);
            }
            if ("<sumRent>".equals(foundedTag)) {
                String sumRent = Double.toString(Month.calcSumRent(month));
                newValue = sumRent;
                
                resultString = cellString.replaceAll(foundedTag, newValue);
                cell.setCellValue(Double.parseDouble(resultString));
                return;
            }
            if ("<sumInWords>".equals(foundedTag)) {
                String sumInWords = Money.digits2Text(145.00);
                newValue = sumInWords;
            }
            
            resultString = cellString.replaceAll(foundedTag, newValue);
            cell.setCellValue(resultString);
            cellString = resultString;
        }
    }
}
