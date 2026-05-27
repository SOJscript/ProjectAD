package projectAD.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import projectAD.IDAO;
import projectAD.model.Department;
import projectAD.model.Employee;

public class Componente implements IDAO {

    private MongoClient conexion;
    private MongoDatabase database;

    public Componente() {
        try {
            String url = "mongodb://sergi:1234@localhost:27017/?authSource=admin";
            this.conexion = MongoClients.create(url);
            this.database = conexion.getDatabase("empresa");
        } catch (Exception e) {
            System.err.println("Error al conectar con MongoDB: " + e.getMessage());
        }
    }

    private MongoDatabase getDatabase() {
        return database;
    }

    @Override
    public List<Employee> findAllEmployees() {
        List<Employee> lista = new ArrayList<>();
        try {
            MongoCollection<Document> coleccion = getDatabase().getCollection("empleado");

            try (MongoCursor<Document> cursor = coleccion.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    Employee empleado = new Employee();
                    empleado.setEmpno(doc.getInteger("empno"));
                    empleado.setNombre(doc.getString("nombre"));
                    empleado.setPuesto(doc.getString("puesto"));

                    Integer depno = doc.getInteger("depno");
                    if (depno != null) {
                        Department dep = findDepartmentById(depno);
                        empleado.setDepartamento(dep);
                    }

                    lista.add(empleado);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al listar empleados: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Employee findEmployeeById(Object id) {
        Employee empleado = null;
        try {
            MongoCollection<Document> coleccion = getDatabase().getCollection("empleado");
            Document doc = coleccion.find(Filters.eq("empno", (Integer) id)).first();

            if (doc != null) {
                empleado = new Employee();
                empleado.setEmpno(doc.getInteger("empno"));
                empleado.setNombre(doc.getString("nombre"));
                empleado.setPuesto(doc.getString("puesto"));
            }
        } catch (Exception e) {
            System.err.println("Error al buscar el empleado: " + e.getMessage());
        }
        return empleado;
    }

    @Override
    public boolean addEmployee(Employee employee) {
        try {
            MongoCollection<Document> coleccion = getDatabase().getCollection("empleado");

            Document doc = new Document("empno", employee.getEmpno())
                    .append("nombre", employee.getNombre())
                    .append("puesto", employee.getPuesto());

            if (employee.getDepartamento() != null) {
                doc.append("depno", employee.getDepartamento().getDepno());
            } else {
                doc.append("depno", null);
            }

            coleccion.insertOne(doc);
            return true;

        } catch (Exception e) {
            System.err.println("Error al añadir empleado: " + e.getMessage());
            return false;
        }
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

        System.out.print("Nuevo ID de departamento (0 si ninguno): ");
        int depno = Integer.parseInt(sc.nextLine());

        try {
            MongoCollection<Document> coleccion = getDatabase().getCollection("empleado");

            Bson filtro = Filters.eq("empno", (Integer) id);
            Bson cambios = Updates.combine(
                    Updates.set("nombre", nombre),
                    Updates.set("puesto", puesto),
                    Updates.set("depno", depno != 0 ? depno : null)
            );

            UpdateResult resultado = coleccion.updateOne(filtro, cambios);
            return resultado.getModifiedCount() > 0;

        } catch (Exception e) {
            System.err.println("Error al actualizar empleado: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteEmployee(Object id) {
        try {
            MongoCollection<Document> coleccion = getDatabase().getCollection("empleado");
            DeleteResult resultado = coleccion.deleteOne(Filters.eq("empno", (Integer) id));
            return resultado.getDeletedCount() > 0;

        } catch (Exception e) {
            System.err.println("Error al eliminar empleado: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Department> findAllDepartments() {
        List<Department> lista = new ArrayList<>();
        try {
            MongoCollection<Document> coleccion = getDatabase().getCollection("departamento");

            try (MongoCursor<Document> cursor = coleccion.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    Department departamento = new Department();
                    departamento.setDepno(doc.getInteger("depno"));
                    departamento.setNombre(doc.getString("nombre"));
                    departamento.setUbicacion(doc.getString("ubicacion"));
                    lista.add(departamento);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al listar los departamentos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Department findDepartmentById(Object id) {
        Department departamento = null;
        try {
            MongoCollection<Document> coleccion = getDatabase().getCollection("departamento");
            Document doc = coleccion.find(Filters.eq("depno", (Integer) id)).first();

            if (doc != null) {
                departamento = new Department();
                departamento.setDepno(doc.getInteger("depno"));
                departamento.setNombre(doc.getString("nombre"));
                departamento.setUbicacion(doc.getString("ubicacion"));
            }
        } catch (Exception e) {
            System.err.println("Error al buscar el departamento: " + e.getMessage());
        }
        return departamento;
    }

    @Override
    public boolean addDepartment(Department department) {
        try {
            MongoCollection<Document> coleccion = getDatabase().getCollection("departamento");

            Document doc = new Document("depno", department.getDepno())
                    .append("nombre", department.getNombre())
                    .append("ubicacion", department.getUbicacion());

            coleccion.insertOne(doc);
            return true;

        } catch (Exception e) {
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

        try {
            MongoCollection<Document> coleccion = getDatabase().getCollection("departamento");

            Bson filtro = Filters.eq("depno", (Integer) id);
            Bson cambios = Updates.combine(
                    Updates.set("nombre", nombre),
                    Updates.set("ubicacion", ubicacion)
            );

            UpdateResult resultado = coleccion.updateOne(filtro, cambios);
            return resultado.getModifiedCount() > 0;

        } catch (Exception e) {
            System.err.println("Error al actualizar departamento: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteDepartment(Object id) {
        try {
            MongoCollection<Document> coleccion = getDatabase().getCollection("departamento");
            DeleteResult resultado = coleccion.deleteOne(Filters.eq("depno", (Integer) id));
            return resultado.getDeletedCount() > 0;

        } catch (Exception e) {
            System.err.println("Error al eliminar departamento: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Employee> findEmployeesByDept(Object idDept) {
        List<Employee> lista = new ArrayList<>();
        try {
            MongoCollection<Document> coleccion = getDatabase().getCollection("empleado");

            try (MongoCursor<Document> cursor = coleccion.find(Filters.eq("depno", (Integer) idDept)).iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    Employee emp = new Employee();
                    emp.setEmpno(doc.getInteger("empno"));
                    emp.setNombre(doc.getString("nombre"));
                    emp.setPuesto(doc.getString("puesto"));
                    lista.add(emp);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al buscar empleados del departamento: " + e.getMessage());
        }
        return lista;
    }
}
