/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economistworkstation.Entity;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author fnajer
 */
public class Building {
    private final IntegerProperty id;
    private final StringProperty type;
    private final DoubleProperty square;
    private final DoubleProperty costBalance;
    private final DoubleProperty costResidue;
    
    public Building() {
        this(null, 0.0, 0.0, 0.0);
    }
  
    public Building(String type, double square, double costBalance, double costResidue) {
       this.id = new SimpleIntegerProperty(0);
       this.type = new SimpleStringProperty(type);
       this.square = new SimpleDoubleProperty(square);
       this.costBalance = new SimpleDoubleProperty(costBalance);
       this.costResidue = new SimpleDoubleProperty(costResidue);
    }
    
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }
    
    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }
    
    public StringProperty typeProperty() {
        return type;
    }
    
    public double getSquare() {
        return square.get();
    }

    public void setSquare(Double square) {
        this.square.set(square);
    }
    
    public DoubleProperty squareProperty() {
        return square;
    }
    
    public double getCostBalance() {
        return costBalance.get();
    }

    public void setCostBalance(Double costBalance) {
        this.costBalance.set(costBalance);
    }
    
    public double getCostResidue() {
        return costResidue.get();
    }

    public void setCostResidue(Double costResidue) {
        this.costResidue.set(costResidue);
    }
    
    @Override
    public String toString() {
        return String.format("Тип: %s. Площадь: %.2f", 
                getType(), getSquare());
    }
}
