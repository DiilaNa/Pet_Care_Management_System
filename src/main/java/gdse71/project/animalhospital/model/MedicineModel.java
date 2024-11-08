package gdse71.project.animalhospital.model;

import gdse71.project.animalhospital.CrudUtil.Util;
import gdse71.project.animalhospital.dto.MedicineDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MedicineModel {
    public ArrayList<MedicineDto> getAll() throws SQLException, ClassNotFoundException {
        ResultSet rst = Util.execute("SELECT m.medicine_id, m.med_name, m.med_condition, m.weight, m.inv_id, i.qoh, i.weight FROM medicine m JOIN inventory i ON m.inv_id = i.inventory_id");

        ArrayList<MedicineDto> medicineDtos = new ArrayList<>();
        while (rst.next()) {
            MedicineDto medicineDto = new MedicineDto(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getDouble(4),
                    rst.getString(5),
                    rst.getInt(6),
                    rst.getDouble(7)
            );
            medicineDtos.add(medicineDto);
        }
        return medicineDtos;
    }

    public boolean save(MedicineDto medicineDto) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            connection = Util.getConnection();
            connection.setAutoCommit(false);

            // Insert into inventory table
            boolean inventorySaved = Util.execute(
                    "INSERT INTO inventory (inventory_id, weight, qoh) VALUES (?, ?, ?)",
                    medicineDto.getInventoryId(),
                    medicineDto.getFullWeight(),
                    medicineDto.getQuantity()
            );

            if (!inventorySaved) {
                connection.rollback();
                return false;
            }

            // Insert into medicine table
            boolean medicineSaved = Util.execute(
                    "INSERT INTO medicine (medicine_id, med_name, med_condition, weight, inv_id) VALUES (?, ?, ?, ?, ?)",
                    medicineDto.getMedicineId(),
                    medicineDto.getMedicineName(),
                    medicineDto.getMedicineCondition(),
                    medicineDto.getMedicineWeight(),
                    medicineDto.getInventoryId()
            );

            if (medicineSaved) {
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

    public boolean update(MedicineDto medicineDto) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            connection = Util.getConnection();
            connection.setAutoCommit(false);

            // Update inventory table
            boolean inventoryUpdated = Util.execute(
                    "UPDATE inventory SET weight = ?, qoh = ? WHERE inventory_id = ?",
                    medicineDto.getFullWeight(),
                    medicineDto.getQuantity(),
                    medicineDto.getInventoryId()
            );

            if (!inventoryUpdated) {
                connection.rollback();
                return false;
            }

            // Update medicine table
            boolean medicineUpdated = Util.execute(
                    "UPDATE medicine SET med_name = ?, med_condition = ?, weight = ? WHERE medicine_id = ?",
                    medicineDto.getMedicineName(),
                    medicineDto.getMedicineCondition(),
                    medicineDto.getMedicineWeight(),
                    medicineDto.getMedicineId()
            );

            if (medicineUpdated) {
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



    public boolean delete(String medicineId, String inventoryId) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        PreparedStatement medicineStmt = null;
        PreparedStatement inventoryStmt = null;
        boolean success = false;

        try {
            // Get a connection
            connection = Util.getConnection();
            connection.setAutoCommit(false); // Disable auto-commit for transaction

            // Prepare SQL statements
            String deleteMedicineQuery = "DELETE FROM medicine WHERE medicine_id = ?";
            String deleteInventoryQuery = "DELETE FROM inventory WHERE inventory_id = ?";

            // Create prepared statements
            medicineStmt = connection.prepareStatement(deleteMedicineQuery);
            inventoryStmt = connection.prepareStatement(deleteInventoryQuery);

            // Set parameters for medicine deletion
            medicineStmt.setString(1, medicineId);

            // Set parameters for inventory deletion
            inventoryStmt.setString(1, inventoryId);

            // Execute the delete queries
            int medicineRowsAffected = medicineStmt.executeUpdate();
            int inventoryRowsAffected = inventoryStmt.executeUpdate();

            // Check if both deletions were successful
            if (medicineRowsAffected > 0 && inventoryRowsAffected > 0) {
                // Commit the transaction
                connection.commit();
                success = true;
            } else {
                // Rollback if any deletion failed
                connection.rollback();
            }
        } catch (SQLException | ClassNotFoundException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback in case of error
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx); // Add suppressed exception if rollback fails
                }
            }
            throw e; // Rethrow the original exception
        }

        return success;
    }

}
