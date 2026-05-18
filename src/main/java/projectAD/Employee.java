package projectAD;

public class Employee {
    private int empno;
    private String nombre;
    private String puesto;
    private Department departamento;

    public Employee() {
    }

    public Employee(int empno, String nombre, String puesto, Department departamento) {
        this.empno = empno;
        this.nombre = nombre;
        this.puesto = puesto;
        this.departamento = departamento;
    }

    // Getters y Setters
    public int getEmpno() { return empno; }
    public void setEmpno(int empno) { this.empno = empno; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    public Department getDepartamento() { return departamento; }
    public void setDepartamento(Department departamento) { this.departamento = departamento; }

    @Override
    public String toString() {
        return "Employee{" + "empno=" + empno + ", nombre='" + nombre + '\'' + ", puesto='" + puesto + '\'' + ", departamento=" + (departamento != null ? departamento.getNombre() : "Ninguno") + '}';
    }
}
