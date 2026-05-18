package projectAD;

import java.util.List;

public interface IDAO {
    public List<Employee> findAllEmployees();
    public Employee findEmployeeById(Object id);
    public boolean addEmployee(Employee employee);
    public boolean updateEmployee(Object id);
    public boolean deleteEmployee(Object id);
    public List<Department> findAllDepartments();
    public Department findDepartmentById(Object id);
    public boolean addDepartment(Department department);
    public boolean updateDepartment(Object id);
    public boolean deleteDepartment(Object id);
    public List<Employee> findEmployeesByDept(Object idDept);
}
