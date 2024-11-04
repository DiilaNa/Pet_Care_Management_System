package gdse71.project.animalhospital.model;

import gdse71.project.animalhospital.CrudUtil.Util;
import gdse71.project.animalhospital.dto.Appointmentsdto;
import gdse71.project.animalhospital.dto.PetRecorddto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AppointmentsModel {
    public ArrayList<Appointmentsdto> getAll() throws SQLException, ClassNotFoundException {
        ResultSet rst =  Util.execute("select * from appointments");

        ArrayList<Appointmentsdto> appointmentsdtos = new ArrayList<>();

        while (rst.next()){
            Appointmentsdto appointmentsdto = new Appointmentsdto(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getString(4)

            );
            appointmentsdtos.add(appointmentsdto);
        }
        return appointmentsdtos;
    }
    public boolean save(Appointmentsdto appointmentsdto) throws SQLException, ClassNotFoundException {
        return Util.execute(
                "insert into appointments values (?,?,?,?)",
                appointmentsdto.getAptID(),
                appointmentsdto.getAptDate(),
                appointmentsdto.getAptTime(),
                appointmentsdto.getPayID()

        );
    }
    public boolean update(Appointmentsdto appointmentsdto) throws SQLException, ClassNotFoundException {
        return Util.execute(
                "update appointments set  appointment_date=?, appointment_time=?, pay_id=? , where appointment_id=?",
                appointmentsdto.getAptDate(),
                appointmentsdto.getAptTime(),
                appointmentsdto.getPayID(),
                appointmentsdto.getAptID()
                );
    }
    public boolean delete(String appointment_id) throws SQLException, ClassNotFoundException {
        return Util.execute("delete from appointments where appointment_id=?", appointment_id);
    }

}
