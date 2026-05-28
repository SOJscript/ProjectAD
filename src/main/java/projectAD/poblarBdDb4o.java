package projectAD;

import projectAD.model.Department;
import projectAD.model.Employee;
import java.util.List;

public class poblarBdDb4o {
    public static void main(String[] args) {
        IDAO postgres = new projectAD.postgres.Componente();
        IDAO db4o = new projectAD.db4o.Componente();

        List<Department> departamentos = postgres.findAllDepartments();
        for (Department d : departamentos) {
            db4o.addDepartment(d);
        }

        List<Employee> empleados = postgres.findAllEmployees();
        for (Employee e : empleados) {
            db4o.addEmployee(e);
        }
    }
}