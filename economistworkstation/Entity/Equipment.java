/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economistworkstation.Entity;

import economistworkstation.Database;
import static economistworkstation.Util.Util.parseField;
import static economistworkstation.Util.Util.setText;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;

/**
 *
 * @author fnajer
 */

public class Equipment extends Payment {
    private final ObjectProperty<Double> costEquipment;
    
    public Equipment() {
        this(null, null, null);
    }
    
    public Equipment(Object paid, String datePaid, Object costEquipment) {
        super(paid, datePaid);
        this.costEquipment = new SimpleObjectProperty(costEquipment);
    }
    
    public Double getCostEquipment() {
        return costEquipment.get();
    }
    public void setCostEquipment(Double costEquipment) {
        this.costEquipment.set(costEquipment);
    }
    
    @Override
    public PreparedStatement getInsertStatement(Database db) throws SQLException {
        PreparedStatement ps = db.conn.prepareStatement("INSERT INTO EQUIPMENT "
                + "(id_equipment, paid_equipment, date_paid_equipment, cost_equipment) "
                + "VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, getId());
        ps.setObject(2, getPaid());
        ps.setString(3, getDatePaid());
        ps.setObject(4, getCostEquipment());

        return ps;
    }
    @Override
    public PreparedStatement getUpdateStatement(Database db) throws SQLException {
        PreparedStatement ps = db.conn.prepareStatement("UPDATE EQUIPMENT "
                + "SET paid_equipment=?, date_paid_equipment=?, cost_equipment=? "
                + "WHERE id_equipment=?", Statement.RETURN_GENERATED_KEYS);
        ps.setObject(1, getPaid());
        ps.setString(2, getDatePaid());
        ps.setObject(3, getCostEquipment());
        ps.setInt(4, getId());
        
        return ps;
    }
    @Override
    public PreparedStatement getDeleteStatement(Database db) throws SQLException {
        PreparedStatement ps = db.conn.prepareStatement("DELETE FROM EQUIPMENT "
                + "WHERE id_equipment=?");
        ps.setInt(1, getId());
        
        return ps;
    }
    
    @Override
    public Double sumToPay() {
        return safeGetSum(getCostEquipment());
    }
    
    @Override
    public boolean isEmpty() {
        return getCostEquipment() == null
                && getPaid() == null
                && getDatePaid() == null;
    }
    
    @Override
    public Equipment copy() {
        Equipment equipment = new Equipment(getPaid(), getDatePaid(), 
            getCostEquipment());
        equipment.setId(getId());
        return equipment;
    }
    
    @Override
    public void saveValuesOf(Field field) {
        setCostEquipment(parseField(field.getCostEquipment()));

        setPaid(parseField(field.getPaymentEquipment()));
        setDatePaid(parseField(field.getDatePaidEquipment()));
    }
    
    @Override
    public void bindPeriod(Period period) {
        period.setEquipmentPayment(this);
    }
    
    @Override
    public String checkFields(Field field) {
        return field.checkEquipmentFields();
    }
    @Override
    public boolean fieldsIsFilled(Field fields) {
        return fields.equipmentIsFilled();
    }
    
    @Override
    public void fill(Field field) {
        field.fillEquipment(this);
    }

    @Override
    public void setLabels(Map<String, Label> labels) {
        setText(labels.get("costEquipment"), getCostEquipment());
    }
    
    @Override
    public Payment createNewPayment() {
        return new Equipment();
    }
    
    @Override
    public Double getCredit(BalanceTable balanceTable) {
        return balanceTable.getCreditEquipment();
    }
    @Override
    public void setCredit(BalanceTable balanceTable, Double credit) {
        balanceTable.setCreditEquipment(credit);
    }
    @Override
    public Double getDebit(BalanceTable balanceTable) {
        return balanceTable.getDebitEquipment();
    }
    @Override
    public void setDebit(BalanceTable balanceTable, Double debit) {
        balanceTable.setDebitEquipment(debit);
    }
    
    @Override
    public String toString() {
        return String.format("Платеж на аренду за оборудование. id = %d", this.getId());
    }
}
