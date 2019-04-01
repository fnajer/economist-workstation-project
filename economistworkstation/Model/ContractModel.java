/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economistworkstation.Model;

import economistworkstation.Database;
import economistworkstation.EconomistWorkstation;
import economistworkstation.Entity.Contract;
import economistworkstation.Entity.Month;
import economistworkstation.Entity.Renter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author fnajer
 */
public class ContractModel {
    private static Database db = Database.getInstance();
 
    public static int addContract(Contract contract) {
        int idContract = -1;
        
        try {
            System.out.println("Добавлено: " + contract.getDateStart());
            PreparedStatement ps = db.conn.prepareStatement("insert into CONTRACT values(NULL,?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, contract.getDateStart());
            ps.setString(2, contract.getDateEnd());
            ps.setInt(3, contract.getIdRenter());
            ps.setInt(4, contract.getIdBuilding());
            
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
        
            if (rs.next()) {
                idContract = rs.getInt(1);
                contract.setId(idContract);
                System.out.println("Добавлено: " + idContract);
                
                createMonths(contract);
                System.out.println("Месяцы добавлены. Id контракта: " + idContract);
            } else {
                System.out.println("Error upon creating contract. Id didn got");
            }
        } catch (SQLException ex) {
            Logger.getLogger(EconomistWorkstation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return idContract;
    }
    
    public static void updateContract(int id, Contract contract, boolean extend) {
        try {
            
            PreparedStatement ps = db.conn.prepareStatement("UPDATE CONTRACT\n" +
                            "SET date_start=?, date_end=?, id_renter=?, id_building=?\n" +
                            "WHERE id=?;");
            ps.setString(1, contract.getDateStart());
            ps.setString(2, contract.getDateEnd());
            ps.setInt(3, contract.getIdRenter());
            ps.setInt(4, contract.getIdBuilding());
            ps.setInt(5, id);
            
            ps.executeUpdate();
            System.out.println("Изменено: " + contract.getId());
            
            if (!extend) {
                MonthModel.deleteMonths(id);
                createMonths(contract);
                System.out.println("Месяцы добавлены. Id контракта: " + contract.getId());
            }
        } catch (SQLException ex) {
            Logger.getLogger(EconomistWorkstation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void createMonths(Contract contract) throws SQLException {
        LocalDate date_start = LocalDate.parse(contract.getDateStart());
        LocalDate date_end = LocalDate.parse(contract.getDateEnd());
        long diffOfDates = ChronoUnit.MONTHS.between(date_start, date_end);
        
        int dayOfMonth = date_start.getDayOfMonth();
        if (dayOfMonth == 1) {
            dayOfMonth = 0;
        } else {
            dayOfMonth--;
            diffOfDates++; //т.к. периодов оплаты больше, чем месяцев
        }

        date_start = date_start.minusDays(dayOfMonth).plusMonths(1);
        for(int i = 1; i <= diffOfDates; i++) {
            Month newMonth = new Month();
            newMonth.setNumber(i);
            newMonth.setDate(date_start.toString());
            newMonth.setIdContract(contract.getId());
            MonthModel.addMonth(newMonth);

            if(i == diffOfDates - 1 && dayOfMonth > 1) {
                date_start = date_start.plusDays(dayOfMonth);
                continue;
            }
            date_start = date_start.plusMonths(1);
        }
    }
    
    public static void deleteContract(int id) {
        try {
            MonthModel.deleteMonths(id);
            db.stmt.executeUpdate("DELETE FROM CONTRACT WHERE id='" + id + "'");
            System.out.println("Удалено: " + id);
        } catch (SQLException ex) {
            Logger.getLogger(EconomistWorkstation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static ObservableList<Contract> getContracts() {
        ObservableList contracts = FXCollections.observableArrayList();
        try {
            ResultSet rs = db.stmt.executeQuery("SELECT * FROM CONTRACT "
                    + "LEFT JOIN RENTER ON CONTRACT.id_renter=RENTER.id");
            
            while (rs.next()) {
                contracts.add(createObjectContract(rs));
            }
            
            System.out.println("Извлечение договоров завершено.");
            
        } catch (SQLException ex) {
            Logger.getLogger(EconomistWorkstation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return contracts;
    }
    
    public static Contract getContract(int id) {
        Contract contract = null;
        try {
            //ResultSet rs = db.stmt.executeQuery("SELECT * FROM CONTRACT WHERE id='" + id + "'");
            ResultSet rs = db.stmt.executeQuery("SELECT * FROM CONTRACT,RENTER, BUILDING WHERE CONTRACT.id=" + id + " AND CONTRACT.id_renter=RENTER.id AND CONTRACT.id_building=BUILDING.id;");
    
            if (rs.next()) {
                contract = createObjectContract(rs);
            }
            
            System.out.println("Извлечение договора завершено.");
            
        } catch (SQLException ex) {
            Logger.getLogger(EconomistWorkstation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return contract;
    }
    
    private static Contract createObjectContract(ResultSet rs) throws SQLException {
        Contract contract = new Contract(rs.getString("date_start"), rs.getString("date_end"), rs.getInt("id_renter"), 
        rs.getInt("id_building"));
        contract.setId(rs.getInt("id"));
        
        String checkString = rs.getString("subject");
        if (checkString != null) {
            Renter renter = RenterModel.createObjectRenter(rs);
            contract.setRenter(renter);
        }
        
        return contract;
    }
}
