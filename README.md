# db4o en el proyecto

Guía del componente de persistencia basado en **db4o**, una de las implementaciones intercambiables de la interfaz `IDAO` (junto a Postgres, Mongo y ORM).

## 1. Qué es db4o

db4o (*database for objects*) es una base de datos **orientada a objetos** y **embebida**, escrita en Java. A diferencia de una base relacional como PostgreSQL, db4o guarda los objetos Java directamente (`Employee`, `Department`), sin mapeo objeto-relacional ni SQL: le pides "guarda este objeto" o "tráeme los que cumplan esta condición", y trabaja con tus clases tal cual.

Es embebida, es decir, no necesita servidor, usuario ni contraseña: toda la base de datos vive en un único fichero local que se crea solo. Esto la hace muy cómoda para proyectos pequeños o para entender el paradigma orientado a objetos, que es justo el objetivo aquí: demostrar que una misma interfaz `IDAO` puede implementarse sobre motores radicalmente distintos (relacional, documental y de objetos) sin cambiar el resto de la aplicación.

## 2. Instalación de la librería (JAR)

db4o está descontinuado y **no está disponible de forma fiable en el repositorio central de Maven**, así que no se añade por `pom.xml`: hay que incluir el JAR manualmente. Se usa el archivo "all-java5", que es autocontenido (trae todo db4o en un solo fichero). El sufijo *java5* solo indica que está compilado para Java 5 en adelante; funciona sin problema en cualquier Java moderno.

Pasos en IntelliJ:

1. Copia el JAR dentro del proyecto (por ejemplo en una carpeta `lib/`), para que la dependencia viaje con el proyecto y no dependa de una ruta externa.
2. `File → Project Structure → Libraries → + → Java`, selecciona el JAR y aplícalo al módulo.
3. Comprueba que los `import com.db4o.*;` del componente ya no aparecen marcados como error.

En Eclipse el equivalente es: clic derecho sobre el proyecto → `Build Path → Configure Build Path → Libraries → Add External JARs`.

Importante: el código **no referencia la ruta del JAR** en ningún sitio. La conexión entre el código y la librería son únicamente los `import com.db4o.*` más la llamada a `Db4oEmbedded.openFile(...)`. Lo único que hace falta es que el JAR esté en el *classpath*.

## 3. El fichero de la base de datos

```java
private static final String DB_FILE = "empresa.db";
```

db4o guarda todos los objetos en un único fichero binario, creado automáticamente en el directorio de trabajo del proyecto la primera vez que se abre. Para "empezar de cero" basta con borrar ese fichero: se recreará vacío en el siguiente arranque.

Nota: db4o bloquea el fichero para un solo proceso a la vez. Si dejas otra ejecución abierta apuntando al mismo fichero (por ejemplo el `Main` y a la vez `poblarBdDb4o`), la segunda dará error de fichero bloqueado.

## 4. Apertura de la conexión y los índices

```java
private final ObjectContainer db;

public Componente() {
    EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
    config.common().objectClass(Employee.class).objectField("empno").indexed(true);
    config.common().objectClass(Department.class).objectField("depno").indexed(true);
    db = Db4oEmbedded.openFile(config, DB_FILE);
}
```

El `ObjectContainer` es **el núcleo de la conexión**: representa la base de datos abierta y a través de él se hacen todas las operaciones (guardar, consultar, borrar). Se abre una sola vez en el **constructor** y se reutiliza durante toda la vida del componente.

¿Por qué en el constructor y no en un método `abrirConexion()` aparte? Porque `Main` se limita a hacer `new projectAD.db4o.Componente()` y no llamaría a ningún método de apertura. Poniéndolo en el constructor, la conexión queda lista en el momento en que se elige la opción de db4o en el menú.

Mantener un único contenedor abierto tiene además una ventaja clave: los objetos recuperados quedan **rastreados** por db4o, de modo que al modificarlos y volver a guardarlos se actualizan en lugar de duplicarse (ver sección 6).

Los **índices** sobre `empno` y `depno` aceleran las búsquedas por esos campos (`findEmployeeById`, `findDepartmentById`). Se definen en la configuración **antes** de abrir el fichero. Si el fichero ya existía sin índices, hay que borrarlo y volver a poblar para que los índices tengan efecto.

## 5. Consultas con Predicate

Las consultas usan **Native Queries** con `Predicate`, una clase abstracta de db4o en la que defines la condición de búsqueda en Java puro:

```java
db.query(new Predicate<Employee>() {
    @Override
    public boolean match(Employee emp) {
        return emp.getEmpno() == empno;
    }
});
```

El método `match()` se ejecuta para cada objeto de ese tipo en la base; devuelve `true` si debe incluirse en el resultado. Como `Predicate` es una clase abstracta (no una interfaz funcional), **no se puede usar una lambda**: hay que usar una clase anónima, y las variables capturadas dentro del `match` deben ser finales o efectivamente finales (por eso el id se copia a una variable local antes).

Se eligió `Predicate` en lugar de *Query By Example* (QBE) porque QBE ignora los campos con valor por defecto, y para un `int` el valor por defecto es `0`. Con QBE, buscar por id `0` devolvería *todos* los registros en vez de ninguno. Con `Predicate` se controla la condición exacta y se evita ese comportamiento inesperado.

### El patrón `result.isEmpty() ? null : result.get(0)`

Las consultas en db4o siempre devuelven una **lista**, porque la API no tiene un método "buscar único". En una búsqueda por id (que es único) la lista tendrá 0 o 1 elemento, así que este patrón la convierte en un solo objeto o en `null`:

```java
List<Employee> result = db.query(...);
return result.isEmpty() ? null : result.get(0);
```

## 6. Guardar, actualizar y borrar

Las operaciones de escritura se centralizan en dos *helpers* para no repetir `store`/`commit`/`delete` en cada método:

```java
private boolean guardar(Object obj) { db.store(obj); db.commit(); return true; }
private boolean borrar(Object obj)  { if (obj == null) return false; db.delete(obj); db.commit(); return true; }
```

**Guardar (alta):** `db.store(obj)` seguido de `db.commit()` para confirmar la transacción.

**Actualizar:** se recupera el objeto con `findEmployeeById`/`findDepartmentById`, se modifican sus atributos con los *setters* y se vuelve a llamar a `guardar(obj)` sobre **el mismo objeto**. Como ese objeto viene del contenedor (está rastreado), db4o reconoce que ya existe y lo actualiza en lugar de insertar uno nuevo.

**Borrar:** `db.delete(obj)` + `db.commit()`.

En las altas, el componente comprueba además que no exista ya un objeto con ese id (`addEmployee` y `addDepartment` rechazan duplicados), algo que en Postgres haría la clave primaria pero que aquí hay que verificar a mano.

## 7. Integridad referencial manual

Esta es una de las diferencias importantes respecto a una base relacional. En PostgreSQL, la clave externa `fk_employee_dpt` impide borrar un departamento que tenga empleados asignados. **En db4o no hay restricciones automáticas**, así que esa comprobación se implementa a mano en `deleteDepartment`:

```java
List<Employee> empleados = findEmployeesByDept(id);
if (!empleados.isEmpty()) {
    System.out.println("No se puede borrar el departamento porque tiene empleados asignados.");
    return false;
}
```

Sin esta comprobación, al borrar un departamento los empleados que lo referenciaban quedarían apuntando a un objeto ya eliminado, provocando un `NullPointerException` al hacer, por ejemplo, `empleado.getDepartamento().getNombre()`.

Por el mismo motivo, en las altas y modificaciones de empleados se reutiliza la instancia del departamento que ya está en la base (buscándola con `findDepartmentById`) en lugar de crear una nueva, para no acabar con departamentos duplicados.

## 8. Cómo usarlo

1. Ejecuta `Main` y elige la opción de db4o en el menú de modelos de datos. La primera vez se crea el fichero de base de datos automáticamente.
2. Da de alta **primero los departamentos** y luego los empleados, porque al añadir un empleado se le pide el id de su departamento y este debe existir ya.
3. Recorre el resto del menú (listar, buscar por id, ver empleados de un departamento, modificar, borrar) para validar las operaciones.

Como cada alta, modificación y borrado hace `commit()`, los datos persisten entre ejecuciones: al volver a abrir el programa y elegir db4o, los empleados y departamentos siguen ahí.

## 9. Resumen

| Acción | Código |
|---|---|
| Abrir BD con índices | `Db4oEmbedded.openFile(config, "empresa.db")` |
| Consulta por condición | `db.query(new Predicate<T>() { ... })` |
| Extraer único resultado | `lista.isEmpty() ? null : lista.get(0)` |
| Añadir / actualizar | `guardar(obj)` → `store` + `commit` |
| Borrar | `borrar(obj)` → `delete` + `commit` |
| Evitar referencias colgantes | comprobar empleados antes de borrar un departamento |

db4o es una tecnología antigua pero muy útil para entender el paradigma de bases de datos orientadas a objetos, y demuestra cómo un mismo `IDAO` puede implementarse sobre motores muy diferentes (relacional, documental, objeto-embebido) manteniendo idéntica la interfaz.
