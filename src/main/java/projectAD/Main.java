package projectAD;

import java.util.List;
import java.util.Scanner;
import projectAD.model.Department;
import projectAD.model.Employee;


// Para cambiar de componente, modifica solo esta línea:
// import projectAD.fichero.Componente;
// import projectAD.postgres.Componente;
import projectAD.orm.Componente;
// import projectAD.db4o.Componente;
// import projectAD.mongo.Componente;


public class Main {

    private static IDAO datos;
    private static int opcionMenu = -1;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        menuDB();
        menuAcciones();
    }

    public static void menuDB() {
        while (opcionMenu < 0 || opcionMenu > 5) {
            System.out.println("\n--- MODELO DE DATOS ---");
            System.out.println("1. Ficheros");
            System.out.println("2. Postgres");
            System.out.println("3. ORM");
            System.out.println("4. db40");
            System.out.println("5. Mongo");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            try {
                opcionMenu = Integer.parseInt(sc.nextLine());
                switch (opcionMenu) {
                    case 1 -> datos = new projectAD.fichero.Componente();
                    case 2 -> datos = new projectAD.postgres.Componente();
                    case 3 -> datos = new projectAD.orm.Componente();
                    case 4 -> datos = new projectAD.db4o.Componente();
                    case 5 -> datos = new projectAD.mongo.Componente();
                    case 0 -> System.out.println("Exit");
                    default -> System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("Error: Introduce un número válido.");
                opcionMenu = -1;
            }
        }
    }

    public static void menuAcciones() {
        if (opcionMenu != 0) {
            opcionMenu = -1;
            while (opcionMenu != 0) {

                System.out.println("\n--- GESTIÓN DE EMPLEADOS ---");
                System.out.println("1. Listar todos los departamentos");
                System.out.println("2. Listar todos los empleados");
                System.out.println("3. Buscar empleado por ID");
                System.out.println("4. Buscar departamento por ID");
                System.out.println("5. Ver empleados de un departamento");
                System.out.println("6. Añadir empleado");
                System.out.println("7. Añadir departamento");
                System.out.println("8. Modificar empleado");
                System.out.println("9. Modificar departamento");
                System.out.println("10. Eliminar empleado");
                System.out.println("11. Eliminar departamento");
                System.out.println("0. Salir");
                System.out.print("Elige una opción: ");

                try {
                    opcionMenu = Integer.parseInt(sc.nextLine());
                    switch (opcionMenu) {
                        case 1 -> listarDepartamentos();
                        case 2 -> listarEmpleados();
                        case 3 -> buscarEmpleado();
                        case 4 -> buscarDepartamento();
                        case 5 -> listarEmpleadosPorDepartamento();
                        case 6 -> agregarEmpleado();
                        case 7 -> agregarDepartamento();
                        case 8 -> modificarEmpleado();
                        case 9 -> modificarDepartamento();
                        case 10 -> borrarEmpleado();
                        case 11 -> borrarDepartamento();
                        case 0 -> System.out.println("Exit");
                        default -> System.out.println("Opción no válida.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: Introduce un número válido.");
                    opcionMenu = -1;
                }
            }
        }
    }

    private static void listarDepartamentos() {
        System.out.println("\n--- LISTA DE DEPARTAMENTOS ---");
        List<Department> lista = datos.findAllDepartments();

        for (Department d : lista) {
            System.out.println("ID: " + d.getDepno() +
                    " | Nombre: " + d.getNombre() +
                    " | Ubicacion: " + d.getUbicacion());
        }
    }

    private static void listarEmpleados() {
        System.out.println("\n--- LISTA DE EMPLEADOS ---");
        List<Employee> lista = datos.findAllEmployees();

        for (Employee e : lista) {
            System.out.println("ID: " + e.getEmpno() +
                    " | Nombre: " + e.getNombre() +
                    " | Puesto: " + e.getPuesto()+
                    " | Departamento: "+ e.getDepartamento().getNombre());
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

    private static void buscarDepartamento() {
        System.out.print("Introduce el ID del departamento: ");
        int id = Integer.parseInt(sc.nextLine());
        Department d = datos.findDepartmentById(id);

        if (d != null) {
            System.out.println("Departamento encontrado: " + d.getNombre() +
                    " | Ubicacion: " + d.getUbicacion());
        } else {
            System.out.println("No existe ningún departamento con ese ID.");
        }
    }

    private static void listarEmpleadosPorDepartamento() {
        System.out.print("Introduce el ID del departamento: ");
        int idDep = Integer.parseInt(sc.nextLine());
        System.out.println("\n--- EMPLEADOS DEL DEPARTAMENTO " + idDep + " ---");

        List<Employee> lista = datos.findEmployeesByDept(idDep);
        for (Employee e : lista) {
            System.out.println("- " + e.getNombre() + " [" + e.getPuesto() + "]");
        }
    }

    private static void agregarEmpleado() {
        System.out.print("Introduce el ID del nuevo empleado: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("Introduce el nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Introduce el puesto: ");
        String puesto = sc.nextLine();

        System.out.print("Introduce el ID del departamento (0 si ninguno): ");
        int depno = Integer.parseInt(sc.nextLine());

        Department dep = null;
        if (depno != 0) {
            dep = datos.findDepartmentById(depno);
            if (dep == null) {
                System.out.println("El departamento no existe. Se creará el empleado sin departamento.");
            }
        }

        Employee nuevo = new Employee(id, nombre, puesto, dep);

        if (datos.addEmployee(nuevo)) {
            System.out.println("Empleado añadido correctamente.");
        } else {
            System.out.println("No se pudo añadir el empleado.");
        }
    }

    private static void agregarDepartamento() {
        System.out.print("Introduce el ID del nuevo departamento: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("Introduce el nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Introduce la ubicación: ");
        String ubicacion = sc.nextLine();

        Department nuevo = new Department(id, nombre, ubicacion);

        if (datos.addDepartment(nuevo)) {
            System.out.println("Departamento añadido correctamente.");
        } else {
            System.out.println("No se pudo añadir el departamento.");
        }
    }

    private static void modificarEmpleado() {
        System.out.print("Introduce el ID del empleado a modificar: ");
        int id = Integer.parseInt(sc.nextLine());

        if (datos.updateEmployee(id)) {
            System.out.println("Empleado modificado correctamente.");
        } else {
            System.out.println("No se pudo modificar el empleado.");
        }
    }

    private static void modificarDepartamento() {
        System.out.print("Introduce el ID del departamento a modificar: ");
        int id = Integer.parseInt(sc.nextLine());

        if (datos.updateDepartment(id)) {
            System.out.println("Departamento modificado correctamente.");
        } else {
            System.out.println("No se pudo modificar el departamento.");
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

    private static void borrarDepartamento() {
        System.out.print("Introduce el ID del departamento a borrar: ");
        int id = Integer.parseInt(sc.nextLine());

        if (datos.deleteDepartment(id)) {
            System.out.println("Departamento eliminado correctamente.");
        } else {
            System.out.println("No se pudo eliminar el departamento.");
        }
    }
}