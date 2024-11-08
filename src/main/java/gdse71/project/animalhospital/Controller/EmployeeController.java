package gdse71.project.animalhospital.Controller;

import gdse71.project.animalhospital.dto.Employeedto;
import gdse71.project.animalhospital.dto.PetTm.ApointmentsTM;
import gdse71.project.animalhospital.dto.PetTm.EmployeeTM;
import gdse71.project.animalhospital.dto.PetTm.OwnerTM;
import gdse71.project.animalhospital.model.EmployeeModel;
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

public class EmployeeController implements Initializable {

    @FXML
    private TextField address;

    @FXML
    private Button back;

    @FXML
    private TextField contact;

    @FXML
    private Button delete;

    @FXML
    private TextField duty;

    @FXML
    private TextField empID;

    @FXML
    private TextField empNAme;

    @FXML
    private ImageView image;

    @FXML
    private Button save;

    @FXML
    private TableView<EmployeeTM> table;

    @FXML
    private TableColumn<EmployeeTM, String> tableAddress;

    @FXML
    private TableColumn<EmployeeTM, String> tableContactNo;

    @FXML
    private TableColumn<EmployeeTM, String> tableDuty;

    @FXML
    private TableColumn<EmployeeTM, String> tableEmpID;

    @FXML
    private TableColumn<EmployeeTM, String> tableEmpName;

    @FXML
    private Button update;

    EmployeeModel employeeModel = new EmployeeModel();

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
        EmployeeTM selectedEmployee = table.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an employee to delete.").show();
            return;
        }

        String employeeID = selectedEmployee.getEmployeeId();
        try {
            boolean isDeleted = employeeModel.delete(employeeID);
            if (isDeleted) {
                refreshPage();
                new Alert(Alert.AlertType.INFORMATION, "Employee deleted successfully!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to delete employee.").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "An error occurred while deleting the employee.").show();
        }

    }

    @FXML
    void saveAction(ActionEvent event) {
        String employeeID = empID.getText();
        String employeeName = empNAme.getText();
        String employeeDuty = duty.getText();
        String employeeAddress = address.getText();
        String employeeContact = contact.getText();

        String idPattern = "^[A-Za-z0-9]+$";
        String namePattern = "^[A-Za-z\\s]+$";
        String dutyPattern = "^[A-Za-z\\s]+$";
        String addressPattern ="^[a-zA-Z0-9, -]+$";
        String contactPattern = "^\\d{10}$";

        boolean isValidID = employeeID.matches(idPattern);
        boolean isValidName = employeeName.matches(namePattern);
        boolean isValidDuty = employeeDuty.matches(dutyPattern);
        boolean isValidAddress = employeeAddress.matches(addressPattern);
        boolean isValidContact = employeeContact.matches(contactPattern);

 /*       // Reset styles to default
        empID.setStyle(null);
        empNAme.setStyle(null);
        duty.setStyle(null);
        address.setStyle(null);
        contact.setStyle(null);*/

        if (!isValidID) {
            empID.setStyle("-fx-border-color: red;");
            System.out.println("Invalid Employee ID: " + employeeID);
        }
        if (!isValidName) {
            empNAme.setStyle("-fx-border-color: red;");
            System.out.println("Invalid Employee Name: " + employeeName);
        }
        if (!isValidDuty) {
            duty.setStyle("-fx-border-color: red;");
            System.out.println("Invalid Duty: " + employeeDuty);
        }
        if (!isValidAddress) {
            address.setStyle("-fx-border-color: red;");
            System.out.println("Invalid Address: " + employeeAddress);
        }
        if (!isValidContact) {
            contact.setStyle("-fx-border-color: red;");
            System.out.println("Invalid Contact Number: " + employeeContact);
        }

        if (isValidID && isValidName && isValidDuty && isValidAddress && isValidContact) {
            Employeedto employeedto = new Employeedto(
                    employeeID,
                    employeeName,
                    employeeDuty,
                    employeeAddress,
                    employeeContact
            );

            try {
                boolean isSaved = employeeModel.save(employeedto);
                if (isSaved) {
                    refreshPage();
                    new Alert(Alert.AlertType.INFORMATION, "Employee saved successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save employee.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred while saving the employee.").show();
            }
        }
    }

    @FXML
    void tableONACTION(MouseEvent event) {
        EmployeeTM employeeTM = table.getSelectionModel().getSelectedItem();

        if (employeeTM != null) {
            empID.setText(employeeTM.getEmployeeId());
            empNAme.setText(employeeTM.getEmployeeName());
            duty.setText(employeeTM.getEmployeeDuty());
            address.setText(employeeTM.getEmployeeAddress());
            contact.setText(employeeTM.getEmployeePhone());

            save.setDisable(false);

            delete.setDisable(false);
            update.setDisable(false);
        }

    }

    @FXML
    void updateAction(ActionEvent event) {
        String employeeID = empID.getText();
        String employeeName = empNAme.getText();
        String employeeDuty = duty.getText();
        String employeeAddress = address.getText();
        String employeeContact = contact.getText();

        String idPattern = "^[A-Za-z0-9]+$";
        String namePattern = "^[A-Za-z\\s]+$";
        String dutyPattern = "^[A-Za-z\\s]+$";
        String addressPattern ="^[a-zA-Z0-9, -]+$";
        String contactPattern = "^\\d{10}$";

        boolean isValidID = employeeID.matches(idPattern);
        boolean isValidName = employeeName.matches(namePattern);
        boolean isValidDuty = employeeDuty.matches(dutyPattern);
        boolean isValidAddress = employeeAddress.matches(addressPattern);
        boolean isValidContact = employeeContact.matches(contactPattern);

 /*       // Reset styles to default
        empID.setStyle(null);
        empNAme.setStyle(null);
        duty.setStyle(null);
        address.setStyle(null);
        contact.setStyle(null);*/

        if (!isValidID) {
            empID.setStyle("-fx-border-color: red;");
            System.out.println("Invalid Employee ID: " + employeeID);
        }
        if (!isValidName) {
            empNAme.setStyle("-fx-border-color: red;");
            System.out.println("Invalid Employee Name: " + employeeName);
        }
        if (!isValidDuty) {
            duty.setStyle("-fx-border-color: red;");
            System.out.println("Invalid Duty: " + employeeDuty);
        }
        if (!isValidAddress) {
            address.setStyle("-fx-border-color: red;");
            System.out.println("Invalid Address: " + employeeAddress);
        }
        if (!isValidContact) {
            contact.setStyle("-fx-border-color: red;");
            System.out.println("Invalid Contact Number: " + employeeContact);
        }

        if (isValidID && isValidName && isValidDuty && isValidAddress && isValidContact) {
            Employeedto employeedto = new Employeedto(
                    employeeID,
                    employeeName,
                    employeeDuty,
                    employeeAddress,
                    employeeContact
            );

            try {
                boolean isSaved = employeeModel.update(employeedto);
                if (isSaved) {
                    refreshPage();
                    new Alert(Alert.AlertType.INFORMATION, "Employee updated successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to update employee.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred while updating the employee.").show();
            }
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image loginImage = new Image(getClass().getResourceAsStream("/images/Employee.jpeg"));
        image.setImage(loginImage);

        tableEmpID.setCellValueFactory(new PropertyValueFactory<>("EmployeeId"));
        tableEmpName.setCellValueFactory(new PropertyValueFactory<>("EmployeeName"));
        tableDuty.setCellValueFactory(new PropertyValueFactory<>("EmployeeDuty"));
        tableAddress.setCellValueFactory(new PropertyValueFactory<>("EmployeeAddress"));
        tableContactNo.setCellValueFactory(new PropertyValueFactory<>("EmployeePhone"));


        try {
            refreshPage();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Fail to load ").show();
        }

    }
    private void loadTableData() throws SQLException, ClassNotFoundException {

        ArrayList<Employeedto> employeedtos = employeeModel.getAll();
        ObservableList<EmployeeTM> employeeTMS = FXCollections.observableArrayList();

        for (Employeedto employeedto : employeedtos) {
            EmployeeTM employeeTM = new EmployeeTM(
                    employeedto.getEmployeeId(),
                    employeedto.getEmployeeName(),
                    employeedto.getEmployeeDuty(),
                    employeedto.getEmployeeAddress(),
                    employeedto.getEmployeePhone()

            );
            employeeTMS.add(employeeTM);
        }

        table.setItems(employeeTMS);
    }
    private void refreshPage() throws SQLException, ClassNotFoundException {

        loadTableData();

        save.setDisable(false);

        update.setDisable(true);
        delete.setDisable(true);

        empID.setText("");
        empNAme.setText("");
        duty.setText("");
        address.setText("");
        contact.setText("");

    }
}
