package gdse71.project.animalhospital.Controller;

import gdse71.project.animalhospital.dto.Appointmentsdto;
import gdse71.project.animalhospital.dto.PetTm.ApointmentsTM;
import gdse71.project.animalhospital.model.AppointmentsModel;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppointmentsController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image loginImage = new Image(getClass().getResourceAsStream("/images/Appointments.jpeg"));
        image.setImage(loginImage);

        tableID.setCellValueFactory(new PropertyValueFactory<>("AptID"));
        tableDate.setCellValueFactory(new PropertyValueFactory<>("AptDate"));
        tableTime.setCellValueFactory(new PropertyValueFactory<>("AptTime"));
        tablePayID.setCellValueFactory(new PropertyValueFactory<>("PayID"));



        try {
            refreshPage();
            loadPayIDS();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Fail to load id").show();
        }
    }

    @FXML
    private TextField aptID;

    @FXML
    private Button backID;

    @FXML
    private TextField date;

    @FXML
    private Button delete;

    @FXML
    private ImageView image;

    @FXML
    private ComboBox<String> payID;

    @FXML
    private Button save;

    @FXML
    private TableView<ApointmentsTM> table;

    @FXML
    private TableColumn<ApointmentsTM,String > tableDate;

    @FXML
    private TableColumn<ApointmentsTM, String> tableID;

    @FXML
    private TableColumn<ApointmentsTM, String> tablePayID;

    @FXML
    private TableColumn<ApointmentsTM, String> tableTime;

    @FXML
    private TextField time;

    @FXML
    private Button update;

    AppointmentsModel appointmentsModel = new AppointmentsModel();

    @FXML
    void DeleteAPT(ActionEvent event) {
        String Id = aptID.getText();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> optionalButtonType = alert.showAndWait();

        if (optionalButtonType.isPresent() && optionalButtonType.get() == ButtonType.YES) {

            boolean isDeleted = false;
            try {
                isDeleted = appointmentsModel.delete(Id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (isDeleted) {
                try {
                    refreshPage();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                new Alert(Alert.AlertType.INFORMATION, "  Record deleted...!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Fail to delete Record...!").show();
            }
        }

    }

    @FXML
    void backIDAction(ActionEvent event) {
        try {
            Stage stage = (Stage) backID.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/dashboard.fxml"))));
            stage.setTitle("Pets & Vets Animal Hospital");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void saveAPT(ActionEvent event) throws SQLException, ClassNotFoundException {
            String appointmentID = aptID.getText();
            String appointmentDate = date.getText();
            String appointmentTime = time.getText();
            String paymentID = payID.getValue();

            aptID.setStyle(aptID.getStyle() + ";-fx-border-color: #7367F0;");
            date.setStyle(date.getStyle() + ";-fx-border-color: #7367F0;");
            time.setStyle(time.getStyle() + ";-fx-border-color: #7367F0;");
            payID.setStyle(payID.getStyle() + ";-fx-border-color: #7367F0;");


            String idPattern = "^[A-Za-z0-9]+$";
            String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
            String timePattern = "^\\d{2}:\\d{2}:\\d{2}$";

            boolean isValidID = appointmentID.matches(idPattern);
            boolean isValidDate = appointmentDate.matches(datePattern);
            boolean isValidTime = appointmentTime.matches(timePattern);
            boolean isValidPayID = paymentID != null && !paymentID.isEmpty();

            if (!isValidID) {
                aptID.setStyle(aptID.getStyle() + ";-fx-border-color: red;");
                System.out.println("Invalid appointment ID: " + appointmentID);
            }
            if (!isValidDate) {
                date.setStyle(date.getStyle() + ";-fx-border-color: red;");
                System.out.println("Invalid date format: " + appointmentDate);
            }
            if (!isValidTime) {
                time.setStyle(time.getStyle() + ";-fx-border-color: red;");
                System.out.println("Invalid time format: " + appointmentTime);
            }
            if (!isValidPayID) {
                payID.setStyle(payID.getStyle() + ";-fx-border-color: red;");
                System.out.println("Payment ID is required.");
            }

            if (isValidID && isValidDate && isValidTime && isValidPayID) {



                    Appointmentsdto appointmentsDto = new Appointmentsdto(
                            appointmentID,
                            appointmentDate,
                            appointmentTime,
                            paymentID

                    );

                boolean isSaved = false;
                try {
                    isSaved = appointmentsModel.save(appointmentsDto);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if (isSaved) {
                            refreshPage();
                            new Alert(Alert.AlertType.INFORMATION, "Appointment saved successfully!").show();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Failed to save appointment.").show();
                        }
            }else{
                new Alert(Alert.AlertType.ERROR, "Invalid appointment ID.").show();
            }



    }

    @FXML
    void tableactionMOuse(MouseEvent event) {
        ApointmentsTM apointmentsTM = table.getSelectionModel().getSelectedItem();

        if (apointmentsTM != null) {
            aptID.setText(apointmentsTM.getAptID());
            date.setText(String.valueOf(apointmentsTM.getAptDate()));
            time.setText(String.valueOf(apointmentsTM.getAptTime()));
            payID.setValue(apointmentsTM.getPayID());

            save.setDisable(false);

            delete.setDisable(false);
            update.setDisable(false);
        }


    }

    @FXML
    void updateAPT(ActionEvent event) {
        String appointmentID = aptID.getText();
        String appointmentDate = date.getText();
        String appointmentTime = time.getText();
        String paymentID = payID.getValue();

        aptID.setStyle(aptID.getStyle() + ";-fx-border-color: #7367F0;");
        date.setStyle(date.getStyle() + ";-fx-border-color: #7367F0;");
        time.setStyle(time.getStyle() + ";-fx-border-color: #7367F0;");
        payID.setStyle(payID.getStyle() + ";-fx-border-color: #7367F0;");


        String idPattern = "^[A-Za-z0-9]+$";
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
        String timePattern =  "^\\d{2}:\\d{2}:\\d{2}$";

        boolean isValidID = appointmentID.matches(idPattern);
        boolean isValidDate = appointmentDate.matches(datePattern);
        boolean isValidTime = appointmentTime.matches(timePattern);
        boolean isValidPayID = paymentID != null && !paymentID.isEmpty();

        if (!isValidID) {
            aptID.setStyle(aptID.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid appointment ID: " + appointmentID);
        }
        if (!isValidDate) {
            date.setStyle(date.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid date format: " + appointmentDate);
        }
        if (!isValidTime) {
            time.setStyle(time.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid time format: " + appointmentTime);
        }
        if (!isValidPayID) {
            payID.setStyle(payID.getStyle() + ";-fx-border-color: red;");
            System.out.println("Payment ID is required.");
        }

        if (isValidID && isValidDate && isValidTime && isValidPayID) {

            Appointmentsdto appointmentsDto = new Appointmentsdto(
                    appointmentID,
                    appointmentDate,
                    appointmentTime,
                    paymentID


            );

            try {
                boolean isSaved = appointmentsModel.update(appointmentsDto);
                if (isSaved) {
                    refreshPage();
                    new Alert(Alert.AlertType.INFORMATION, "Appointment updated successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to update appointment.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred while saving the appointment.").show();
            }
        }

    }
    private void loadPayIDS() throws SQLException {
        final Connection connection;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/animal_hospital", "root", "Ijse@1234");

            ResultSet rs = connection.createStatement().executeQuery("SELECT payment_id FROM payment");
            ObservableList<String> data = FXCollections.observableArrayList();

            while (rs.next()) {
                data.add(rs.getString("payment_id"));
            }
            payID.setItems(data);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void loadTableData() throws SQLException, ClassNotFoundException {

        ArrayList<Appointmentsdto> appointmentsdtos = appointmentsModel.getAll();
        ObservableList<ApointmentsTM> apointmentsTMS = FXCollections.observableArrayList();

        for (Appointmentsdto appointmentsdto : appointmentsdtos) {
            ApointmentsTM apointmentsTM = new ApointmentsTM(
                    appointmentsdto.getAptID(),
                    appointmentsdto.getAptDate(),
                    appointmentsdto.getAptTime(),
                    appointmentsdto.getPayID()

            );
            apointmentsTMS.add(apointmentsTM);
        }

        table.setItems(apointmentsTMS);
    }
    private void refreshPage() throws SQLException, ClassNotFoundException {

        loadTableData();

        save.setDisable(false);

        update.setDisable(true);
        delete.setDisable(true);

        aptID.setText("");
        date.setText("");
        time.setText("");

    }



}
