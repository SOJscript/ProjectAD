# Componente db4o

Implementación de la interfaz `IDAO` sobre **db4o**, una base de datos orientada a objetos embebida. El componente persiste directamente las instancias de `Employee` y `Department` en un único fichero binario, sin SQL ni mapeo objeto-relacional.

## 1. Configuración y apertura

```java
private static final String DB_FILE = "empresa.db";
private final ObjectContainer db;

public Componente() {
    EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
    config.common().objectClass(Employee.class).objectField("empno").indexed(true);
    config.common().objectClass(Department.class).objectField("depno").indexed(true);
    db = Db4oEmbedded.openFile(config, DB_FILE);
}
```

El **`ObjectContainer`** es el equivalente a una conexión en JDBC: representa la base de datos abierta y a través de él se realizan todas las operaciones. Se declara `final` y se inicializa en el constructor, de modo que existe un único contenedor durante toda la vida del componente.

db4o **rastrea** los objetos que devuelve mientras el contenedor permanece abierto: si se recupera un `Employee` y se modifica, una llamada posterior a `store()` sobre ese mismo objeto lo actualiza en lugar de insertar una copia. Si se abriera y cerrara el contenedor en cada método, esa identidad se perdería y las modificaciones provocarían duplicados.

La apertura se realiza en el **constructor** porque la interfaz `IDAO` no declara métodos de ciclo de vida (`abrirConexion`/`cerrarConexion`) y la clase `Main` se limita a instanciar el componente. Colocar `openFile` en el constructor garantiza que la conexión queda lista en el mismo momento en que se selecciona la opción de db4o en el menú.

Antes de abrir el fichero se definen **índices** sobre los campos `empno` y `depno`. Estos índices aceleran significativamente las búsquedas por identificador (`findEmployeeById`, `findDepartmentById`, `findEmployeesByDept`), que de otro modo recorrerían toda la extensión de la clase. Los índices deben configurarse **antes** de `openFile`; si el fichero ya existía sin índices, debe eliminarse para que tengan efecto.

## 2. Helpers de escritura

```java
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
```

Toda escritura en db4o consta de dos pasos: la operación (`store` o `delete`) y la confirmación de la transacción (`commit`). Encapsular ambos en helpers evita la repetición en los nueve métodos que escriben en la base y deja el flujo de cada método CRUD reducido a una única línea de retorno.

Cada `commit()` confirma los cambios al fichero, lo que garantiza la persistencia entre ejecuciones sin necesidad de un cierre explícito del contenedor.

## 3. Operaciones sobre `Employee`

### `findAllEmployees`

```java
return db.query(new Predicate<Employee>() {
    @Override public boolean match(Employee emp) { return true; }
});
```

Se utiliza una *Native Query* con un `Predicate` que acepta todos los objetos. Se eligió `Predicate` en lugar de *Query By Example* (`queryByExample(Employee.class)`) para mantener consistencia con el resto de consultas: todas las búsquedas del componente siguen el mismo patrón.

`Predicate` es una **clase abstracta** (no una interfaz funcional), por lo que se instancia como clase anónima; no admite lambda. Las variables capturadas en `match` deben ser finales o efectivamente finales, motivo por el que en los métodos siguientes el identificador se copia a una variable local antes del `query`.

### `findEmployeeById`

```java
int empno = (int) id;
List<Employee> result = db.query(new Predicate<Employee>() {
    @Override public boolean match(Employee emp) { return emp.getEmpno() == empno; }
});
return result.isEmpty() ? null : result.get(0);
```

La API de db4o no ofrece un método "buscar único": toda consulta devuelve una lista. Como `empno` es único, la lista contendrá 0 o 1 elementos, y el patrón ternario final convierte ese resultado en `Employee` o `null`, ajustándose a la firma de `IDAO`.

Se descartó *Query By Example* porque QBE ignora los campos con valor por defecto, y el valor por defecto de un `int` es `0`: una búsqueda con id `0` devolvería toda la extensión de la clase en lugar de un conjunto vacío. `Predicate` elimina esa ambigüedad.

### `addEmployee`

```java
if (findEmployeeById(employee.getEmpno()) != null) {
    System.err.println("Ya existe un empleado con ID " + employee.getEmpno());
    return false;
}
if (employee.getDepartamento() != null) {
    int depno = employee.getDepartamento().getDepno();
    Department dep = findDepartmentById(depno);
    if (dep == null) {
        System.err.println("El departamento asociado no existe");
        return false;
    }
    employee.setDepartamento(dep);
}
return guardar(employee);
```

El método realiza dos validaciones que en un modelo relacional aportarían el motor:

La primera verifica que **no exista ya** un empleado con el mismo `empno`. db4o no impone unicidad sobre ningún campo (la identidad es de objeto, no de valor), por lo que esta comprobación es responsabilidad del componente.

La segunda comprueba que el departamento referenciado **existe en la base** y, si existe, reemplaza la instancia recibida por la recuperada del contenedor. Este reemplazo es crítico: si se guardara el `Employee` con un `Department` creado en memoria que no proviene de db4o, el motor lo trataría como un objeto nuevo y duplicaría el departamento.

### `updateEmployee`

```java
Employee empleado = findEmployeeById(id);
if (empleado == null) { ... return false; }
// lectura interactiva de los nuevos valores
empleado.setNombre(nombre);
empleado.setPuesto(puesto);
if (depno != 0) {
    Department nuevoDep = findDepartmentById(depno);
    empleado.setDepartamento(nuevoDep);  // null si no existe
} else {
    empleado.setDepartamento(null);
}
return guardar(empleado);
```

La firma `updateEmployee(Object id)` solo recibe el identificador, por lo que los nuevos valores se leen por consola dentro del método, replicando el patrón del componente de Postgres.

La actualización se realiza sobre el **mismo objeto** devuelto por `findEmployeeById`. Como ese objeto es la instancia rastreada por el contenedor, `db.store(empleado)` ejecuta una actualización in-situ; no se crea un objeto nuevo ni se duplica nada.

### `deleteEmployee`

```java
Employee emp = findEmployeeById(id);
if (emp == null) return false;
return borrar(emp);
```

Localización, comprobación de existencia y borrado a través del helper. El `commit` interno consolida la eliminación.

## 4. Operaciones sobre `Department`

`findAllDepartments`, `findDepartmentById`, `addDepartment` y `updateDepartment` siguen exactamente la misma estructura que sus equivalentes de `Employee`: native query con `Predicate`, validación de duplicados en el alta, modificación sobre el objeto rastreado en la actualización. No requieren explicación adicional. El único método específico es el borrado.

### `deleteDepartment` — integridad referencial manual

```java
Department dept = findDepartmentById(id);
if (dept == null) return false;

List<Employee> empleados = findEmployeesByDept(id);
if (!empleados.isEmpty()) {
    System.out.println("No se puede borrar el departamento porque tiene empleados asignados.");
    return false;
}
return borrar(dept);
```

En PostgreSQL la restricción `fk_employee_dpt` impide borrar un departamento referenciado por algún empleado. **db4o no implementa claves foráneas ni borrados en cascada**: si se eliminara el departamento sin más, los empleados que lo referencian quedarían apuntando a un objeto inexistente, y un acceso posterior del tipo `empleado.getDepartamento().getNombre()` lanzaría `NullPointerException`.

Para preservar la integridad del modelo se replica manualmente esa restricción: antes de borrar se consulta `findEmployeesByDept`, y si existe al menos un empleado asignado, el borrado se rechaza.

## 5. Consulta relacional `findEmployeesByDept`

```java
int depno = (int) idDept;
return db.query(new Predicate<Employee>() {
    @Override public boolean match(Employee emp) {
        return emp.getDepartamento() != null && emp.getDepartamento().getDepno() == depno;
    }
});
```

La consulta navega la referencia `Employee → Department` y filtra por `depno`. La comprobación de `!= null` es necesaria porque un empleado puede no tener departamento asignado (a diferencia del modelo SQL, donde el campo puede ser `NULL` y el filtro `WHERE depno = ?` ya lo descarta).

Este método es además el que da soporte a la integridad referencial manual descrita en el apartado anterior.

## Decisiones de diseño — síntesis

| Decisión | Motivo |
|---|---|
| Un único `ObjectContainer` abierto en el constructor | Permite que db4o rastree los objetos y `store()` actualice sin duplicar |
| `Predicate` en todas las consultas | Evita la ambigüedad de QBE con el valor por defecto `0` de `int` |
| Helpers `guardar`/`borrar` | Centralizan `store/delete + commit`, eliminan repetición |
| Índices sobre `empno` y `depno` | Aceleran todas las búsquedas por identificador |
| Validación de duplicados en altas | Suple la ausencia de claves primarias en db4o |
| Reemplazo del `Department` por la instancia rastreada | Evita duplicar departamentos al guardar empleados |
| Comprobación de empleados antes de borrar departamento | Suple la ausencia de claves foráneas en db4o |

## Persistencia y ejecución

Los datos persisten en el fichero `empresa.db`, generado automáticamente en el directorio de trabajo. Cada operación de escritura confirma la transacción mediante `commit()`, por lo que el estado se mantiene entre ejecuciones. Para reiniciar la base basta con eliminar el fichero. db4o bloquea el fichero a un único proceso, por lo que no pueden ejecutarse simultáneamente `Main` y `poblarBdDb4o` sobre el mismo `empresa.db`.
