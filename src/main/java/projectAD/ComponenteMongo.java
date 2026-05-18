package projectAD;

import java.util.List;

public class ComponenteMongo implements IDAO {
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

