package projectAD.db4o;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;
import projectAD.IDAO;
import projectAD.model.Department;
import projectAD.model.Employee;
import com.db4o.*;

public class Componente implements IDAO {

    private static final String DB_FILE = "empresa.db";

    private final ObjectContainer db;

    public Componente() {
        EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
        // Índice para empleado.empno
        config.common().objectClass(Employee.class)
                .objectField("empno").indexed(true);
        // Índice para departamento.depno
        config.common().objectClass(Department.class)
                .objectField("depno").indexed(true);
        db = Db4oEmbedded.openFile(config, DB_FILE);
    }

    // Helpers

    private boolean guardar(Object obj) {
        db.store(obj);
        db.commit();
        return true;
    }

    private boolean borrar(Object obj) {
        if (obj == null) return false;
        db.delete(obj);
        db.commit();
        return true;
    }

    //EMPLEADOS

    @Override
    public List<Employee> findAllEmployees() {
        return db.query(new Predicate<Employee>() {
            @Override
            public boolean match(Employee emp) {
                return true;
            }
        });
    }

    @Override
    public Employee findEmployeeById(Object id) {
        int empno = (int) id;

        List<Employee> result = db.query(new Predicate<Employee>() {
            @Override
            public boolean match(Employee emp) {
                return emp.getEmpno() == empno;
            }
        });
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public boolean addEmployee(Employee employee) {
        // comprobar que no exista ya
        if (findEmployeeById(employee.getEmpno()) != null) {
            System.err.println("Ya existe un empleado con ID " + employee.getEmpno());
            return false;
        }
        // si el empleado tiene departamento, asegurar que existe en la BD
        if (employee.getDepartamento() != null) {
            //obtener el ID del departamento del empleado
            int depno = employee.getDepartamento().getDepno();
            //buscar el Departament con ese id
            Department dep = findDepartmentById(depno);
            if (dep == null) {
                System.err.println("El departamento asociado no existe");
                return false;
            }
            employee.setDepartamento(dep);
        }
        return guardar(employee);
    }

    @Override
    public boolean updateEmployee(Object id) {
        Employee empleado = findEmployeeById(id);
        if (empleado == null) {
            System.out.println("No existe ningún empleado con ese ID.");
            return false;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Empleado actual: " + empleado.getNombre() + " (" + empleado.getPuesto() + ")");

        System.out.print("Nuevo nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Nuevo puesto: ");
        String puesto = sc.nextLine();
        System.out.print("Nuevo ID de departamento (0 si ninguno): ");
        int depno = Integer.parseInt(sc.nextLine());

        empleado.setNombre(nombre);
        empleado.setPuesto(puesto);

        if (depno != 0) {
            Department nuevoDep = findDepartmentById(depno);
            if (nuevoDep == null) {
                System.out.println("El departamento no existe. Se dejará sin departamento.");
                empleado.setDepartamento(null);
            } else {
                empleado.setDepartamento(nuevoDep);
            }
        } else {
            empleado.setDepartamento(null);
        }

        return guardar(empleado); //actualiza el objeto existente
    }

    @Override
    public boolean deleteEmployee(Object id) {
        Employee emp = findEmployeeById(id);
        if (emp == null) return false;
        return borrar(emp);
    }



    // DEPARTAMENTOS
    @Override
    public List<Department> findAllDepartments() {
        return db.query(new Predicate<Department>() {
            @Override
            public boolean match(Department dep) {
                return true;
            }
        });
    }

    @Override
    public Department findDepartmentById(Object id) {
        int depno = (int) id;
        List<Department> result = db.query(new Predicate<Department>() {
            @Override
            public boolean match(Department dep) {
                return dep.getDepno() == depno;
            }
        });
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public boolean addDepartment(Department department) {
        if (findDepartmentById(department.getDepno()) != null) {
            System.err.println("Ya existe un departamento con ID " + department.getDepno());
            return false;
        }
        return guardar(department);
    }

    @Override
    public boolean updateDepartment(Object id) {
        Department dept = findDepartmentById(id);
        if (dept == null) {
            System.out.println("No existe ningún departamento con ese ID.");
            return false;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Departamento actual: " + dept.getNombre() + " | " + dept.getUbicacion());

        System.out.print("Nuevo nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Nueva ubicación: ");
        String ubicacion = sc.nextLine();

        dept.setNombre(nombre);
        dept.setUbicacion(ubicacion);

        return guardar(dept);
    }

    @Override
    public boolean deleteDepartment(Object id) {
        Department dept = findDepartmentById(id);
        if (dept == null) return false;

        // Obligatorio: comprobar empleados para evitar referencias colgantes (NullPointerException)
        List<Employee> empleados = findEmployeesByDept(id);
        if (!empleados.isEmpty()) {
            System.out.println("No se puede borrar el departamento porque tiene empleados asignados.");
            return false;
        }
        return borrar(dept);
    }

    @Override
    public List<Employee> findEmployeesByDept(Object idDept) {
        int depno = (int) idDept;
        return db.query(new Predicate<Employee>() {
            @Override
            public boolean match(Employee emp) {
                return emp.getDepartamento() != null && emp.getDepartamento().getDepno() == depno;
            }
        });
    }
}
