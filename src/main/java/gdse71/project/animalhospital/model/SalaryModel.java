package gdse71.project.animalhospital.model;

import gdse71.project.animalhospital.CrudUtil.Util;
import gdse71.project.animalhospital.dto.Salarydto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SalaryModel {

    public ArrayList<Salarydto> getAll() throws SQLException, ClassNotFoundException {
        ResultSet rst =  Util.execute("select * from salary");

        ArrayList<Salarydto> salarydtos = new ArrayList<>();

        while (rst.next()){
            Salarydto salarydto = new Salarydto(
                    rst.getString(1),
                    rst.getDate(2),
                    rst.getDouble(3),
                    rst.getString(4)

            );
            salarydtos.add(salarydto);
        }
        return salarydtos;
    }
    public boolean save(Salarydto salarydto) throws SQLException, ClassNotFoundException {
        return Util.execute(
                "insert into salary values (?,?,?,?)",
                salarydto.getSalaryId(),
                salarydto.getDate(),
                salarydto.getAmount(),
                salarydto.getEmployeeId()
        );
    }
    public boolean update(Salarydto salarydto) throws SQLException, ClassNotFoundException {
        return Util.execute(
                "update salary set  payment_date=?, amount=?, employee_id=?  where salary_id=?",
                salarydto.getDate(),
                salarydto.getAmount(),
                salarydto.getEmployeeId(),
                salarydto.getSalaryId()
                );
    }

    public boolean delete(String salary_id ) throws SQLException, ClassNotFoundException {
        return Util.execute("delete from salary where salary_id=?", salary_id);
    }
}
