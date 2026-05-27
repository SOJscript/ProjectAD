package projectAD;

import projectAD.model.Department;
import projectAD.model.Employee;
import java.util.List;

public class poblarBdMongo {
    public static void main(String[] args) {
        IDAO postgres = new projectAD.postgres.Componente();
        IDAO mongo = new projectAD.mongo.Componente();

        List<Department> departamentos = postgres.findAllDepartments();
        for (Department d : departamentos) {
            mongo.addDepartment(d);
        }

        List<Employee> empleados = postgres.findAllEmployees();

        for (Employee e : empleados) {
            mongo.addEmployee(e);
        }
    }
}
