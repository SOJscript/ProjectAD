package projectAD.postgres;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        List<Employee> lista = new ArrayList<>();
        String consulta = "SELECT * FROM empleado";

        try (Connection conexion = getConnection();
             PreparedStatement ps = conexion.prepareStatement(consulta);
             ResultSet resultadoQuery = ps.executeQuery()) {

            while (resultadoQuery.next()) {
                Employee empleado = new Employee();
                empleado.setEmpno(resultadoQuery.getInt("empno"));
                empleado.setNombre(resultadoQuery.getString("nombre"));
                empleado.setPuesto(resultadoQuery.getString("puesto"));
                lista.add(empleado);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar empleados: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Employee findEmployeeById(Object id) {
        Employee empleado = null;
        String consulta = "SELECT * FROM empleado WHERE empno = ?";

        try (Connection conexion = getConnection();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {
            ps.setInt(1, (Integer) id);

            try (ResultSet resultadoQuery = ps.executeQuery()) {
                if (resultadoQuery.next()) {
                    empleado = new Employee();
                    empleado.setEmpno(resultadoQuery.getInt("empno"));
                    empleado.setNombre(resultadoQuery.getString("nombre"));
                    empleado.setPuesto(resultadoQuery.getString("puesto"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el empleado: " + e.getMessage());
        }
        return empleado;
    }

    @Override
    public boolean addEmployee(Employee employee) {
        String consulta = "INSERT INTO empleado (empno, nombre, puesto, depno) VALUES (?, ?, ?, ?)";

        try (Connection conexion = getConnection();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {

            ps.setInt(1, employee.getEmpno());
            ps.setString(2, employee.getNombre());
            ps.setString(3, employee.getPuesto());

            // Si el empleado tiene departamento, lo guardamos; si no, NULL
            if (employee.getDepartamento() != null) {
                ps.setInt(4, employee.getDepartamento().getDepno());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al añadir empleado: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateEmployee(Object id) {
        Scanner sc = new Scanner(System.in);

        // Primero comprobamos que el empleado existe
        Employee existente = findEmployeeById(id);
        if (existente == null) {
            System.out.println("No existe ningún empleado con ese ID.");
            return false;
        }

        System.out.println("Empleado actual: " + existente.getNombre() + " (" + existente.getPuesto() + ")");

        System.out.print("Nuevo nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Nuevo puesto: ");
        String puesto = sc.nextLine();

        System.out.print("Nuevo ID de departamento (0 si ninguno): ");
        int depno = Integer.parseInt(sc.nextLine());

        String consulta = "UPDATE empleado SET nombre = ?, puesto = ?, depno = ? WHERE empno = ?";

        try (Connection conexion = getConnection();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {

            ps.setString(1, nombre);
            ps.setString(2, puesto);

            if (depno != 0) {
                ps.setInt(3, depno);
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setInt(4, (Integer) id);

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar empleado: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteEmployee(Object id) {
        String consulta = "DELETE FROM empleado WHERE empno = ?";

        try (Connection conexion = getConnection();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {

            ps.setInt(1, (Integer) id);
            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar empleado: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Department> findAllDepartments() {
        List<Department> lista = new ArrayList<>();
        String consulta = "SELECT * FROM departamento";

        try (Connection conexion = getConnection();
             PreparedStatement ps = conexion.prepareStatement(consulta);
             ResultSet resultadoQuery = ps.executeQuery()) {

            while (resultadoQuery.next()) {
                Department departamento = new Department();
                departamento.setDepno(resultadoQuery.getInt("depno"));
                departamento.setNombre(resultadoQuery.getString("nombre"));
                departamento.setUbicacion(resultadoQuery.getString("ubicacion"));
                lista.add(departamento);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar los departamentos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Department findDepartmentById(Object id) {
        Department departamento = null;
        String consulta = "SELECT * FROM departamento WHERE depno = ?";

        try (Connection conexion = getConnection();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {

            ps.setInt(1, (Integer) id);
            try (ResultSet resultadoQuery = ps.executeQuery()) {
                if (resultadoQuery.next()) {
                    departamento = new Department();
                    departamento.setDepno(resultadoQuery.getInt("depno"));
                    departamento.setNombre(resultadoQuery.getString("nombre"));
                    departamento.setUbicacion(resultadoQuery.getString("ubicacion"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el departamento: " + e.getMessage());
        }
        return departamento;
    }

    @Override
    public boolean addDepartment(Department department) {
        String consulta = "INSERT INTO departamento (depno, nombre, ubicacion) VALUES (?, ?, ?)";

        try (Connection conexion = getConnection();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {

            ps.setInt(1, department.getDepno());
            ps.setString(2, department.getNombre());
            ps.setString(3, department.getUbicacion());

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al añadir departamento: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateDepartment(Object id) {
        Scanner sc = new Scanner(System.in);

        Department existente = findDepartmentById(id);
        if (existente == null) {
            System.out.println("No existe ningún departamento con ese ID.");
            return false;
        }

        System.out.println("Departamento actual: " + existente.getNombre() + " | " + existente.getUbicacion());

        System.out.print("Nuevo nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Nueva ubicación: ");
        String ubicacion = sc.nextLine();

        String consulta = "UPDATE departamento SET nombre = ?, ubicacion = ? WHERE depno = ?";

        try (Connection conexion = getConnection();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {

            ps.setString(1, nombre);
            ps.setString(2, ubicacion);
            ps.setInt(3, (Integer) id);

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar departamento: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteDepartment(Object id) {
        String consulta = "DELETE FROM departamento WHERE depno = ?";

        try (Connection conexion = getConnection();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {

            ps.setInt(1, (Integer) id);
            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar departamento: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Employee> findEmployeesByDept(Object idDept) {
        List<Employee> lista = new ArrayList<>();
        String consulta = "SELECT * FROM empleado WHERE depno = ?";

        try (Connection conexion = getConnection();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {

            ps.setInt(1, (Integer) idDept);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Employee emp = new Employee();
                    emp.setEmpno(rs.getInt("empno"));
                    emp.setNombre(rs.getString("nombre"));
                    emp.setPuesto(rs.getString("puesto"));
                    lista.add(emp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar empleados del departamento: " + e.getMessage());
        }
        return lista;
    }
}