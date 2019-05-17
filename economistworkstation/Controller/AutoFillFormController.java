/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economistworkstation.Controller;

import economistworkstation.ContractData;
import economistworkstation.Entity.Building;
import economistworkstation.Model.BuildingModel;
import economistworkstation.Model.PeriodModel;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import economistworkstation.Util.Precedency;
import static economistworkstation.Util.Util.isExist;
import static economistworkstation.Util.Util.parseField;
import java.io.File;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author fnajer
 */
public class AutoFillFormController extends BaseFormController {
    @FXML
    private DatePicker dateStartField;
    @FXML
    private DatePicker dateEndField;
    @FXML
    private ComboBox<Building> buildingsListField;
    @FXML
    private ComboBox<Field> fieldsListField;
    @FXML
    private TextField valueField;

    private File newFile;
    private Precedency userPrefs;

    
    @Override
    public void setData(ContractData data) {  
        ObservableList<Building> buildings = BuildingModel.getBuildings();
        Building allBuildingType = createAllBuildingType(buildings);
        buildings.add(0, allBuildingType);
        
        buildingsListField.setItems(buildings);
        fieldsListField.setItems(getFields());
    }
    
    @FXML
    private ObservableList<Field> getFields() {
        return FXCollections.observableArrayList(
                Field.indexInflation, Field.tariffWater,
                Field.tariffElectricity);
    }
    
    @FXML
    @Override
    protected void handleOk() {
        if (isInputValid()) {
            Building building = buildingsListField.getSelectionModel().getSelectedItem();
            Field field = fieldsListField.getSelectionModel().getSelectedItem();
            LocalDate dateStart = dateStartField.getValue();
            LocalDate dateEnd = dateEndField.getValue();
            
            PeriodModel.autoFill(parseField(valueField), 
                    field.getDbValue(), field.getTableName(),
                    dateStart, dateEnd, building.getId(),
                    field.getIdName());
            
            closeForm();
        }
    }
    
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";
        Building building = buildingsListField.getSelectionModel().getSelectedItem();
        Field field = fieldsListField.getSelectionModel().getSelectedItem();
        
        if (!isExist(building)) {
            errorMessage += "Выберите здание!\n"; 
        }
        if (!isExist(field)) {
            errorMessage += "Выберите поле для авто-заполнения!\n"; 
        }
        if (fieldIsEmpty(dateStartField)) {
            errorMessage += "Поставьте начальную дату выборки месяцев!\n"; 
        }
        if (fieldIsEmpty(dateEndField)) {
            errorMessage += "Поставьте конечную дату выборки месяцев!\n"; 
        }
        if (costIsInvalid(valueField)) {
            errorMessage += "Указанное значение для авто-заполнения должно быть числом!\n"; 
        }

        return errorNotExist(errorMessage);
    }
}
