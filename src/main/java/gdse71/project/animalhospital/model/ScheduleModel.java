package gdse71.project.animalhospital.model;

import gdse71.project.animalhospital.CrudUtil.Util;
import gdse71.project.animalhospital.dto.ScheduleDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ScheduleModel {
    public ArrayList<ScheduleDto> getAll() throws SQLException, ClassNotFoundException {
        ResultSet rst =  Util.execute("select * from schedule");

        ArrayList<ScheduleDto> servicedtos = new ArrayList<>();

        while (rst.next()){
            ScheduleDto servicedto = new ScheduleDto(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3)

            );
            servicedtos.add(servicedto);
        }
        return servicedtos;
    }
    public boolean save(ScheduleDto scheduleDto) throws SQLException, ClassNotFoundException {
        return Util.execute(
                "insert into schedule values (?,?,?)",
                scheduleDto.getScheduleID(),
                scheduleDto.getDate(),
                scheduleDto.getTime()
        );
    }
    public boolean update(ScheduleDto scheduleDto) throws SQLException, ClassNotFoundException {
        return Util.execute(
                "update schedule set  schedule_date=?, schedule_time=?  where schedule_id=?",
                scheduleDto.getDate(),
                scheduleDto.getTime(),
                scheduleDto.getScheduleID()
        );
    }

    public boolean delete(String schedule_id) throws SQLException, ClassNotFoundException {
        return Util.execute("delete from schedule where schedule_id=?", schedule_id);
    }
    public String  getNextScheduleID() throws SQLException, ClassNotFoundException {
        try {
            ResultSet rst = Util.execute("select schedule_id from schedule order by schedule_id desc limit 1");
            if (rst.next()) {
                String lastId = rst.getString(1); // Last appointment ID
                String numericPart = lastId.replaceAll("[^0-9]", ""); // Extract numeric part only
                if (numericPart.isEmpty()) {
                    return "SCH001"; // Return default if no numeric part found
                } // Extract the numeric part
                int i = Integer.parseInt(numericPart); // Convert the numeric part to integer
                int newIdIndex = i + 1; // Increment the number by 1
                return String.format("SCH%03d", newIdIndex); // Return the new customer ID in format Cnnn
            }
        } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
            throw new RuntimeException(e);
        }
        return "SCH001";
    }
}
