package projectAD.orm;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import projectAD.IDAO;
import projectAD.model.Department;
import projectAD.model.Employee;

public class Componente implements IDAO {

    // Fábrica de EntityManager
    private EntityManagerFactory emf;

    public Componente() {
        // "empresaDepartamento" es el nombre definido en persistence.xml
        emf = Persistence.createEntityManagerFactory("empresaDepartamento");
    }

    @Override
    public List<Employee> findAllEmployees() {
        EntityManager em = emf.createEntityManager();
        // Cosulta sobre objetos no las tablas
        List<Employee> lista = em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
        em.close();
        return lista;
    }

    @Override
    public Employee findEmployeeById(Object id) {
        EntityManager em = emf.createEntityManager();
        Employee e = em.find(Employee.class, (Integer) id); 
        em.close();
        return e;
    }

    @Override
    public boolean addEmployee(Employee employee) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(employee); 
        em.getTransaction().commit();
        em.close();
        return true;
    }

    @Override
    public boolean updateEmployee(Object id) {
        return false; 
    }

    @Override
    public boolean deleteEmployee(Object id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Employee e = em.find(Employee.class, (Integer) id);
        if (e == null) {
            em.getTransaction().rollback();
            em.close();
            return false;
        }
        em.remove(e);
        em.getTransaction().commit();
        em.close();
        return true;
    }

    @Override
    public List<Department> findAllDepartments() {
        EntityManager em = emf.createEntityManager();
        List<Department> lista = em.createQuery("SELECT d FROM Department d", Department.class).getResultList();
        em.close();
        return lista;
    }

    @Override
    public Department findDepartmentById(Object id) {
        EntityManager em = emf.createEntityManager();
        Department d = em.find(Department.class, (Integer) id);
        em.close();
        return d;
    }

    @Override
    public boolean addDepartment(Department department) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(department);
        em.getTransaction().commit();
        em.close();
        return true;
    }

    @Override
    public boolean updateDepartment(Object id) {
        return false;
    }

    @Override
    public boolean deleteDepartment(Object id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Department d = em.find(Department.class, (Integer) id);
        if (d == null) {
            em.getTransaction().rollback();
            em.close();
            return false;
        }
        em.remove(d);
        em.getTransaction().commit();
        em.close();
        return true;
    }

    @Override
    public List<Employee> findEmployeesByDept(Object idDept) {
        EntityManager em = emf.createEntityManager();
        List<Employee> lista = em.createQuery(
                        "SELECT e FROM Employee e WHERE e.departamento.depno = :dep", Employee.class)
                .setParameter("dep", (Integer) idDept)
                .getResultList();
        em.close();
        return lista;
    }
}
