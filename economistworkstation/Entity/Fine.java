/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economistworkstation.Entity;

import economistworkstation.Database;
import static economistworkstation.Util.Util.isExist;
import static economistworkstation.Util.Util.parseField;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author fnajer
 */

public class Fine extends Payment {
    private final ObjectProperty<Double> costFine;
    
    public Fine() {
        this(null, null, null, null);
    }
    
    public Fine(Object paid, String datePaid, Object costFine, Balance balance) {
        super(paid, datePaid, balance);
        this.costFine = new SimpleObjectProperty(costFine);
    }
    
    public Double getFine() {
        return costFine.get();
    }
    public void setFine(Double costFine) {
        this.costFine.set(costFine);
    }
    
    @Override
    public PreparedStatement getInsertStatement(Database db) throws SQLException {
        PreparedStatement ps = db.conn.prepareStatement("INSERT INTO FINE "
                + "(id_fine, paid_fine, date_paid_fine, fine) "
                + "VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, getId());
        ps.setObject(2, getPaid());
        ps.setString(3, getDatePaid());
        ps.setObject(4, getFine());

        return ps;
    }
    @Override
    public PreparedStatement getUpdateStatement(Database db) throws SQLException {
        PreparedStatement ps = db.conn.prepareStatement("UPDATE FINE "
                + "SET paid_fine=?, date_paid_fine=?, fine=? "
                + "WHERE id_fine=?", Statement.RETURN_GENERATED_KEYS);
        ps.setObject(1, getPaid());
        ps.setString(2, getDatePaid());
        ps.setObject(3, getFine());
        ps.setInt(4, getId());
        
        return ps;
    }
    @Override
    public PreparedStatement getDeleteStatement(Database db) throws SQLException {
        PreparedStatement ps = db.conn.prepareStatement("DELETE FROM FINE "
                + "WHERE id_fine=?");
        ps.setInt(1, getId());
        
        return ps;
    }
    
    @Override
    public Double sumToPay() {
        return safeGetSum(getFine());
    }
    
    @Override
    public boolean isEmpty() {
        return getFine() == null
                && getPaid() == null
                && getDatePaid() == null;
    }
    
    @Override
    public Fine copy() {
        Fine fine = new Fine(getPaid(), getDatePaid(), getFine(),
            getBalance().copy());
        fine.setId(getId());
        return fine;
    }
    
    @Override
    public String toString() {
        return String.format("Платеж на пени. id = %d", this.getId());
    }
}
