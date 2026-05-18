package projectAD.fichero;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import projectAD.IDAO;
import projectAD.model.Department;
import projectAD.model.Employee;

public class Componente implements IDAO {


    private List<Department> departamentos = new ArrayList<>();
    private List<Employee> empleados = new ArrayList<>();

    public Componente() {
        cargarFichero();
    }

    private void cargarFichero() {
    try (BufferedReader br = new BufferedReader(new FileReader("ruta/empresa.txt"))) {
        String linea;
        while ((linea = br.readLine()) != null) {
            if (linea.startsWith("--") || linea.isBlank()) continue;

            if (linea.startsWith("department(")) {
                // extraer contenido entre paréntesis y hacer split por ","

                // crear Department y añadirlo a departamentos

            } else if (linea.startsWith("employee(")) {
                // igual pero crear Employee

                // buscar el Department correspondiente por depno
                
            }
        }
    } catch (IOException e) {
        System.out.println("Error al leer el fichero: " + e.getMessage());
    }
}

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
