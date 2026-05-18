package projectAD;

import java.util.List;
import java.util.Scanner;
import projectAD.model.Department;
import projectAD.model.Employee;

// Para cambiar de componente, modifica solo esta línea:
// import projectAD.fichero.Componente;
import projectAD.postgres.Componente;
// import projectAD.orm.Componente;
// import projectAD.db4o.Componente;
// import projectAD.mongo.Componente;

public class Main {

    private static IDAO datos = new Componente();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        menu();
    }

    public static void menu() {
        int opcion = -1;
        while (opcion != 0) {

            System.out.println("\n--- GESTIÓN DE EMPLEADOS ---");
            System.out.println("1. Listar todos los departamentos");
            System.out.println("2. Listar todos los empleados");
            System.out.println("3. Buscar empleado por ID");
            System.out.println("4. Ver empleados de un departamento");
            System.out.println("5. Eliminar empleado");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
                switch (opcion) {
                    case 1 -> listarDepartamentos();
                    case 2 -> listarEmpleados();
                    case 3 -> buscarEmpleado();
                    case 4 -> listarPorDepartamento();
                    case 5 -> borrarEmpleado();
                    case 0 -> System.out.println("Exit");
                    default -> System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("Error: Introduce un número válido.");
                opcion = -1;
            }
        }
    }

    private static void listarDepartamentos() {
        System.out.println("\n--- LISTA DE DEPARTAMENTOS ---");
        List<Department> lista = datos.findAllDepartments();

        for (Department d : lista) {
            System.out.println("ID: " + d.getDepno() +
                    " | Nombre: " + d.getNombre() +
                    " | Sede: " + d.getUbicacion());
        }
    }

    private static void listarEmpleados() {
        System.out.println("\n--- LISTA DE EMPLEADOS ---");
        List<Employee> lista = datos.findAllEmployees();

        for (Employee e : lista) {
            System.out.println("ID: " + e.getEmpno() +
                    " | Nombre: " + e.getNombre() +
                    " | Puesto: " + e.getPuesto());
        }
    }

    private static void buscarEmpleado() {
        System.out.print("Introduce el ID del empleado: ");
        int id = Integer.parseInt(sc.nextLine());
        Employee e = datos.findEmployeeById(id);

        if (e != null) {
            System.out.println("Empleado encontrado: " + e.getNombre() + " (" + e.getPuesto() + ")");
        } else {
            System.out.println("No existe ningún empleado con ese ID.");
        }
    }

    private static void listarPorDepartamento() {
        System.out.print("Introduce el ID del departamento: ");
        int idDep = Integer.parseInt(sc.nextLine());
        System.out.println("\n--- EMPLEADOS DEL DEPARTAMENTO " + idDep + " ---");

        List<Employee> lista = datos.findEmployeesByDept(idDep);
        for (Employee e : lista) {
            System.out.println("- " + e.getNombre() + " [" + e.getPuesto() + "]");
        }
    }

    private static void borrarEmpleado() {
        System.out.print("Introduce el ID del empleado a borrar: ");
        int id = Integer.parseInt(sc.nextLine());

        if (datos.deleteEmployee(id)) {
            System.out.println("Empleado eliminado correctamente.");
        } else {
            System.out.println("No se pudo eliminar el empleado.");
        }
    }
}
