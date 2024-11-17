package gdse71.project.animalhospital.model;

import gdse71.project.animalhospital.CrudUtil.Util;
import gdse71.project.animalhospital.db.DBConnection;
import gdse71.project.animalhospital.dto.MedicineDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MedicineModel {
    public ArrayList<MedicineDto> getAll() throws SQLException, ClassNotFoundException {
        ResultSet rst = Util.execute("SELECT * from medicine");
        ArrayList<MedicineDto> medicineDtos = new ArrayList<>();
        while (rst.next()) {
            MedicineDto medicineDto = new MedicineDto(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getDouble(4),
                    rst.getInt(5)
            );
            medicineDtos.add(medicineDto);
        }
        return medicineDtos;
    }

    public boolean save(MedicineDto medicineDto) throws SQLException, ClassNotFoundException {

            // Insert into medicine table
            boolean medicineSaved = Util.execute(
                    "INSERT INTO medicine (medicine_id, med_name, med_condition, weight, Qty) VALUES (?, ?, ?, ?, ?)",
                    medicineDto.getMedicineId(),
                    medicineDto.getMedicineName(),
                    medicineDto.getMedicineCondition(),
                    medicineDto.getMedicineWeight(),
                    medicineDto.getQuantity()
            );
            return medicineSaved;
    }

    public boolean update(MedicineDto medicineDto) throws SQLException, ClassNotFoundException {


        // Update medicine table
        boolean medicineUpdated = Util.execute(
                "UPDATE medicine SET med_name = ?, med_condition = ?, weight = ? ,Qty = ? WHERE medicine_id = ?",
                medicineDto.getMedicineName(),
                medicineDto.getMedicineCondition(),
                medicineDto.getMedicineWeight(),
                medicineDto.getQuantity(),
                medicineDto.getMedicineId()
        );
        return medicineUpdated;
    }



    public boolean delete(String medicineId) throws SQLException, ClassNotFoundException {
       return Util.execute("DELETE FROM medicine WHERE medicine_id = ?", medicineId);
    }
   /* public boolean updateQuantity(double updatedQty) throws SQLException, ClassNotFoundException {
        String query = "UPDATE medicine SET Qoh = ? WHERE medicine_id = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, updatedQty);

            return statement.executeUpdate() > 0;
        }
    }
*/
}
