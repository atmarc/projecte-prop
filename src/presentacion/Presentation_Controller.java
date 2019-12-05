package presentacion;

import dominio.Domain_Controller;
import persistencia.Persistence_Controller;
import javax.swing.*;

public class Presentation_Controller {

    // He creado esta clase solo para poder tenerla referenciada en las otras controladoras.
    // Aqui se supone que van todos los metodos necesarios para comunicarse tanto con persistencia como con dominio.

    private Domain_Controller domain_controller;
    Formprova formprova;
    public Presentation_Controller () {
        formprova = new Formprova(this);
    }

    public void initializeInterface() {
        JFrame frame = new JFrame("Egg");
        frame.setContentPane(formprova.getPanel1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void setDomain_controller(Domain_Controller domain_controller) {
        this.domain_controller = domain_controller;
    }

    public void sendPath(String s) {
        domain_controller.sendPath(s);
    }

}
