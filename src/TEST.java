import dominio.Domain_Controller;
import persistencia.Persistence_Controller;
import presentacion.Presentation_Controller;

import java.util.Scanner;

class TEST {
    public static void main(String[] args) throws Exception {
        System.out.println("Good To Go.");
       Domain_Controller DC = new Domain_Controller();
//        DC.compress("auto/pseudo_ansi", "auto/pseudo_ansi.egg", 1);
//        DC.compress("auto/pseudo_ansi.txt", "auto/pseudo_ansi.egg", 2);
       DC.compress("auto/1TB.txt", "auto/1TB.egg", 0, true);
       DC.decompress("auto/1TB.egg", "auto/1TB_dec", true);
//        DC.decompress("auto/pseudo_ansi.egg", "auto/pseudo_ansi_dec");
//        DC.compress("auto/pseudo_ansi", "auto/pseudo_ansi.egg", 0);
//        DC.decompress("auto/pseudo_ansi.egg", "auto/pseudo_ansi_dec");
//        DC.compress("auto/pseudo_ansi", "auto/pseudo_ansi.egg");
//        DC.decompress("auto/pseudo_ansi.egg", "auto/pseudo_ansi_dec");
//        byte[] arr = new byte[]{0, 0, 1, -29};
//        System.out.println(DC.toLong(arr));
        // Presentation_Controller PC = new Presentation_Controller();
        // PC.setDomain_controller(new Domain_Controller());
        // PC.initializeInterface();
//        Scanner in = new Scanner(System.in);
//        Domain_Controller DC = new Domain_Controller();
//
//        System.out.println(
//            "Que tarea desea realizar?\n" +
//            "\t [0] - Comprimir archivo.\n" +
//            "\t [1] - Descomprimir archivo.");
//        int action = in.nextInt();
//        if (action == 0)  {
//
//        }
//        else if (action == 1) {
//
//        }
//        else {
//            System.out.println("Esto no es una opcion valida. Elija otra opcion.");
//        }
    }
}