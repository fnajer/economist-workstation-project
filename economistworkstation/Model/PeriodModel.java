/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economistworkstation.Model;

import economistworkstation.ContractData;
import economistworkstation.Controller.ContractController;
import economistworkstation.Database;
import economistworkstation.EconomistWorkstation;
import economistworkstation.Entity.BalanceTable;
import economistworkstation.Entity.ExtraCost;
import economistworkstation.Entity.Fine;
import economistworkstation.Entity.Payment;
import economistworkstation.Entity.Period;
import economistworkstation.Entity.Rent;
import economistworkstation.Entity.Equipment;
import economistworkstation.Entity.Services;
import economistworkstation.Entity.TaxLand;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
        
/**
 *
 * @author fnajer
 */
public class PeriodModel {
    private static Database db = Database.getInstance();
 
    public static void addPeriod(Period period) {
        try {
            PreparedStatement ps = db.conn.prepareStatement("INSERT INTO PERIOD "
                    + "(id, number, date_end, id_contract) "
                    + "VALUES(NULL,?, ?, ?)");
            ps.setInt(1, period.getNumber());
            ps.setString(2, period.getEndPeriod());
            ps.setInt(3, period.getIdContract());
            
            ps.executeUpdate();
            System.out.println("Добавлен период: " + period.getNumber());
        } catch (SQLException ex) {
            Logger.getLogger(EconomistWorkstation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void addPeriods(int id, int count, Period lastPeriod) {
        LocalDate date = LocalDate.parse(lastPeriod.getEndPeriod());
        int daysAfterCalends = date.getDayOfMonth();
        if (daysAfterCalends > 1) {
            daysAfterCalends--;
            date = date.minusDays(daysAfterCalends);  
            date = date.plusMonths(1);
        } else {
            daysAfterCalends = 0;
        }
        
        lastPeriod.setEndPeriod(date.toString());
        updatePeriod(lastPeriod.getId(), lastPeriod);
          
        if(count == 1 && daysAfterCalends > 1) {
            date = date.plusDays(daysAfterCalends);
        } else {
            date = date.plusMonths(1);
        }
        
        for(int i = 1; i <= count; i++) {
            Period newPeriod = new Period();
            newPeriod.setNumber(lastPeriod.getNumber() + i);
            newPeriod.setEndPeriod(date.toString());
            newPeriod.setIdContract(id);
            PeriodModel.addPeriod(newPeriod);

            if(i == count - 1 && daysAfterCalends > 1) {
                date = date.plusDays(daysAfterCalends);
                continue;
            }
            
            date = date.plusMonths(1);
        }

        System.out.println("Осуществлено продление аренды.");
    }
    
    public static void updatePeriod(int id, Period period) {
        try {
            Integer idRent = addPayment(period.getRentPayment(), id);
            if (idRent == null) period.setRentPayment(null);
            Integer idFine = addPayment(period.getFinePayment(), id);
            if (idFine == null) period.setFinePayment(null);
            Integer idTaxLand = addPayment(period.getTaxLandPayment(), id);
            if (idTaxLand == null) period.setTaxLandPayment(null);
            Integer idServices = addPayment(period.getServicesPayment(), id);
            if (idServices == null) period.setServicesPayment(null);
            Integer idEquipment = addPayment(period.getEquipmentPayment(), id);
            if (idEquipment == null) period.setEquipmentPayment(null);
            
            PreparedStatement ps = db.conn.prepareStatement("UPDATE PERIOD "
                    + "SET number=?, date_end=?, id_contract=? "
                    + "WHERE id=?");
                    
            ps.setInt(1, period.getNumber());
            ps.setString(2, period.getEndPeriod());
            ps.setInt(3, period.getIdContract());
            ps.setInt(4, id);
            
            ps.executeUpdate();
            System.out.println("Изменен период: " + period.getNumber());
        } catch (SQLException ex) {
            Logger.getLogger(EconomistWorkstation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    public static Integer addPayment(Payment payment, int id) {
        if (payment == null) return null;
        
        PreparedStatement ps;
        String state;
        try {
            if (payment.getPaid() != null && payment.getPaid() == -1.0) {
                payment.setId(id);
                ps = payment.getDeleteStatement(db);
                state = "Delete";
                ps.executeUpdate();
                System.out.println(String.format("%s: %s", state, payment));
                return null;
            } else if (payment.getId() == 0) {
                payment.setId(id);
                ps = payment.getInsertStatement(db);
                state = "Insert";
            } else {
                ps = payment.getUpdateStatement(db);
                state = "Update";
            }
     
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            
            if (rs.next()) {
                payment.setId(rs.getInt(1));
            }
            
            System.out.println(String.format("%s: %s", state, payment));
            
        } catch (SQLException ex) {
            Logger.getLogger(PeriodModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return payment.getId();
    }
    
    public static void deletePeriods(int id) {
        try {
            db.stmt.executeUpdate("DELETE FROM PERIOD WHERE id_contract='" + id + "'");
            System.out.println("Удалены месяцы для договора: " + id);
        } catch (SQLException ex) {
            Logger.getLogger(EconomistWorkstation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static ObservableList<Period> getPeriods(int id) {
        ObservableList months = FXCollections.observableArrayList();
        try {
            ResultSet rs = db.stmt.executeQuery("SELECT * FROM PERIOD "
                    + "LEFT JOIN RENT ON PERIOD.id=RENT.id_rent "
                    + "LEFT JOIN FINE ON PERIOD.id=FINE.id_fine "
                    + "LEFT JOIN TAXLAND ON PERIOD.id=TAXLAND.id_tax_land "
                    + "LEFT JOIN EQUIPMENT ON PERIOD.id=EQUIPMENT.id_equipment "
                    + "LEFT JOIN SERVICES ON PERIOD.id=SERVICES.id_services "
                    + "LEFT JOIN EXTRACOST ON PERIOD.id=EXTRACOST.id_extra_cost "
                    + "LEFT JOIN BALANCE ON PERIOD.id=BALANCE.id_balance "
                    + "WHERE id_contract='"
                    + id + "' ORDER BY number");
            
            while (rs.next()) {
                months.add(createObjectPeriod(rs));
            }
            
            System.out.println("Извлечение месяцев завершено.");
            
        } catch (SQLException ex) {
            Logger.getLogger(EconomistWorkstation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return months;
    }
    
    public static ObservableList<ContractData> getContractData(LocalDate month) {
        ObservableList contractsData = FXCollections.observableArrayList();
        LocalDate nextMonth = month.plusMonths(1);
        
        try {
            ResultSet rs = db.stmt.executeQuery("SELECT * FROM PERIOD "
                    + "LEFT JOIN RENT ON PERIOD.id=RENT.id_rent "
                    + "LEFT JOIN FINE ON PERIOD.id=FINE.id_fine "
                    + "LEFT JOIN TAXLAND ON PERIOD.id=TAXLAND.id_tax_land "
                    + "LEFT JOIN EQUIPMENT ON PERIOD.id=EQUIPMENT.id_equipment "
                    + "LEFT JOIN SERVICES ON PERIOD.id=SERVICES.id_services "
                    + "LEFT JOIN EXTRACOST ON PERIOD.id=EXTRACOST.id_extra_cost "
                    + "LEFT JOIN BALANCE ON PERIOD.id=BALANCE.id_balance "
                    + "LEFT JOIN CONTRACT ON PERIOD.ID_CONTRACT=CONTRACT.id "
                    + "LEFT JOIN RENTER ON CONTRACT.ID_RENTER=RENTER.id "
                    + "LEFT JOIN BUILDING ON CONTRACT.ID_BUILDING=BUILDING.id "
                    + "WHERE date_end >= '" + month + "' AND date_end < '" + nextMonth + "' "
                    + "ORDER BY id_contract");
            
            while (rs.next()) {
                contractsData.add(new ContractData(null, createObjectPeriod(rs),
                        BuildingModel.createObjectBuilding(rs),
                        RenterModel.createObjectRenter(rs), 
                        ContractModel.createObjectContract(rs)));
            }
            
            System.out.println("Извлечение целых контрактов завершено.");
            
        } catch (SQLException ex) {
            Logger.getLogger(EconomistWorkstation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return contractsData;
    }
    public static ObservableList<ContractData> getContractData(LocalDate dateStart, LocalDate dateEnd) {
        ObservableList contractsData = FXCollections.observableArrayList();
        
        try {
            ResultSet rs = db.stmt.executeQuery("select CONTRACT.id, CONTRACT.date_start, num, PERIOD.date_end,"
             + "group_concat(cost) as cost, group_concat(index_inflation) as index_cost, SUM(fine) as fine, SUM(fine) as fine, SUM(tax_land) as tax_land, SUM(cost_equipment) as cost_equipment, "
             + "SUM(cost_meter_heading) as cost_meter_heading, SUM(cost_meter_garbage) as cost_meter_garbage, SUM(cost_internet) as cost_internet, SUM(cost_telephone) as cost_telephone, "
             + "group_concat(count_water) as count_water, group_concat(tariff_water) as tariff_water, group_concat(count_electricity) as count_electricity, group_concat(tariff_electricity) as tariff_electricity,"
             + "SUM(paid_rent) as paid_rent, SUM(paid_fine) as paid_fine, SUM(paid_tax_land) as paid_tax_land, SUM(paid_equipment) as paid_equipment, SUM(paid_services) as paid_services "
             + "from CONTRACT "
             + "LEFT JOIN PERIOD ON PERIOD.id_contract=CONTRACT.id "
             + "LEFT JOIN RENT ON PERIOD.ID=RENT.id_rent "
             + "LEFT JOIN FINE ON PERIOD.id=FINE.id_fine "
             + "LEFT JOIN TAXLAND ON PERIOD.id=TAXLAND.id_tax_land "
             + "LEFT JOIN EQUIPMENT ON PERIOD.id=EQUIPMENT.id_equipment "
             + "LEFT JOIN SERVICES ON PERIOD.id=SERVICES.id_services "
             + "WHERE date_end > '" + dateStart + "' AND date_end <= '" + dateEnd + "' "
             + "GROUP BY CONTRACT.id, PERIOD.id "
             + "ORDER BY id_contract");
            
            while (rs.next()) {
                contractsData.add(new ContractData(null, createObjectPeriod(rs),
                        BuildingModel.createObjectBuilding(rs),
                        RenterModel.createObjectRenter(rs), 
                        ContractModel.createObjectContract(rs)));
            }
            
            System.out.println("Извлечение целых контрактов завершено.");
            
        } catch (SQLException ex) {
            Logger.getLogger(EconomistWorkstation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return contractsData;
    }
    
    public static void updateAccountNumbers() {
        try {
            LocalDate currentYear = LocalDate.now().with(firstDayOfYear());
            LocalDate nextYear = currentYear.plusYears(1);
            
            ResultSet rs = db.stmt.executeQuery("SELECT id \n" +
                "FROM PERIOD\n" +
                "WHERE date >= '" + currentYear + "' AND date < '" + nextYear + "' \n" +
                "ORDER BY date, id_contract;");
            int number = 1;
            int idPeriod;
            
            while (rs.next()) {
                idPeriod = rs.getInt("id");
                PreparedStatement ps = db.conn.prepareStatement("UPDATE PERIOD\n" +
                            "SET number_rent_acc=?, number_services_acc=?\n" +
                            "WHERE id=?;");
                ps.setInt(1, number);
                ps.setInt(2, number + 1);
                ps.setInt(3, idPeriod);
                
                number += 2;
                ps.executeUpdate();
            }
            
            System.out.println("Обновление порядка счета месяцев завершено.");
            
        } catch (SQLException ex) {
            Logger.getLogger(EconomistWorkstation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void updateExtraCostPeriod(int id, Period period) {
        Integer idExtraCost = addExtraCost(period.getExtraCost(), id);
        if (idExtraCost == null) period.setExtraCost(null);
    }
            
    public static Integer addExtraCost(ExtraCost extraCost, int id) {
        if (extraCost == null) return null;
        
        PreparedStatement ps;
        String state;
        try {
            if (extraCost.getCostRent() != null && extraCost.getCostRent() == -1.0) {
                extraCost.setId(id);
                ps = extraCost.getDeleteStatement(db);
                state = "Delete";
                ps.executeUpdate();
                System.out.println(String.format("%s: %s", state, extraCost));
                return null;
            } else if (extraCost.getId() == 0) {
                extraCost.setId(id);
                ps = extraCost.getInsertStatement(db);
                state = "Insert";
            } else {
                ps = extraCost.getUpdateStatement(db);
                state = "Update";
            }
     
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            
            if (rs.next()) {
                extraCost.setId(rs.getInt(1));
            }
            
            System.out.println(String.format("%s: %s", state, extraCost));
            
        } catch (SQLException ex) {
            Logger.getLogger(PeriodModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return extraCost.getId();
    }
    
    public static void updateBalancePeriod(Period period) {
        Integer idBalance = addBalance(period);
        if (idBalance == null) period.setBalanceTable(new BalanceTable());
    }
            
    public static Integer addBalance(Period period) {
        BalanceTable balance = period.getBalanceTable();
        int id = period.getId();
        
        if (balance == null) return null;
        if (balance.isEmpty()) return null;
        
        PreparedStatement ps;
        String state;
        try {
            if (balance.getCreditRent() != null && balance.getCreditRent() == -1.0) {
                balance.setId(id);
                ps = balance.getDeleteStatement(db);
                state = "Delete";
                ps.executeUpdate();
                System.out.println(String.format("%s: %s", state, balance));
                return null;
            } else if (balance.getId() == 0) {
                balance.setId(id);
                ps = balance.getInsertStatement(db);
                state = "Insert";
            } else {
                ps = balance.getUpdateStatement(db);
                state = "Update";
            }
     
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            
            if (rs.next()) {
                balance.setId(rs.getInt(1));
            }
            
            System.out.println(String.format("%s: %s", state, balance));
            
        } catch (SQLException ex) {
            Logger.getLogger(PeriodModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return balance.getId();
    }
    
    private static Payment createObjectRent(ResultSet rs) throws SQLException {
        if(rs.getInt("id_rent") != 0) {
            Payment rent = new Rent(rs.getObject("paid_rent"), 
                    rs.getString("date_paid_rent"),
                    rs.getObject("cost"), 
                    rs.getObject("index_inflation"));
            rent.setId(rs.getInt("id_rent"));
            return rent;
        }
        return null;
    }
    private static Payment createObjectFine(ResultSet rs) throws SQLException {
        if(rs.getInt("id_fine") != 0) {
            Payment fine = new Fine(rs.getObject("paid_fine"), 
                    rs.getString("date_paid_tax_land"),
                    rs.getObject("fine"));
            fine.setId(rs.getInt("id_fine"));
            return fine;
        }
        return null;
    }
    private static Payment createObjectTaxLand(ResultSet rs) throws SQLException {
        if(rs.getInt("id_tax_land") != 0) {
            Payment fine = new TaxLand(rs.getObject("paid_tax_land"), 
                    rs.getString("date_paid_tax_land"),
                    rs.getObject("tax_land"));
            fine.setId(rs.getInt("id_tax_land"));
            return fine;
        }
        return null;
    }
    private static Payment createObjectEquipment(ResultSet rs) throws SQLException {
        if(rs.getInt("id_equipment") != 0) {
            Payment fine = new Equipment(rs.getObject("paid_equipment"), 
                    rs.getString("date_paid_equipment"),
                    rs.getObject("cost_equipment"));
            fine.setId(rs.getInt("id_equipment"));
            return fine;
        }
        return null;
    }
    private static Payment createObjectServices(ResultSet rs) throws SQLException {
        if(rs.getInt("id_services") != 0) {
            Payment services = new Services(rs.getObject("paid_services"), 
                    rs.getString("date_paid_services"),
                    rs.getObject("count_water"),
                    rs.getObject("count_electricity"), 
                    rs.getObject("cost_meter_heading"),
                    rs.getObject("cost_meter_garbage"), 
                    rs.getObject("cost_internet"), 
                    rs.getObject("cost_telephone"),
                    rs.getObject("tariff_water"),
                    rs.getObject("tariff_electricity"));
            services.setId(rs.getInt("id_services"));
            return services;
        }
        return null;
    }
    private static ExtraCost createObjectExtraCost(ResultSet rs) throws SQLException {
        if(rs.getInt("id_extra_cost") != 0) {
            ExtraCost extraCost = new ExtraCost(rs.getObject("extra_cost_rent"), 
                    rs.getObject("extra_cost_fine"),
                    rs.getObject("extra_cost_tax_land"),
                    rs.getObject("extra_cost_services"), 
                    rs.getObject("extra_cost_equipment"));
            extraCost.setId(rs.getInt("id_extra_cost"));
            return extraCost;
        }
        return null;
    }
    private static BalanceTable createObjectBalanceTable(ResultSet rs) throws SQLException {
        if (rs.getInt("id_balance") != 0) {
            BalanceTable balanceTable = new BalanceTable(
                    rs.getObject("credit_rent"), 
                    rs.getObject("debit_rent"),
                    rs.getObject("credit_fine"), 
                    rs.getObject("debit_fine"),
                    rs.getObject("credit_tax_land"), 
                    rs.getObject("debit_tax_land"),
                    rs.getObject("credit_services"), 
                    rs.getObject("debit_services"),
                    rs.getObject("credit_equipment"), 
                    rs.getObject("debit_equipment"));
            balanceTable.setId(rs.getInt("id_balance"));
            return balanceTable;
        }
        return null;
    }
    
    private static Period createObjectPeriod(ResultSet rs) throws SQLException {
        Period period = new Period(rs.getInt("number"), 
                    rs.getInt("number_rent_acc"),
                    rs.getInt("number_services_acc"),
                    rs.getString("date_end"),  
                    rs.getInt("id_contract"),
                    createObjectRent(rs),
                    createObjectFine(rs),
                    createObjectTaxLand(rs),
                    createObjectServices(rs),
                    createObjectEquipment(rs),
                    createObjectExtraCost(rs),
                    createObjectBalanceTable(rs));
        period.setId(rs.getInt("id"));
        
        return period;
    }
    private static Period createPartObjectPeriod(ResultSet rs) throws SQLException {
        Period period = new Period(0, 
                    0,
                    0,
                    rs.getString("date_end"),  
                    rs.getInt("id_contract"),
                    createObjectRent(rs),
                    createObjectFine(rs),
                    createObjectTaxLand(rs),
                    createObjectServices(rs),
                    createObjectEquipment(rs),
                    createObjectExtraCost(rs),
                    createObjectBalanceTable(rs));
        period.setId(rs.getInt("id"));
        
        return period;
    }

    public static void autoFill(Double value, String typeField, String typeTable,
            LocalDate dateStart, LocalDate dateEnd, int idBuilding, String idName) {
        try {
            // necessary to enter the months exactly, from 1 to 1
            // work only with created payments
            ResultSet rs = db.stmt.executeQuery(
                    "SELECT * FROM PERIOD "
                    + "LEFT JOIN RENT ON PERIOD.id=RENT.id_rent "
                    + "LEFT JOIN FINE ON PERIOD.id=FINE.id_fine "
                    + "LEFT JOIN TAXLAND ON PERIOD.id=TAXLAND.id_tax_land "
                    + "LEFT JOIN EQUIPMENT ON PERIOD.id=EQUIPMENT.id_equipment "
                    + "LEFT JOIN SERVICES ON PERIOD.id=SERVICES.id_services "
                    + "LEFT JOIN EXTRACOST ON PERIOD.id=EXTRACOST.id_extra_cost "
                    + "LEFT JOIN BALANCE ON PERIOD.id=BALANCE.id_balance "
                            
                    + "LEFT JOIN CONTRACT ON CONTRACT.id=PERIOD.id_contract "
                    + "LEFT JOIN BUILDING ON BUILDING.id=CONTRACT.id_building "
                    + "WHERE date_end > '" + dateStart + "' "
                    + "AND date_end <= '"+ dateEnd + "' "
                    + (idBuilding == -1 ? "" : "AND BUILDING.id=" + idBuilding));

            ArrayList<Period> array = new ArrayList();
            while (rs.next()) {
                array.add(createObjectPeriod(rs));
            }
            
            for (Period period : array) {
                setPayment(period.getId(), typeField, typeTable, idName, value);
                ObservableList<Period> periods = getPeriodsById(
                        period.getId(), period.getNumber(), period.getIdContract());

                ContractController controller = new ContractController();
                controller.setPeriods(periods);
                Period updatedPeriod = periods.get(0);
                BalanceTable nextBalanceTable = new BalanceTable();
                for (Payment payment : updatedPeriod.getListPayments()) {
                    if (payment == null) continue;
                    payment.calcPartOfBalance(updatedPeriod.getBalanceTable(), nextBalanceTable);
                }
                updatedPeriod.setNextBalanceTable(nextBalanceTable);
                
                controller.recalculateBalance(updatedPeriod);
            }
            
            System.out.println("Завершено авто-заполнение: " + typeField);
        } catch (SQLException ex) {
            Logger.getLogger(EconomistWorkstation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static ObservableList<Period> getPeriodsById(int id, int number, int idContract) {
        ObservableList periods = FXCollections.observableArrayList();
        try {
            ResultSet rs = db.stmt.executeQuery("SELECT * FROM PERIOD "
                    + "LEFT JOIN RENT ON PERIOD.id=RENT.id_rent "
                    + "LEFT JOIN FINE ON PERIOD.id=FINE.id_fine "
                    + "LEFT JOIN TAXLAND ON PERIOD.id=TAXLAND.id_tax_land "
                    + "LEFT JOIN EQUIPMENT ON PERIOD.id=EQUIPMENT.id_equipment "
                    + "LEFT JOIN SERVICES ON PERIOD.id=SERVICES.id_services "
                    + "LEFT JOIN EXTRACOST ON PERIOD.id=EXTRACOST.id_extra_cost "
                    + "LEFT JOIN BALANCE ON PERIOD.id=BALANCE.id_balance "
                    + "WHERE id_contract="
                    + idContract + " AND number >=" + number + " ORDER BY number");

            while (rs.next()) {
                periods.add(createObjectPeriod(rs));
            }

            System.out.println("Извлечение периодов завершено.");

        } catch (SQLException ex) {
            Logger.getLogger(EconomistWorkstation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return periods;
    }
    
    public static void setPayment(int idPayment, String typeField, String typeTable,
            String idName, Double value) { 
        try {
            PreparedStatement ps = db.conn.prepareStatement("UPDATE " + typeTable
                    + " SET " + typeField + "=? "
                    + "WHERE " + idName + "=?");
            
            ps.setObject(1, value);
            ps.setInt(2, idPayment);
            
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PeriodModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
