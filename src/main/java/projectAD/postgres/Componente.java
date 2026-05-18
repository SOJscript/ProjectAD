package projectAD.postgres;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import projectAD.IDAO;
import projectAD.model.Department;
import projectAD.model.Employee;

public class Componente implements IDAO {

    // Establecer conexion
    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "1234";
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public List<Employee> findAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String consulta = "SELECT * FROM empleado";

        try (Connection conexion = getConnection();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(consulta)) {

            while (rs.next()) {
                // Creamos el objeto
                Employee emp = new Employee();
                emp.setEmpno(rs.getInt("empno"));
                emp.setNombre(rs.getString("nombre"));
                emp.setPuesto(rs.getString("puesto"));

                list.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar empleados: " + e.getMessage());
        }
        return list;
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
