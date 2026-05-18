package projectAD;

import java.util.Scanner;

public class Main {
    //Elegimos cual usar

    private static IDAO datos = new ComponenteFichero();
    // private static IDAO datos = new ComponentePostgres();
    // private static IDAO datos = new ComponenteORM();
    // private static IDAO datos = new ComponenteDb4o();
    // private static IDAO datos = new ComponenteMongo();

    static void main(String[] args) {
        menu();
    }

    public static void menu() {

    }
}

