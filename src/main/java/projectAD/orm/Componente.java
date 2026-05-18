package projectAD.orm;

import java.util.List;

import projectAD.IDAO;
import projectAD.model.Department;
import projectAD.model.Employee;

public class Componente implements IDAO {
    @Override
    public List<Employee> findAllEmployees() {
        return List.of();
    }

    @Override
    public Employee findEmployeeById(Object id) {
        return null;
    }

    @Override
    public boolean addEmployee(Employee employee) {
        return false;
    }

    @Override
    public boolean updateEmployee(Object id) {
        return false;
    }

    @Override
    public boolean deleteEmployee(Object id) {
        return false;
    }

    @Override
    public List<Department> findAllDepartments() {
        return List.of();
    }

    @Override
    public Department findDepartmentById(Object id) {
        return null;
    }

    @Override
    public boolean addDepartment(Department department) {
        return false;
    }

    @Override
    public boolean updateDepartment(Object id) {
        return false;
    }

    @Override
    public boolean deleteDepartment(Object id) {
        return false;
    }

    @Override
    public List<Employee> findEmployeesByDept(Object idDept) {
        return List.of();
    }
}
