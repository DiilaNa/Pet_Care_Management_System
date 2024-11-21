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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
        tableQty.setCellValueFactory(new PropertyValueFactory<>("Quantity"));


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
    private Label Mid;

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
    private ImageView image;

    @FXML
    private Button save;

    @FXML
    private TableView<MedicineTM> table;

    @FXML
    private TableColumn<MedicineTM, String> tableCondition;


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

    @FXML
    private TextField getQTY;

    @FXML
    private Button getQty;

    @FXML
    private Button reset;

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
        String selectedMedicine = Mid.getText();

        if (selectedMedicine == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a medicine to delete.").show();
            return;
        }

        try {
            boolean isDeleted = medicineModel.delete(selectedMedicine);

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
            Double mWeight = Double.valueOf(Mweight.getText());
            Integer qty = Integer.valueOf(Mqty.getText());


        // Regex patterns
            String idPattern = "^[A-Za-z0-9]+$";
            String weightPattern = "^[0-9]*\\.?[0-9]+$"; // Accepts positive numbers with optional decimal
            String quantityPattern = "^[1-9][0-9]*$"; // Accepts positive integers only

            boolean isValidID = medId.matches(idPattern);
            boolean isValidWeight = Mweight.getText().matches(weightPattern);
            boolean isValidQty = Mqty.getText().matches(quantityPattern);

            // Reset styles
            Mid.setStyle("-fx-border-color: none;");
            Mweight.setStyle("-fx-border-color: none;");
            Mqty.setStyle("-fx-border-color: none;");

            if (!isValidID) {
                Mid.setStyle(Mid.getStyle() + ";-fx-border-color: red;");
                System.out.println("Invalid Medicine ID: " + medId);
            }
            if (!isValidWeight) {
                Mweight.setStyle(Mweight.getStyle() + ";-fx-border-color: red;");
                System.out.println("Invalid Weight: " + Mweight.getText());
            }
            if (!isValidQty) {
                Mqty.setStyle(Mqty.getStyle() + ";-fx-border-color: red;");
                System.out.println("Invalid Quantity: " + Mqty.getText());
            }

            if (isValidID  && isValidWeight && isValidQty) {
                MedicineDto medicineDto = new MedicineDto(medId, medName, medCondition, mWeight, qty);

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
            Mqty.setText(String.valueOf(medicineTM.getQuantity()));

            save.setDisable(false);

            delete.setDisable(false);
            update.setDisable(false);
            getQty.setDisable(false);
        }


    }

    @FXML
    void updateAction(ActionEvent event) {
        String medId = Mid.getText();
        String medName = Mname.getText();
        String medCondition = Mcoondition.getText();
        Double mWeight = Double.valueOf(Mweight.getText());
        Integer qty = Integer.valueOf(Mqty.getText());

        // Regex patterns
        String idPattern = "^[A-Za-z0-9]+$";
        String weightPattern = "^[0-9]*\\.?[0-9]+$"; // Accepts positive numbers with optional decimal
        String quantityPattern = "^[1-9][0-9]*$"; // Accepts positive integers only

        boolean isValidID = medId.matches(idPattern);
        boolean isValidWeight = Mweight.getText().matches(weightPattern);
        boolean isValidQty = Mqty.getText().matches(quantityPattern);

        // Reset styles
        Mid.setStyle("-fx-border-color: none;");
        Mweight.setStyle("-fx-border-color: none;");
        Mqty.setStyle("-fx-border-color: none;");

        if (!isValidID) {
            Mid.setStyle(Mid.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Medicine ID: " + medId);
        }
        if (!isValidWeight) {
            Mweight.setStyle(Mweight.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Weight: " + Mweight.getText());
        }
        if (!isValidQty) {
            Mqty.setStyle(Mqty.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Quantity: " + Mqty.getText());
        }

        if (isValidID && isValidWeight && isValidQty) {
            MedicineDto medicineDto = new MedicineDto(medId, medName, medCondition,mWeight, qty);

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
        getNextMedId();

        save.setDisable(false);
        getQty.setDisable(true);
        update.setDisable(true);
        delete.setDisable(true);

        Mname.setText("");
        Mcoondition.setText("");
        Mweight.setText("");
        Mqty.setText("");
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
                    medicineDto.getQuantity()

            );
            medicineTMS.add(medicineTM);
        }

        table.setItems(medicineTMS);
    }

    @FXML
    void getqtyAction(ActionEvent event) {
        try {
            MedicineTM selectedMedicine = table.getSelectionModel().getSelectedItem();

            if (selectedMedicine != null) {
                // Validate the inputs before attempting to parse them
                String mQtyText = getQTY.getText();
                int mQty = Integer.parseInt(mQtyText);

                // Calculate the new quantity
                int currentQty = selectedMedicine.getQuantity(); // Assuming a getter for quantity
                int updatedQty = currentQty - mQty;

                if (updatedQty >= 0) {
                    selectedMedicine.setQuantity(updatedQty); // Assuming a setter for quantity
                    updateMedicineQuantityInDatabase(selectedMedicine.getMedicineId(), updatedQty);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Medicine Qty Updated");
                    alert.setHeaderText(null);
                    alert.setContentText("Medicine Qty Updated");
                    alert.showAndWait();
                    table.refresh();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText(" Quantity is Over");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Select Quantity From the table ");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Enter an Quantity to get");
            alert.showAndWait();
        }
    }

    // Update the quantity in the database
    private void updateMedicineQuantityInDatabase(String medicineId, int updatedQty) {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/animal_hospital", "root", "Ijse@1234")) {
            String sql = "UPDATE medicine SET Qty = ? WHERE medicine_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, updatedQty);
            preparedStatement.setString(2, medicineId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void getNextMedId(){
        String NextMedID = medicineModel.getNextMedid();
        Mid.setText(NextMedID);
    }
    @FXML
    void reseTAction(ActionEvent event) {
        getNextMedId();
        Mname.setText("");
        Mcoondition.setText("");
        Mweight.setText("");
        Mqty.setText("");

    }


}
