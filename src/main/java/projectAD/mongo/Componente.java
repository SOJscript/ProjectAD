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

    // Establecer conexion
    private MongoClient getConnection() {
        String url = "mongodb://sergi:1234@localhost:27017/?authSource=admin";
        return MongoClients.create(url);
    }

    private MongoDatabase getDatabase(MongoClient client) {
        return client.getDatabase("empresa");
    }

    @Override
    public List<Employee> findAllEmployees() {return List.of();}

    @Override
    public Employee findEmployeeById(Object id) {return null;}

    @Override
    public boolean addEmployee(Employee employee) {
        try (MongoClient conexion = getConnection()) {
            MongoCollection<Document> coleccion = getDatabase(conexion).getCollection("empleado");

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
    public boolean updateEmployee(Object id) {return false;}

    @Override
    public boolean deleteEmployee(Object id) {return false;}

    @Override
    public List<Department> findAllDepartments() {return List.of();}

    @Override
    public Department findDepartmentById(Object id) {return null;}

    @Override
    public boolean addDepartment(Department department) {
        try (MongoClient conexion = getConnection()) {
            MongoCollection<Document> coleccion = getDatabase(conexion).getCollection("departamento");

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
    public boolean updateDepartment(Object id) {return false;}

    @Override
    public boolean deleteDepartment(Object id) {return false;}

    @Override
    public List<Employee> findEmployeesByDept(Object idDept) {return List.of();}
}
