package gdse71.project.animalhospital.Controller;

import gdse71.project.animalhospital.dto.PetTm.SalaryTM;
import gdse71.project.animalhospital.dto.Salarydto;
import gdse71.project.animalhospital.model.SalaryModel;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class SalaryController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image loginImage = new Image(getClass().getResourceAsStream("/images/salary.jpeg"));
        image.setImage(loginImage);

        tableSalaryId.setCellValueFactory(new PropertyValueFactory<>("salaryId"));
        tableSalaryDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableSalaryAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        tableSalaryEmployeeId.setCellValueFactory(new PropertyValueFactory<>("EmployeeId"));

        try {
            refreshPage();
            loadEmpIds();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TextField SalAmount;

    @FXML
    private TextField SalDate;

    @FXML
    private ComboBox<String> SalEmpId;

    @FXML
    private Button back;

    @FXML
    private Button delete;

    @FXML
    private ImageView image;

    @FXML
    private TextField salaryID;

    @FXML
    private Button save;

    @FXML
    private TableView<SalaryTM> table;

    @FXML
    private TableColumn<SalaryTM, Double> tableSalaryAmount;

    @FXML
    private TableColumn<SalaryTM, Date> tableSalaryDate;

    @FXML
    private TableColumn<SalaryTM, String> tableSalaryEmployeeId;

    @FXML
    private TableColumn<SalaryTM, String> tableSalaryId;

    @FXML
    private Button update;

    SalaryModel salaryModel = new SalaryModel();

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
        SalaryTM selectedSalary = table.getSelectionModel().getSelectedItem();

        if (selectedSalary == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a medicine to delete.").show();
            return;
        }

        try {
            boolean isDeleted = salaryModel.delete(selectedSalary.getSalaryId());

            if (isDeleted) {
                refreshPage();
                new Alert(Alert.AlertType.INFORMATION, "Salary Record deleted successfully!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to delete Salary Record.").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "An error occurred while deleting the Salary Record.").show();
        }

    }

    @FXML
    void saveAction(ActionEvent event) {
        // Retrieve input values
        String salaryId = salaryID.getText();
        String dateText = SalDate.getText();
        String amountText = SalAmount.getText();
        String employeeId = SalEmpId.getValue();

        // Regex patterns
        String idPattern = "^[A-Za-z0-9]+$"; // Alphanumeric pattern
        String amountPattern = "^[0-9]*\\.?[0-9]+$"; // Accepts positive numbers with optional decimal

        boolean isValidID = salaryId.matches(idPattern);
        boolean isValidDate = !dateText.isEmpty(); // Simple non-empty check (you may apply additional date validation)
        boolean isValidAmount = amountText.matches(amountPattern);
        boolean isValidEmpID = employeeId != null && employeeId.matches(idPattern);

      /*  // Reset styles
        salaryID.setStyle("-fx-border-color: none;");
        SalDate.setStyle("-fx-border-color: none;");
        SalAmount.setStyle("-fx-border-color: none;");
        SalEmpId.setStyle("-fx-border-color: none;");
*/
        // Highlight invalid fields
        if (!isValidID) {
            salaryID.setStyle(salaryID.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Salary ID: " + salaryId);
        }
        if (!isValidDate) {
            SalDate.setStyle(SalDate.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Date: " + dateText);
        }
        if (!isValidAmount) {
            SalAmount.setStyle(SalAmount.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Amount: " + amountText);
        }
        if (!isValidEmpID) {
            SalEmpId.setStyle(SalEmpId.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Employee ID: " + employeeId);
        }

        // Proceed if all inputs are valid
        if (isValidID && isValidDate && isValidAmount && isValidEmpID) {
            try {
                double amount = Double.parseDouble(amountText);

                // Assuming date format "yyyy-MM-dd"
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = formatter.parse(dateText);

                Salarydto salarydto = new Salarydto(salaryId, date, amount, employeeId);

                boolean isSaved = salaryModel.save(salarydto);
                if (isSaved) {
                    refreshPage();
                    new Alert(Alert.AlertType.INFORMATION, "Salary saved successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save salary.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred while saving the salary.").show();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Please fix the highlighted errors before saving.").show();
        }

    }

    @FXML
    void tableClick(MouseEvent event) {
        SalaryTM salaryTM = table.getSelectionModel().getSelectedItem();
        if (salaryTM != null) {

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = salaryTM.getDate() != null ? dateFormatter.format(salaryTM.getDate()) : "";

            salaryID.setText(salaryTM.getSalaryId());
            SalDate.setText(formattedDate);
            SalAmount.setText(String.valueOf(salaryTM.getAmount()));
            SalEmpId.setValue(salaryTM.getEmployeeId());

            save.setDisable(false);

            delete.setDisable(false);
            update.setDisable(false);
        }


    }

    @FXML
    void updateAction(ActionEvent event) {
        // Retrieve input values
        String salaryId = salaryID.getText();
        String dateText = SalDate.getText();
        String amountText = SalAmount.getText();
        String employeeId = SalEmpId.getValue();

        // Regex patterns
        String idPattern = "^[A-Za-z0-9]+$"; // Alphanumeric pattern
        String amountPattern = "^[0-9]*\\.?[0-9]+$"; // Accepts positive numbers with optional decimal

        boolean isValidID = salaryId.matches(idPattern);
        boolean isValidDate = !dateText.isEmpty(); // Simple non-empty check (you may apply additional date validation)
        boolean isValidAmount = amountText.matches(amountPattern);
        boolean isValidEmpID = employeeId != null && employeeId.matches(idPattern);

      /*  // Reset styles
        salaryID.setStyle("-fx-border-color: none;");
        SalDate.setStyle("-fx-border-color: none;");
        SalAmount.setStyle("-fx-border-color: none;");
        SalEmpId.setStyle("-fx-border-color: none;");
*/
        // Highlight invalid fields
        if (!isValidID) {
            salaryID.setStyle(salaryID.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Salary ID: " + salaryId);
        }
        if (!isValidDate) {
            SalDate.setStyle(SalDate.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Date: " + dateText);
        }
        if (!isValidAmount) {
            SalAmount.setStyle(SalAmount.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Amount: " + amountText);
        }
        if (!isValidEmpID) {
            SalEmpId.setStyle(SalEmpId.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Employee ID: " + employeeId);
        }

        // Proceed if all inputs are valid
        if (isValidID && isValidDate && isValidAmount && isValidEmpID) {
            try {
                double amount = Double.parseDouble(amountText);

                // Assuming date format "yyyy-MM-dd"
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = formatter.parse(dateText);

                Salarydto salarydto = new Salarydto(
                        salaryId,
                        date,
                        amount,
                        employeeId
                );

                boolean isSaved = salaryModel.update(salarydto);
                if (isSaved) {
                    refreshPage();
                    new Alert(Alert.AlertType.INFORMATION, "Salary updated successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to update salary.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred while updating the salary.").show();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Please fix the highlighted errors before updating.").show();
        }


    }
    private void loadEmpIds() throws SQLException {
        final Connection connection;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/animal_hospital", "root", "Ijse@1234");

            ResultSet rs = connection.createStatement().executeQuery("SELECT emp_id FROM employee");
            ObservableList<String> data = FXCollections.observableArrayList();

            while (rs.next()) {
                data.add(rs.getString("emp_id"));
            }
            SalEmpId.setItems(data);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void loadTableData() {
        try {
            ArrayList<Salarydto> salarydtos = salaryModel.getAll();
            ObservableList<SalaryTM> salaryTMS = FXCollections.observableArrayList();

            for (Salarydto salarydto : salarydtos) {
                SalaryTM salaryTM = new SalaryTM(
                        salarydto.getSalaryId(),
                        salarydto.getDate(),
                        salarydto.getAmount(),
                        salarydto.getEmployeeId()
                );
                salaryTMS.add(salaryTM);
            }

            table.setItems(salaryTMS);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Failed to load data into the table.");
        }
    }

    private void refreshPage() throws SQLException, ClassNotFoundException {

        loadTableData();

        save.setDisable(false);

        update.setDisable(true);
        delete.setDisable(true);

        salaryID.setText("");
        SalDate.setText("");
        SalAmount.setText("");
        SalEmpId.setValue(null);

    }

}
