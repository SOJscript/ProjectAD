# Componente db4o — Guía de lectura

Implementación de `IDAO` sobre **db4o**, una base de datos de objetos embebida. Persiste directamente las instancias de `Employee` y `Department` en un único fichero binario (`empresa.db`), sin SQL ni mapeo objeto-relacional.

## Cómo está organizado el archivo

El `Componente.java` se lee de arriba abajo en este orden:

1. Constructor: abre la base de datos y configura índices.
2. Helpers privados `guardar` y `borrar`.
3. CRUD de `Employee`.
4. CRUD de `Department`.
5. Consulta relacional `findEmployeesByDept`.

## Apertura de la base de datos

```java
EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
config.common().objectClass(Employee.class).objectField("empno").indexed(true);
config.common().objectClass(Department.class).objectField("depno").indexed(true);
db = Db4oEmbedded.openFile(config, DB_FILE);
```

El `ObjectContainer` es la conexión: se abre **una sola vez en el constructor** y se reutiliza durante toda la vida del componente. Esto permite que db4o rastree los objetos recuperados, de forma que al modificarlos y volver a guardarlos se actualicen en lugar de duplicarse.

Los **índices** sobre `empno` y `depno` aceleran las búsquedas por identificador y se configuran antes de `openFile`.

## Helpers de escritura

```java
guardar(obj) → db.store(obj) + db.commit()
borrar(obj)  → db.delete(obj) + db.commit()
```

Centralizan las dos operaciones de escritura para que cada método CRUD acabe en una sola línea de retorno.

## Patrón de consulta

Todas las consultas usan **Native Queries con `Predicate`**:

```java
db.query(new Predicate<Employee>() {
    @Override public boolean match(Employee emp) { return emp.getEmpno() == empno; }
});
```

Se eligió `Predicate` en lugar de *Query By Example* porque QBE ignora los campos con valor por defecto, y el valor por defecto de un `int` es `0`: una búsqueda por id `0` devolvería todos los registros. Con `Predicate` la condición es explícita.

Como db4o no tiene un método "buscar único", las búsquedas por id devuelven una lista de la que se extrae el primer elemento con `result.isEmpty() ? null : result.get(0)`.

## Suplencia de restricciones relacionales

db4o no tiene claves primarias, foráneas ni borrados en cascada, así que el componente las **simula manualmente**:

**Unicidad:** `addEmployee` y `addDepartment` comprueban con `findById` que no exista ya un objeto con el mismo identificador antes de guardarlo.

**Referencias correctas:** al añadir o modificar un empleado se busca su departamento con `findDepartmentById` y se le asigna esa instancia (la que está en la base), no la recibida. De lo contrario db4o duplicaría el departamento.

**Integridad referencial al borrar:** `deleteDepartment` invoca `findEmployeesByDept` antes de borrar; si hay empleados asignados, rechaza el borrado para evitar referencias colgantes y posibles `NullPointerException`.

## Actualización

`updateEmployee` y `updateDepartment` recuperan el objeto con `findById`, leen los nuevos valores por consola, modifican sus atributos con los *setters* y llaman a `guardar` sobre **ese mismo objeto**. Como está rastreado por el contenedor, db4o lo actualiza in-situ.

## Persistencia y ejecución

Cada operación de escritura confirma la transacción con `commit()`, por lo que los datos persisten entre ejecuciones en `empresa.db`. Para reiniciar la base basta con borrar ese fichero. db4o bloquea el fichero a un único proceso, por lo que `Main` y `poblarBdDb4o` no pueden ejecutarse simultáneamente sobre el mismo `empresa.db`.
