package gdse71.project.animalhospital.model;

import gdse71.project.animalhospital.CrudUtil.Util;
import gdse71.project.animalhospital.dto.Employeedto;
import gdse71.project.animalhospital.dto.Ownerdto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EmployeeModel {
    public ArrayList<Employeedto> getAll() throws SQLException, ClassNotFoundException {
        ResultSet rst =  Util.execute("select * from employee");

        ArrayList<Employeedto> employeedtos = new ArrayList<>();

        while (rst.next()){
            Employeedto employeedto = new Employeedto(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getString(4),
                    rst.getString(5)

            );
            employeedtos.add(employeedto);
        }
        return employeedtos;
    }
    public boolean save(Employeedto employeedto) throws SQLException, ClassNotFoundException {
        return Util.execute(
                "insert into employee values (?,?,?,?,?)",
                employeedto.getEmployeeId(),
                employeedto.getEmployeeName(),
                employeedto.getEmployeeDuty(),
                employeedto.getEmployeeAddress(),
                employeedto.getEmployeePhone()

        );
    }
    public boolean update(Employeedto employeedto) throws SQLException, ClassNotFoundException {
        return Util.execute(
                "update employee set emp_name=?, duty=?, address=?, tel_no=? where emp_id=?",
                employeedto.getEmployeeName(),
                employeedto.getEmployeeDuty(),
                employeedto.getEmployeeAddress(),
                employeedto.getEmployeePhone(),
                employeedto.getEmployeeId()
        );
    }

    public boolean delete(String emp_id) throws SQLException, ClassNotFoundException {
        return Util.execute("delete from employee where emp_id=?", emp_id);
    }
    public String getNextID(){
        try {
            ResultSet rst = null;
            rst = Util.execute("select emp_id from employee order by emp_id desc limit 1");
            if (rst.next()) {
                String lastId = rst.getString(1);
                String numericPart = lastId.replaceAll("[^0-9]", "");
                if (numericPart.isEmpty()) {
                    return "EMP001";
                }
                int i = Integer.parseInt(numericPart);
                int newIdIndex = i + 1;
                return String.format("EMP%03d", newIdIndex);
            }
        } catch (ClassNotFoundException | NumberFormatException |SQLException e) {
            throw new RuntimeException(e);
        }
        return "EMP001";
    }
}
