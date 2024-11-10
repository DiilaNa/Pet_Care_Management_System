package gdse71.project.animalhospital.model;

import gdse71.project.animalhospital.CrudUtil.Util;
import gdse71.project.animalhospital.dto.PaymentInvoicedto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PaymentInvoiceModel {
    public ArrayList<PaymentInvoicedto> getAll() throws SQLException, ClassNotFoundException {
        ResultSet rst = Util.execute("SELECT i.invoice_no, i.name, i.amount, p.payment_id AS payment_id, p.payment_date, p.payment_method FROM invoice i JOIN payment p ON i.pay_id = p.payment_id");

        ArrayList<PaymentInvoicedto> paymentInvoicedtos = new ArrayList<>();
        while (rst.next()) {
            PaymentInvoicedto paymentInvoicedto = new PaymentInvoicedto(
                    rst.getString(4), // payment_id
                    rst.getString(5), // payment_date
                    rst.getString(6), // payment_method
                    rst.getString(1), // invoice_no
                    rst.getString(2), // invoice_name
                    rst.getDouble(3)  // invoice_amount

            );
            paymentInvoicedtos.add(paymentInvoicedto);
        }
        return paymentInvoicedtos;
    }

    public boolean save(PaymentInvoicedto paymentInvoicedto) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            connection = Util.getConnection();
            connection.setAutoCommit(false);

            // Insert into inventory table
            boolean paymentSaved = Util.execute(
                    "INSERT INTO payment (payment_id, payment_date, payment_method) VALUES (?, ?, ?)",
                    paymentInvoicedto.getPaymentId(),
                    paymentInvoicedto.getPaymentDate(),
                    paymentInvoicedto.getPaymentMethod()
            );

            if (!paymentSaved) {
                connection.rollback();
                return false;
            }

            // Insert into medicine table
            boolean invoiceSaved = Util.execute(
                    "INSERT INTO invoice (invoice_no, name, amount, pay_id) VALUES (?, ?, ?, ?)",
                    paymentInvoicedto.getInvoiceNo(),
                    paymentInvoicedto.getInvoiceName(),
                    paymentInvoicedto.getInvoiceAmount(),
                    paymentInvoicedto.getPaymentId()
            );

            if (invoiceSaved) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            throw e;
        }
    }

    public boolean update(PaymentInvoicedto paymentInvoicedto) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            connection = Util.getConnection();
            connection.setAutoCommit(false);

            // Update payment table
            boolean paymentSaved = Util.execute(
                    "UPDATE payment SET payment_date = ?, payment_method = ? WHERE payment_id = ?",
                    paymentInvoicedto.getPaymentDate(),
                    paymentInvoicedto.getPaymentMethod(),
                    paymentInvoicedto.getPaymentId()
            );

            if (!paymentSaved) {
                connection.rollback();
                return false;
            }

            // Update invoice table
            boolean invoiceSaved = Util.execute(
                    "UPDATE invoice SET name = ?, amount = ?, pay_id = ? WHERE invoice_no = ?",
                    paymentInvoicedto.getInvoiceName(),
                    paymentInvoicedto.getInvoiceAmount(),
                    paymentInvoicedto.getPaymentId(),
                    paymentInvoicedto.getInvoiceNo()
            );

            if (invoiceSaved) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            throw e;
        }
    }



    public boolean delete(String invoice_no, String payment_id) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        PreparedStatement paymentStmt = null;
        PreparedStatement invoiceStmt = null;
        boolean success = false;

        try {
            connection = Util.getConnection();
            connection.setAutoCommit(false); // Disable auto-commit for transaction

            // Prepare SQL statements
            String deleteInvoiceQuery = "DELETE FROM invoice WHERE invoice_no = ?";
            String deletePaymentQuery = "DELETE FROM payment WHERE payment_id = ?";

            // Create prepared statements
            invoiceStmt = connection.prepareStatement(deleteInvoiceQuery);
            paymentStmt = connection.prepareStatement(deletePaymentQuery);

            // Set parameters for invoice and payment deletion
            invoiceStmt.setString(1, invoice_no);
            paymentStmt.setString(1, payment_id);

            // Execute the delete queries
            int invoiceRowsAffected = invoiceStmt.executeUpdate();
            int paymentRowsAffected = paymentStmt.executeUpdate();

            // Check if both deletions were successful
            if (invoiceRowsAffected > 0 && paymentRowsAffected > 0) {
                connection.commit();
                success = true;
            } else {
                connection.rollback();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        }
        return success;
    }
}
