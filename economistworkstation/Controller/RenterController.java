/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economistworkstation.Controller;

import economistworkstation.Database;
import economistworkstation.Entity.Renter;
import economistworkstation.Model.RenterModel;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import economistworkstation.Model.RenterModel;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 *
 * @author fnajer
 */

public class RenterController implements Initializable {
    public Database db;
    
    // any creational constructor destroy executing program
    
    @FXML
    private VBox containerRenters;

    @FXML
    public void showListRenters() {
        ArrayList<Renter> renters = RenterModel.getRenters(db.stmt);

        ObservableList listRenters = containerRenters.getChildren();  
        listRenters.clear();
        
        for(Renter renter : renters){
            Label lblRent = new Label(renter.name);
            Button delBtn = new Button("X");
            
            delBtn.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    delRenter(renter.id);
                }
            });
            
            FlowPane root = new FlowPane(10, 10, lblRent, delBtn);
            listRenters.add(root);
        }
    }
    
    public void delRenter(int id) {
        //String name = renterName.getText();
        RenterModel.deleteRenter(db.stmt, id);
        showListRenters();
    }
    
    @FXML
    public void showRenterForm(ActionEvent event) throws IOException {
        RenterFormController renterFormController = new RenterFormController();
        renterFormController.setWindow(this);
        try {
            renterFormController.displayPage();
        } catch (Exception ex) {
            Logger.getLogger(RenterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        db = Database.getInstance();
        showListRenters();
    }
    
    @FXML
    public void displayPage(BorderPane root) throws Exception {
        Parent container = FXMLLoader.load(getClass().getResource("/economistworkstation/View/Renter/Renter.fxml"));
        
        root.setCenter(container);
    }
}
