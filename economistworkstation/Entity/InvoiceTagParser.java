/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economistworkstation.Entity;

import economistworkstation.ContractData;
import economistworkstation.Util.Money;
import java.time.LocalDate;
import java.util.Locale;

/**
 *
 * @author fnajer
 */
public class InvoiceTagParser extends Parser {
    private double sumForWords;

    public InvoiceTagParser(ContractData data) {
        super(data);
        sumForWords = 0.0;
    }
    
    @Override
    protected String parse(String foundedTag) {
        String newValue = "<Tag not founded>";
        
        //common tags
        if ("<date>".equals(foundedTag)) {
            newValue = formatDate(LocalDate.now());
        }
        if ("<numRentAcc>".equals(foundedTag)) {
            newValue = Integer.toString(period.getNumberRentAcc());
        }
        if ("<numServicesAcc>".equals(foundedTag)) {
            newValue = Integer.toString(period.getNumberServicesAcc());
        }
        if ("<subject>".equals(foundedTag)) {
            newValue = renter.getSubject();
        }
        if ("<fullName>".equals(foundedTag)) {
            newValue = renter.getFullName();
        }
        if ("<numContract>".equals(foundedTag)) {
            newValue = Integer.toString(period.getIdContract());
        }
        if ("<dateStartContract>".equals(foundedTag)) {
            LocalDate date = LocalDate.parse(contract.getDateStart());
            newValue = formatDate(date);
        }
        if ("<square>".equals(foundedTag)) {
            newValue = safeDecFormat(building.getSquare(), Locale.getDefault());
        }
        if ("<monthNameAndYear>".equals(foundedTag)) {
            LocalDate date = LocalDate.parse(period.getEndPeriod());
            int monthNum = date.getMonth().minus(1).getValue();
            String monthName = period.getMonthName(monthNum, false);
            int monthYear = date.getYear();
            if (monthNum == 12) {
                monthYear--;
            }
            newValue = monthName + ' ' + Integer.toString(monthYear);
        }
        if ("<sumInWords>".equals(foundedTag)) {
            newValue = Money.digits2Text(sumForWords);
        }
        //rental account
        if ("<rent>".equals(foundedTag)) {
            double result = getDoubleValue(rent.sumToPay());

            cell.setCellValue(result);
            sumForWords += result;
            return null;
        }
        if ("<fine>".equals(foundedTag)) {
            double result = getDoubleValue(fine.getFine());
            
            if (rowWillClear(result, cell))
                return null;

            cell.setCellValue(result);
            sumForWords += result;
            return null;
        }
        if ("<taxLand>".equals(foundedTag)) {
            double result = getDoubleValue(taxLand.getTaxLand());
            
            if (rowWillClear(result, cell))
                return null;

            cell.setCellValue(result);
            sumForWords += result;
            return null;
        }
        if ("<equipment>".equals(foundedTag)) {
            double result = getDoubleValue(equipment.getCostEquipment());
            
            if (rowWillDelete(result, cell))
                return null;

            cell.setCellValue(result);
            sumForWords += result;
            return null;
        }
        if ("<sumRentAcc>".equals(foundedTag)) {
            int firstCell = cell.getRowIndex() - 3;
            int lastCell = cell.getRowIndex();
            String columnLetter = getColumnLetter(cell);
            cell.setCellFormula("SUM(" + columnLetter + firstCell
                    + ":" + columnLetter + lastCell + ")");
            return null;
        }
        //services account
        if ("<sumServicesAcc>".equals(foundedTag)) {
            int firstCell = cell.getRowIndex() - 5;
            int lastCell = cell.getRowIndex();
            String columnLetter = getColumnLetter(cell);
            cell.setCellFormula("SUM(" + columnLetter + firstCell
                    + ":" + columnLetter + lastCell + ")");
            return null;
        }
        if ("<costWater>".equals(foundedTag)) {
            double result = getDoubleValue(services.calcCostWater());

            if (rowWillDelete(result, cell))
                return null;

            cell.setCellValue(result);

            sumForWords += result;
            return null;
        }
        if ("<costElectricity>".equals(foundedTag)) {
            double result = getDoubleValue(services.calcCostElectricity());

            if (rowWillDelete(result, cell))
                return null; 

            cell.setCellValue(result);

            sumForWords += result;
            return null;
        }
        if ("<costHeading>".equals(foundedTag)) {
            double result = getDoubleValue(services.calcCostHeading());
            
            if (rowWillDelete(result, cell))
                return null;

            cell.setCellValue(result);

            sumForWords += result;
            return null;
        }
        if ("<costGarbage>".equals(foundedTag)) {
            double result = getDoubleValue(services.calcCostGarbage());
            
            if (rowWillDelete(result, cell))
                return null;

            cell.setCellValue(result);

            sumForWords += result;
            return null;
        }
        if ("<costInternet>".equals(foundedTag)) {
            double result = getDoubleValue(services.calcCostInternet());
            
            if (rowWillDelete(result, cell))
                return null;

            cell.setCellValue(result);

            sumForWords += result;
            return null;
        }
        if ("<costTelephone>".equals(foundedTag)) {
            double result = getDoubleValue(services.calcCostTelephone());
           
            if (rowWillDelete(result, cell))
                return null;

            cell.setCellValue(result);

            sumForWords += result;
            return null;
        }
        return newValue;
    }
}
