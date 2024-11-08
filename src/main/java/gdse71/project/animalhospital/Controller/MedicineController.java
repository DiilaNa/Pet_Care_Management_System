package gdse71.project.animalhospital.Controller;

import gdse71.project.animalhospital.dto.Appointmentsdto;
import gdse71.project.animalhospital.dto.MedicineDto;
import gdse71.project.animalhospital.dto.PetTm.ApointmentsTM;
import gdse71.project.animalhospital.dto.PetTm.MedicineTM;
import gdse71.project.animalhospital.model.MedicineModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MedicineController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image loginImage = new Image(getClass().getResourceAsStream("/images/Medicinee.png"));
        image.setImage(loginImage);

        tablemediID.setCellValueFactory(new PropertyValueFactory<>("MedicineId"));
        tableMedNAme.setCellValueFactory(new PropertyValueFactory<>("MedicineName"));
        tableCondition.setCellValueFactory(new PropertyValueFactory<>("MedicineCondition"));
        tableWeight.setCellValueFactory(new PropertyValueFactory<>("MedicineWeight"));
        tableInventory.setCellValueFactory(new PropertyValueFactory<>("InventoryId"));
        tableQty.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        tableFullWEight.setCellValueFactory(new PropertyValueFactory<>("FullWeight"));


        try {
            refreshPage();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Fail to load id").show();
        }

    }

    @FXML
    private TextField Mcoondition;

    @FXML
    private TextField Mid;

    @FXML
    private TextField MinvId;

    @FXML
    private TextField Mname;

    @FXML
    private TextField Mqty;

    @FXML
    private TextField Mweight;

    @FXML
    private Button back;

    @FXML
    private Button delete;

    @FXML
    private Label fullQty;

    @FXML
    private ImageView image;

    @FXML
    private Button save;

    @FXML
    private TableView<MedicineTM> table;

    @FXML
    private TableColumn<MedicineTM, String> tableCondition;

    @FXML
    private TableColumn<MedicineTM, Double> tableFullWEight;

    @FXML
    private TableColumn<MedicineTM, String> tableInventory;

    @FXML
    private TableColumn<MedicineTM, String> tableMedNAme;

    @FXML
    private TableColumn<MedicineTM, Double> tableQty;

    @FXML
    private TableColumn<MedicineTM, Double> tableWeight;

    @FXML
    private TableColumn<MedicineTM, String> tablemediID;

    @FXML
    private Button update;

    MedicineModel medicineModel = new MedicineModel();

    @FXML
    void backAction(ActionEvent event) {
        try {
            Stage stage = (Stage) back.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/dashboard.fxml"))));
            stage.setTitle("Pets & Vets Animal Hospital");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void deleteAction(ActionEvent event) {
        MedicineTM selectedMedicine = table.getSelectionModel().getSelectedItem();

        if (selectedMedicine == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a medicine to delete.").show();
            return;
        }

        try {
            boolean isDeleted = medicineModel.delete(selectedMedicine.getMedicineId(), selectedMedicine.getInventoryId());

            if (isDeleted) {
                refreshPage();
                new Alert(Alert.AlertType.INFORMATION, "Medicine deleted successfully!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to delete medicine.").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "An error occurred while deleting the medicine.").show();
        }
    }

    @FXML
    void saveAction(ActionEvent event) {
            String medId = Mid.getText();
            String medName = Mname.getText();
            String medCondition = Mcoondition.getText();
            String invId = MinvId.getText();

            // Regex patterns
            String idPattern = "^[A-Za-z0-9]+$";
            String weightPattern = "^[0-9]*\\.?[0-9]+$"; // Accepts positive numbers with optional decimal
            String quantityPattern = "^[1-9][0-9]*$"; // Accepts positive integers only

            boolean isValidID = medId.matches(idPattern);
            boolean isValidInvID = invId.matches(idPattern);
            boolean isValidWeight = Mweight.getText().matches(weightPattern);
            boolean isValidQty = Mqty.getText().matches(quantityPattern);

            // Reset styles
            Mid.setStyle("-fx-border-color: none;");
            MinvId.setStyle("-fx-border-color: none;");
            Mweight.setStyle("-fx-border-color: none;");
            Mqty.setStyle("-fx-border-color: none;");

            if (!isValidID) {
                Mid.setStyle(Mid.getStyle() + ";-fx-border-color: red;");
                System.out.println("Invalid Medicine ID: " + medId);
            }
            if (!isValidInvID) {
                MinvId.setStyle(MinvId.getStyle() + ";-fx-border-color: red;");
                System.out.println("Invalid Inventory ID: " + invId);
            }
            if (!isValidWeight) {
                Mweight.setStyle(Mweight.getStyle() + ";-fx-border-color: red;");
                System.out.println("Invalid Weight: " + Mweight.getText());
            }
            if (!isValidQty) {
                Mqty.setStyle(Mqty.getStyle() + ";-fx-border-color: red;");
                System.out.println("Invalid Quantity: " + Mqty.getText());
            }

            if (isValidID && isValidInvID && isValidWeight && isValidQty) {
                double medWeight = Double.parseDouble(Mweight.getText());
                int qty = Integer.parseInt(Mqty.getText());

                // Calculate full weight
                double fullWeight = medWeight * qty;
                fullQty.setText(String.valueOf(fullWeight));

                MedicineDto medicineDto = new MedicineDto(medId, medName, medCondition, medWeight, invId, qty, fullWeight);

                try {
                    boolean isSaved = medicineModel.save(medicineDto);
                    if (isSaved) {
                        refreshPage();
                        new Alert(Alert.AlertType.INFORMATION, "Medicine saved successfully!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to save medicine.").show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred while saving the medicine.").show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Please fix the highlighted errors before saving.").show();
            }

    }

    @FXML
    void tableMouseClicked(MouseEvent event) {
        MedicineTM  medicineTM = table.getSelectionModel().getSelectedItem();

        if (medicineTM != null) {
            Mid.setText(medicineTM.getMedicineId());
            Mname.setText(medicineTM.getMedicineName());
            Mcoondition.setText(medicineTM.getMedicineCondition());
            Mweight.setText(String.valueOf(medicineTM.getMedicineWeight()));
            MinvId.setText(medicineTM.getInventoryId());
            Mqty.setText(String.valueOf(medicineTM.getQuantity()));
            fullQty.setText(String.valueOf(medicineTM.getFullWeight()));

            save.setDisable(false);

            delete.setDisable(false);
            update.setDisable(false);
        }


    }

    @FXML
    void updateAction(ActionEvent event) {
        String medId = Mid.getText();
        String medName = Mname.getText();
        String medCondition = Mcoondition.getText();
        String invId = MinvId.getText();

        // Regex patterns
        String idPattern = "^[A-Za-z0-9]+$";
        String weightPattern = "^[0-9]*\\.?[0-9]+$"; // Accepts positive numbers with optional decimal
        String quantityPattern = "^[1-9][0-9]*$"; // Accepts positive integers only

        boolean isValidID = medId.matches(idPattern);
        boolean isValidInvID = invId.matches(idPattern);
        boolean isValidWeight = Mweight.getText().matches(weightPattern);
        boolean isValidQty = Mqty.getText().matches(quantityPattern);

        // Reset styles
        Mid.setStyle("-fx-border-color: none;");
        MinvId.setStyle("-fx-border-color: none;");
        Mweight.setStyle("-fx-border-color: none;");
        Mqty.setStyle("-fx-border-color: none;");

        if (!isValidID) {
            Mid.setStyle(Mid.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Medicine ID: " + medId);
        }
        if (!isValidInvID) {
            MinvId.setStyle(MinvId.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Inventory ID: " + invId);
        }
        if (!isValidWeight) {
            Mweight.setStyle(Mweight.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Weight: " + Mweight.getText());
        }
        if (!isValidQty) {
            Mqty.setStyle(Mqty.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Quantity: " + Mqty.getText());
        }

        if (isValidID && isValidInvID && isValidWeight && isValidQty) {
            double medWeight = Double.parseDouble(Mweight.getText());
            int qty = Integer.parseInt(Mqty.getText());

            // Calculate full weight
            double fullWeight = medWeight * qty;
            fullQty.setText(String.valueOf(fullWeight));

            MedicineDto medicineDto = new MedicineDto(medId, medName, medCondition, medWeight, invId, qty, fullWeight);

            try {
                boolean isSaved = medicineModel.update(medicineDto);
                if (isSaved) {
                    refreshPage();
                    new Alert(Alert.AlertType.INFORMATION, "Medicine updated successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to update medicine.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred while updating the medicine.").show();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Please fix the highlighted errors before updating.").show();
        }

    }
    private void refreshPage() throws SQLException, ClassNotFoundException {

        loadTableData();

        save.setDisable(false);

        update.setDisable(true);
        delete.setDisable(true);

        Mid.setText("");
        Mname.setText("");
        Mcoondition.setText("");
        Mweight.setText("");
        MinvId.setText("");
        Mqty.setText("");
        fullQty.setText("");
    }
    private void loadTableData() throws SQLException, ClassNotFoundException {

        ArrayList<MedicineDto> medicineDtos = medicineModel.getAll();
        ObservableList<MedicineTM> medicineTMS = FXCollections.observableArrayList();

        for (MedicineDto medicineDto: medicineDtos) {
            MedicineTM medicineTM = new MedicineTM(
                    medicineDto.getMedicineId(),
                    medicineDto.getMedicineName(),
                    medicineDto.getMedicineCondition(),
                    medicineDto.getMedicineWeight(),
                    medicineDto.getInventoryId(),
                    medicineDto.getQuantity(),
                    medicineDto.getFullWeight()

            );
            medicineTMS.add(medicineTM);
        }

        table.setItems(medicineTMS);
    }

}
