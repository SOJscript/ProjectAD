package projectAD;

public class Department {
    private int depno;
    private String nombre;
    private String ubicacion;

    public Department() {
    }

    public Department(int depno, String nombre, String ubicacion) {
        this.depno = depno;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
    }

    // Getters y Setters
    public int getDepno() { return depno; }
    public void setDepno(int depno) { this.depno = depno; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    @Override
    public String toString() {
        return "Department{" + "depno=" + depno + ", nombre='" + nombre + '\'' + ", ubicacion='" + ubicacion + '\'' + '}';
    }
}
