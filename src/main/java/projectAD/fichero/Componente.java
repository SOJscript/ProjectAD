package projectAD.fichero;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import projectAD.IDAO;
import projectAD.model.Department;
import projectAD.model.Employee;

public class Componente implements IDAO {

    // Ruta del fichero
    private static final String RUTA = "src/main/java/projectAD/empresa.txt";

    private List<Department> departamentos = new ArrayList<>();
    private List<Employee> empleados = new ArrayList<>();

    public Componente() {
        cargarFichero();
    }

    // Lee el fichero y carga los datos en las listas
    private void cargarFichero() {
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("--") || linea.isBlank()) continue;

                String contenido = linea.substring(linea.indexOf('(') + 1, linea.indexOf(')'));
                String[] partes = contenido.split(",");

                if (linea.startsWith("department(")) {
                    Department d = new Department(Integer.parseInt(partes[0]), partes[1], partes[2]);
                    departamentos.add(d);

                } else if (linea.startsWith("employee(")) {
                    int depno = Integer.parseInt(partes[3]);
                    Department dept = findDepartmentById(depno);
                    Employee e = new Employee(Integer.parseInt(partes[0]), partes[1], partes[2], dept);
                    empleados.add(e);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el fichero: " + e.getMessage());
        }
    }

    // Reescribe el fichero
    private void guardarFichero() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA))) {
            for (Department d : departamentos) {
                bw.write("department(" + d.getDepno() + "," + d.getNombre() + "," + d.getUbicacion() + ")");
                bw.newLine();
            }
            for (Employee e : empleados) {
                int depno = e.getDepartamento().getDepno();
                bw.write("employee(" + e.getEmpno() + "," + e.getNombre() + "," + e.getPuesto() + "," + depno + ")");
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al escribir el fichero: " + e.getMessage());
        }
    }

    @Override
    public List<Employee> findAllEmployees() {
        return empleados;
    }

    @Override
    public Employee findEmployeeById(Object id) {
        int idEmp = (Integer) id;
        for (Employee e : empleados) {
            if (e.getEmpno() == idEmp) return e;
        }
        return null;
    }

    @Override
    public boolean addEmployee(Employee employee) {
        empleados.add(employee);
        guardarFichero();
        return true;
    }

    @Override
    public boolean updateEmployee(Object id) {
        Scanner sc = new Scanner(System.in);

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

        System.out.print("Nuevo ID de departamento: ");
        int depno = Integer.parseInt(sc.nextLine());

        Department dep = findDepartmentById(depno);
        if (dep == null) {
            System.out.println("No existe ningún departamento con ese ID.");
            return false;
        }

        existente.setNombre(nombre);
        existente.setPuesto(puesto);
        existente.setDepartamento(dep);

        guardarFichero();
        return true;
    }

    @Override
    public boolean deleteEmployee(Object id) {
        Employee e = findEmployeeById(id);
        if (e == null) return false;
        empleados.remove(e);
        guardarFichero();
        return true;
    }

    @Override
    public List<Department> findAllDepartments() {
        return departamentos;
    }

    @Override
    public Department findDepartmentById(Object id) {
        int idDep = (Integer) id;
        for (Department d : departamentos) {
            if (d.getDepno() == idDep) return d;
        }
        return null;
    }

    @Override
    public boolean addDepartment(Department department) {
        departamentos.add(department);
        guardarFichero();
        return true;
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

        existente.setNombre(nombre);
        existente.setUbicacion(ubicacion);

        guardarFichero();
        return true;
    }

    @Override
    public boolean deleteDepartment(Object id) {
        Department d = findDepartmentById(id);
        if (d == null) return false;
        departamentos.remove(d);
        guardarFichero();
        return true;
    }

    @Override
    public List<Employee> findEmployeesByDept(Object idDept) {
        int dep = (Integer) idDept;
        List<Employee> lista = new ArrayList<>();
        for (Employee e : empleados) {
            if (e.getDepartamento().getDepno() == dep) {
                lista.add(e);
            }
        }
        return lista;
    }
}
