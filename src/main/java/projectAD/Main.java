package projectAD;

import java.util.Scanner;

// Para cambiar de componente, modifica solo esta línea:
import projectAD.fichero.Componente;
// import projectAD.postgres.Componente;
// import projectAD.orm.Componente;
// import projectAD.db4o.Componente;
// import projectAD.mongo.Componente;

public class Main {

    private static IDAO datos = new Componente();

    static void main(String[] args) {
        menu();
    }

    public static void menu() {

    }
}
