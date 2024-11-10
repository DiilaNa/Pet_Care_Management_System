package gdse71.project.animalhospital.Controller;

import gdse71.project.animalhospital.dto.PaymentInvoicedto;
import gdse71.project.animalhospital.dto.PetTm.MedicineTM;
import gdse71.project.animalhospital.dto.PetTm.PaymentInvoiceTM;
import gdse71.project.animalhospital.dto.PetTm.PetTM;
import gdse71.project.animalhospital.model.PaymentInvoiceModel;
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

public class PaymentInvoiceController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image loginImage = new Image(getClass().getResourceAsStream("/images/payment.jpeg"));
        image.setImage(loginImage);

        tblepaymountID.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        tableDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        tablePaymountMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        tableInvoiceNo.setCellValueFactory(new PropertyValueFactory<>("invoiceNo"));
        tableName.setCellValueFactory(new PropertyValueFactory<>("invoiceName"));
        tableAmount.setCellValueFactory(new PropertyValueFactory<>("invoiceAmount"));

        try {
            refreshPage();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Fail to load id").show();
        }
    }

    @FXML
    private Button back;

    @FXML
    private Button delete;

    @FXML
    private ImageView image;


    @FXML
    private ComboBox<String> paymentMethod;

    @FXML
    private TextField paymentDate;

    @FXML
    private TextField paymentID;

    @FXML
    private TextField paymentInvAmount;

    @FXML
    private TextField paymentInvName;

    @FXML
    private TextField paymentInvoiceNo;

    @FXML
    private Button save;

    @FXML
    private TableView<PaymentInvoiceTM> table;

    @FXML
    private TableColumn<PaymentInvoiceTM, Double> tableAmount;

    @FXML
    private TableColumn<PaymentInvoiceTM, String> tableDate;

    @FXML
    private TableColumn<PaymentInvoiceTM, String> tableInvoiceNo;

    @FXML
    private TableColumn<PaymentInvoiceTM, String> tableName;

    @FXML
    private TableColumn<PaymentInvoiceTM, String> tablePaymountMethod;

    @FXML
    private TableColumn<PaymentInvoiceTM, String> tblepaymountID;

    @FXML
    private Button update;

    PaymentInvoiceModel paymentInvoiceModel = new PaymentInvoiceModel();

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
        PaymentInvoiceTM selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an id to delete.").show();
            return;
        }

        try {
            boolean isDeleted = paymentInvoiceModel.delete(selected.getInvoiceNo(), selected.getPaymentId());

            if (isDeleted) {
                refreshPage();
                new Alert(Alert.AlertType.INFORMATION, " deleted successfully!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to delete .").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "An error occurred while deleting.").show();
        }

    }

    @FXML
    void saveAction(ActionEvent event) {
        // Get values from text fields
        String payId = paymentID.getText();
        String payDate = paymentDate.getText();
        String payMethod = paymentMethod.getSelectionModel().getSelectedItem();
        String invoiceNo = paymentInvoiceNo.getText();
        String invName = paymentInvName.getText();
        String invAmountStr = paymentInvAmount.getText();

        // Validation patterns
        String idPattern = "^[A-Za-z0-9]+$";
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$"; // Date in YYYY-MM-DD format
        String amountPattern = "^[0-9]*\\.?[0-9]+$"; // Positive numbers with optional decimal

        boolean isValidID = payId.matches(idPattern);
        boolean isValidDate = payDate.matches(datePattern);
        boolean isValidAmount = invAmountStr.matches(amountPattern);

        // Reset styles
        paymentID.setStyle("-fx-border-color: none;");
        paymentDate.setStyle("-fx-border-color: none;");
        paymentInvAmount.setStyle("-fx-border-color: none;");

        // Apply error styles if validation fails
        if (!isValidID) {
            paymentID.setStyle(paymentID.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Payment ID: " + payId);
        }
        if (!isValidDate) {
            paymentDate.setStyle(paymentDate.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Payment Date: " + payDate);
        }
        if (!isValidAmount) {
            paymentInvAmount.setStyle(paymentInvAmount.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Invoice Amount: " + invAmountStr);
        }

        if (isValidID && isValidDate && isValidAmount && payMethod != null && !invoiceNo.isEmpty() && !invName.isEmpty()) {
            double invAmount = Double.parseDouble(invAmountStr);

            // Create PaymentInvoicedto object
            PaymentInvoicedto paymentInvoicedto = new PaymentInvoicedto(payId, payDate, payMethod, invoiceNo, invName, invAmount);

            try {
                // Save the payment invoice
                boolean isSaved = paymentInvoiceModel.save(paymentInvoicedto);
                if (isSaved) {
                    refreshPage();
                    new Alert(Alert.AlertType.INFORMATION, "Payment Invoice saved successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save payment invoice.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred while saving the payment invoice.").show();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Please fix the highlighted errors before saving.").show();
        }

    }

    @FXML
    void tableClick(MouseEvent event) {
        PaymentInvoiceTM paymentInvoiceTM = table.getSelectionModel().getSelectedItem();
        if (paymentInvoiceTM != null) {
            paymentID.setText(paymentInvoiceTM.getPaymentId());
            paymentInvName.setText(paymentInvoiceTM.getInvoiceName());
            paymentInvAmount.setText(String.valueOf(paymentInvoiceTM.getInvoiceAmount()));
            paymentMethod.setValue(paymentInvoiceTM.getPaymentMethod());
            paymentInvoiceNo.setText(paymentInvoiceTM.getInvoiceNo());
            paymentDate.setText(paymentInvoiceTM.getPaymentDate());

            save.setDisable(false);

            delete.setDisable(false);
            update.setDisable(false);
        }

    }

    @FXML
    void updateAction(ActionEvent event) {
        // Get values from text fields
        String payId = paymentID.getText();
        String payDate = paymentDate.getText();
        String payMethod = paymentMethod.getSelectionModel().getSelectedItem();
        String invoiceNo = paymentInvoiceNo.getText();
        String invName = paymentInvName.getText();
        String invAmountStr = paymentInvAmount.getText();

        // Validation patterns
        String idPattern = "^[A-Za-z0-9]+$";
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$"; // Date in YYYY-MM-DD format
        String amountPattern = "^[0-9]*\\.?[0-9]+$"; // Positive numbers with optional decimal

        boolean isValidID = payId.matches(idPattern);
        boolean isValidDate = payDate.matches(datePattern);
        boolean isValidAmount = invAmountStr.matches(amountPattern);

        // Reset styles
        paymentID.setStyle("-fx-border-color: none;");
        paymentDate.setStyle("-fx-border-color: none;");
        paymentInvAmount.setStyle("-fx-border-color: none;");

        // Apply error styles if validation fails
        if (!isValidID) {
            paymentID.setStyle(paymentID.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Payment ID: " + payId);
        }
        if (!isValidDate) {
            paymentDate.setStyle(paymentDate.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Payment Date: " + payDate);
        }
        if (!isValidAmount) {
            paymentInvAmount.setStyle(paymentInvAmount.getStyle() + ";-fx-border-color: red;");
            System.out.println("Invalid Invoice Amount: " + invAmountStr);
        }

        if (isValidID && isValidDate && isValidAmount && payMethod != null && !invoiceNo.isEmpty() && !invName.isEmpty()) {
            double invAmount = Double.parseDouble(invAmountStr);

            // Create PaymentInvoicedto object
            PaymentInvoicedto paymentInvoicedto = new PaymentInvoicedto(payId, payDate, payMethod, invoiceNo, invName, invAmount);

            try {
                // Save the payment invoice
                boolean isSaved = paymentInvoiceModel.update(paymentInvoicedto);
                if (isSaved) {
                    refreshPage();
                    new Alert(Alert.AlertType.INFORMATION, "Payment Invoice updating successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to update payment invoice.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred while updating the payment invoice.").show();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Please fix the highlighted errors before saving.").show();
        }

    }


    @FXML
    void paymentAction(ActionEvent event) {
        String selectedPayment = paymentMethod.getSelectionModel().getSelectedItem();
        if (selectedPayment != null) {
            System.out.println("Selected payment method: " + selectedPayment);
        }

    }

    private void refreshPage() throws SQLException, ClassNotFoundException {

        loadTableData();

        save.setDisable(false);

        update.setDisable(true);
        delete.setDisable(true);

        paymentID.setText("");
        paymentDate.setText("");
        paymentInvoiceNo.setText("");
        paymentInvName.setText("");
        paymentInvAmount.setText("");
        paymentMethod.setItems(FXCollections.observableArrayList("Cash", "Card"));
    }
    private void loadTableData() throws SQLException, ClassNotFoundException {

        ArrayList<PaymentInvoicedto> paymentInvoicedtos = paymentInvoiceModel.getAll();
        ObservableList<PaymentInvoiceTM> paymentInvoiceTMS = FXCollections.observableArrayList();

        for (PaymentInvoicedto paymentInvoicedto: paymentInvoicedtos) {
            PaymentInvoiceTM paymentInvoiceTM = new PaymentInvoiceTM(
                    paymentInvoicedto.getPaymentId(),
                    paymentInvoicedto.getPaymentDate(),
                    paymentInvoicedto.getPaymentMethod(),
                    paymentInvoicedto.getInvoiceNo(),
                    paymentInvoicedto.getInvoiceName(),
                    paymentInvoicedto.getInvoiceAmount()
                   // medicineDto.getFullWeight()

            );
            paymentInvoiceTMS.add(paymentInvoiceTM);
        }

        table.setItems(paymentInvoiceTMS);
    }

}
