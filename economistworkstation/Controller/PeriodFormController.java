/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economistworkstation.Controller;

import economistworkstation.EconomistWorkstation;
import economistworkstation.Entity.ExtraCost;
import economistworkstation.Entity.Contract;
import economistworkstation.Entity.Fine;
import economistworkstation.Entity.Payment;
import economistworkstation.Entity.Period;
import economistworkstation.Entity.Rent;
import economistworkstation.Entity.Equipment;
import economistworkstation.Entity.Services;
import economistworkstation.Entity.TaxLand;
import economistworkstation.Model.PeriodModel;
import economistworkstation.Util.TagParser;
import economistworkstation.Util.Util;
import static economistworkstation.Util.Util.setText;
import static economistworkstation.Util.Util.parseField;
import static economistworkstation.Util.Util.isExist;
import static economistworkstation.Util.Util.isFilled;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author fnajer
 */
public class PeriodFormController {
    @FXML
    private Label numberLabel;
    @FXML
    private Label numberRentAccLabel;
    @FXML
    private Label numberServicesAccLabel;
    @FXML
    private Label startPeriodLabel;
    @FXML
    private Label endPeriodLabel;
    
    //calculation
    @FXML
    private Label costRentLabel;
    @FXML
    private TextField costRentField;
    @FXML
    private TextField indexCostRentField;
    @FXML
    private Label sumRentLabel;
    @FXML
    private Label extraCostRentLabel;
    @FXML
    private Label sumExtraCostRentLabel;
    //payment
    @FXML
    private TextField paymentRentField;
    @FXML
    private Label statePaymentRentLabel;
    @FXML
    private Label balancePaymentRentLabel;
    
    //calculation
    @FXML
    private TextField fineField;
    @FXML
    private Label fineLabel;
    @FXML
    private Label extraCostFineLabel;
    @FXML
    private Label sumExtraCostFineLabel;
    @FXML
    private Label sumRentWithFineLabel;
    //payment
    @FXML
    private TextField paymentFineField;
    @FXML
    private Label statePaymentFineLabel;
    @FXML
    private Label balancePaymentFineLabel;
    
    //calculation
    @FXML
    private TextField taxLandField;
    @FXML
    private Label taxLandLabel;
    @FXML
    private Label extraCostTaxLandLabel;
    @FXML
    private Label sumExtraCostTaxLandLabel;
    //payment
    @FXML
    private TextField paymentTaxLandField;
    @FXML
    private Label statePaymentTaxLandLabel;
    @FXML
    private Label balancePaymentTaxLandLabel;
    
    //calculation
    @FXML
    private TextField countWaterField;
    @FXML
    private Label countWaterLabel;
    @FXML
    private TextField tariffWaterField;
    @FXML
    private Label costWaterLabel;
    @FXML
    private TextField countElectricityField;
    @FXML
    private Label countElectricityLabel;
    @FXML
    private TextField tariffElectricityField;
    @FXML
    private Label costElectricityLabel;
    @FXML
    private TextField costHeadingField;
    @FXML
    private Label costHeadingLabel;
    @FXML
    private TextField costGarbageField;
    @FXML
    private Label costGarbageLabel;
    @FXML
    private TextField costInternetField;
    @FXML
    private Label costInternetLabel;
    @FXML
    private TextField costTelephoneField;
    @FXML
    private Label costTelephoneLabel;
    @FXML
    private Label sumServicesLabel;
    @FXML
    private Label extraCostServicesLabel;
    //payment
    @FXML
    private TextField paymentServicesField;
    @FXML
    private Label statePaymentServicesLabel;
    @FXML
    private Label balancePaymentServicesLabel;
    
    //calculation
    @FXML
    private TextField costEquipmentField;
    @FXML
    private Label costEquipmentLabel;
    @FXML
    private Label extraCostEquipmentLabel;
    @FXML
    private Label sumExtraCostEquipmentLabel;
    //payment
    @FXML
    private TextField paymentEquipmentField;
    @FXML
    private Label statePaymentEquipmentLabel;
    @FXML
    private Label balancePaymentEquipmentLabel;
    
    private Label[] labels;
    private TextField[] textFields;
    
    private void setMatchArrays() {
        this.labels = new Label[]{costRentLabel, costRentLabel, fineLabel, 
            taxLandLabel, countWaterLabel, countWaterLabel, countElectricityLabel, 
            countElectricityLabel, costHeadingLabel,
            costGarbageLabel, costInternetLabel,
            costTelephoneLabel};
        this.textFields = new TextField[]{costRentField, indexCostRentField, fineField, 
            taxLandField, countWaterField, tariffWaterField, countElectricityField,
            tariffElectricityField, costHeadingField,
            costGarbageField, costInternetField,
            costTelephoneField};
    }

    private Stage dialogStage;
    private Period period;
    private boolean okClicked = false;
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setPeriod(Period period, Contract contract) {
        this.period = period;
        
        Util.setCalledClass(this);
        
        numberLabel.setText(Integer.toString(period.getNumber()));
        numberRentAccLabel.setText(Integer.toString(period.getNumberRentAcc()));
        numberServicesAccLabel.setText(Integer.toString(period.getNumberServicesAcc()));
        startPeriodLabel.setText(period.getStartPeriod(contract.getDateStart()));
        endPeriodLabel.setText(period.getEndPeriod());
        
        Rent rent = (Rent) period.getRentPayment();
        Fine fine = (Fine) period.getFinePayment();
        TaxLand taxLand = (TaxLand) period.getTaxLandPayment();
        Services services = (Services) period.getServicesPayment();
        Equipment equipment = (Equipment) period.getEquipmentPayment();
        
        if (isExist(rent)) {
            setText(costRentField, rent.getCost());
            setText(indexCostRentField, rent.getIndexCost());
//        statePaymentRentLabel.setText(Util.boolToString(period.getPaidRent()));
        }
        if (isExist(fine)) {
            setText(fineField, fine.getFine());
        }
        if (isExist(taxLand)) {
            setText(taxLandField, taxLand.getTaxLand());
        }
        if (isExist(services)) {
            setText(countWaterField, services.getCountWater());
            setText(tariffWaterField, services.getTariffWater());
            setText(countElectricityField, services.getCountElectricity());
            setText(tariffElectricityField, services.getTariffElectricity());
            setText(costHeadingField, services.getCostHeading());
            setText(costGarbageField, services.getCostGarbage());
            setText(costInternetField, services.getCostInternet());
            setText(costTelephoneField, services.getCostTelephone());
        }
        if (isExist(equipment)) {
            setText(costEquipmentField, equipment.getCostEquipment());
        }
        
        refreshExtraCost();
        
        setMatchArrays();
        setColorLabels(textFields);
        
        initCalc();
    }

    public boolean isOkClicked() {
        return okClicked;
    }
    
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            Rent rent = (Rent) period.getRentPayment();
            Fine fine = (Fine) period.getFinePayment();
            TaxLand taxLand = (TaxLand) period.getTaxLandPayment();
            Services services = (Services) period.getServicesPayment();
            Equipment equipment = (Equipment) period.getEquipmentPayment();
            
            if (isFilled(costRentField, indexCostRentField)) {
                if (rent == null)
                    rent = new Rent();
                rent.setCost(parseField(costRentField));
                rent.setIndexCost(parseField(indexCostRentField));
//            period.setPaidRent(Util.stringToBool(paidRentField.getText()));
                period.setRentPayment(rent);
            } else if (isExist(rent)) {
                Rent rentForDelete = new Rent();
                rentForDelete.setId(rent.getId());
                rentForDelete.setPaid(-1);
                period.setRentPayment(rentForDelete);
            }
            if (isFilled(fineField)) {
                if (fine == null)
                    fine = new Fine();
                fine.setFine(parseField(fineField));
                period.setFinePayment(fine);
            } else if (isExist(fine)) {
                Fine fineForDelete = new Fine();
                fineForDelete.setId(fine.getId());
                fineForDelete.setPaid(-1);
                period.setFinePayment(fineForDelete);
            }
            if (isFilled(taxLandField)) {
                if (taxLand == null)
                    taxLand = new TaxLand();
                taxLand.setTaxLand(parseField(taxLandField));
                period.setTaxLandPayment(taxLand);
            } else if (isExist(taxLand)) {
                TaxLand taxLandForDelete = new TaxLand();
                taxLandForDelete.setId(taxLand.getId());
                taxLandForDelete.setPaid(-1);
                period.setTaxLandPayment(taxLandForDelete);
            }
            if (isFilled(countWaterField, tariffWaterField, countElectricityField,
                    tariffElectricityField, costHeadingField, costGarbageField,
                    costInternetField, costTelephoneField)) {
                if (services == null)
                    services = new Services();
                services.setCountWater(parseField(countWaterField));
                services.setTariffWater(parseField(tariffWaterField));
                services.setCountElectricity(parseField(countElectricityField));
                services.setTariffElectricity(parseField(tariffElectricityField));
                services.setCostHeading(parseField(costHeadingField));
                services.setCostGarbage(parseField(costGarbageField));
                services.setCostInternet(parseField(costInternetField));
                services.setCostTelephone(parseField(costTelephoneField));
                period.setServicesPayment(services);
            } else if (isExist(services)) {
                Services servicesForDelete = new Services();
                servicesForDelete.setId(services.getId());
                servicesForDelete.setPaid(-1);
                period.setServicesPayment(servicesForDelete);
            }
            if (isFilled(costEquipmentField)) {
                if (equipment == null)
                    equipment = new Equipment();
                equipment.setCostEquipment(parseField(costEquipmentField));
                period.setEquipmentPayment(equipment);
            } else if (isExist(equipment)) {
                Equipment equipmentForDelete = new Equipment();
                equipmentForDelete.setId(equipment.getId());
                equipmentForDelete.setPaid(-1);
                period.setEquipmentPayment(equipmentForDelete);
            }
            
            okClicked = true;
            dialogStage.close();
        }
    }
    
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
    private boolean isInputValid() {
        String errorMessage = "";

//        if (firstNameField.getText() == null || firstNameField.getText().length() == 0) {
//            errorMessage += "Введите имя!\n"; 
//        }
//        if (lastNameField.getText() == null || lastNameField.getText().length() == 0) {
//            errorMessage += "Введите фамилию!\n"; 
//        }
//        if (patronymicField.getText() == null || patronymicField.getText().length() == 0) {
//            errorMessage += "Введите отчество!\n"; 
//        }
//        if (addressField.getText() == null || addressField.getText().length() == 0) {
//            errorMessage += "Введите адрес!\n"; 
//        }
//        if (birthdayField.getText() == null || birthdayField.getText().length() == 0) {
//            errorMessage += "Неверная дата рождения!\n"; 
//        }
//        if (personField.getText() == null || personField.getText().length() == 0) {
//            errorMessage += "Введите физ. лицо!\n";
//        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Показываем сообщение об ошибке.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Некорректные данные");
            alert.setHeaderText("Заполните поля корректно");
            alert.setContentText(errorMessage);
            
            alert.showAndWait();
            
            return false;
    }}
    
    private boolean isPositive(TextField tf) {
        try {
            double value = Double.parseDouble(tf.getText());
            System.out.println(value > 0);
            return value > 0;
        } catch(NumberFormatException e) {
            System.out.println("false - set gray color");
            return false;
        }
    }
    
    private void setColorLabels(TextField ...tfs) {
        for (TextField tf: tfs) {
            int index = Arrays.asList(this.textFields).indexOf(tf);
            if (!isPositive(tf)) {
                labels[index].setTextFill(Color.web("#C8C1C1"));
            } else {
                labels[index].setTextFill(Color.web("#0F0A0A"));
            }
        }
    }
    
    
    private void setTextAndColor(boolean isPaid, Label label, Button button) {
        if (isPaid) {
            label.setTextFill(Color.web("#96DA1B"));
            button.setText("Отменить");
        }
        else {
            label.setTextFill(Color.web("#DA221B"));
            button.setText("Оплатить");
        }
    }
    @FXML
    private TitledPane servicesPane;
    
    private void initCalc() {
        servicesPane.setTextFill(Color.web("#96DA1B"));
        displaySum();
        addCalcListeners();
    }
    
    private void displaySum() {
        setCost(countWaterField, tariffWaterField, costWaterLabel);
        setCost(countElectricityField, tariffElectricityField, costElectricityLabel);
        printSum("services");
        setCost(costRentField, indexCostRentField, sumRentLabel);
        printSum("rentFine");
    }
    
    private void setCost(TextField firstField, TextField secondField, Label label) {
        try {
            String count = firstField.getText();
            String tariff = secondField.getText();
            
            if ("".equals(count) && "".equals(tariff)) {
                label.setText("");  
                return;
            }
            
            double total = Double.parseDouble(count) * Double.parseDouble(tariff);
            if (total < 0) throw new NumberFormatException();
            String formatTotal = TagParser.getDecimalFormat(Locale.US).format(total);
            label.setText(formatTotal);
        } catch(NumberFormatException e) {
            label.setText("...");    
        }
    }
    
    private void calcSum(Label label, Control ...controls) {
        try {
            double sum = 0;
            double value;
            String formatSum;
            String givenText = "";

            for (Control control: controls) {
                if (control instanceof Label)
                    givenText = ((Label) control).getText();
                else if(control instanceof TextField)
                    givenText = ((TextField) control).getText();
                
                if ("".equals(givenText)) continue;
                value = Double.parseDouble(givenText);
                if (value < 0) throw new NumberFormatException();
                sum += value;
            }
            formatSum = TagParser.getDecimalFormat(Locale.US).format(sum);
            label.setText(formatSum);
        } catch(NumberFormatException e) {
            label.setText("...");
        }
    }
    
    private void printSum(String sumFor) {
        if (sumFor.equals("services"))
            calcSum(sumServicesLabel, costWaterLabel, costElectricityLabel, costHeadingField,
                    costGarbageField, costInternetField, costTelephoneField);
        else if(sumFor.equals("rentFine"))
            calcSum(sumRentWithFineLabel, sumRentLabel, fineField);
    }
   
    public void addCalcListeners() {
        
        countWaterField.textProperty().addListener((observable, oldValue, newValue) -> {
            setCost(countWaterField, tariffWaterField, costWaterLabel);
            printSum("services");
            setColorLabels(countWaterField);
        });
        tariffWaterField.textProperty().addListener((observable, oldValue, newValue) -> {
            setCost(countWaterField, tariffWaterField, costWaterLabel);
            printSum("services");
            setColorLabels(tariffWaterField);
        });
        
        countElectricityField.textProperty().addListener((observable, oldValue, newValue) -> {
            setCost(countElectricityField, tariffElectricityField, costElectricityLabel);
            printSum("services");
            setColorLabels(countElectricityField);
        });
        tariffElectricityField.textProperty().addListener((observable, oldValue, newValue) -> {
            setCost(countElectricityField, tariffElectricityField, costElectricityLabel);
            printSum("services");
            setColorLabels(tariffElectricityField);
        });
        
        costHeadingField.textProperty().addListener((observable, oldValue, newValue) -> {
           printSum("services");
           setColorLabels(costHeadingField);
        });
        
        costGarbageField.textProperty().addListener((observable, oldValue, newValue) -> {
           printSum("services");
           setColorLabels(costGarbageField);
        });
        
        costInternetField.textProperty().addListener((observable, oldValue, newValue) -> {
            printSum("services");
            setColorLabels(costInternetField);
        });
        costTelephoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            printSum("services");
            setColorLabels(costTelephoneField);
        });
        
        costRentField.textProperty().addListener((observable, oldValue, newValue) -> {
            setCost(costRentField, indexCostRentField, sumRentLabel);
            printSum("rentFine");
            setColorLabels(costRentField);
        });
        indexCostRentField.textProperty().addListener((observable, oldValue, newValue) -> {
            setCost(costRentField, indexCostRentField, sumRentLabel);
            printSum("rentFine");
            setColorLabels(indexCostRentField);
        });
        fineField.textProperty().addListener((observable, oldValue, newValue) -> {
            printSum("rentFine");
            setColorLabels(fineField);
        });
        
        taxLandField.textProperty().addListener((observable, oldValue, newValue) -> {
            setColorLabels(taxLandField);
        });
    }

    
}
