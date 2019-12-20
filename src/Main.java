import presentacion.Presentation_Controller;

public class Main {
    public static void main(String[] args) throws Exception {

        if (args.length > 0) {
            ConsoleMain.main(null);
            return;
        }

        Presentation_Controller PC =  new Presentation_Controller();
        PC.initializeInterface();
    }
}

