package presentacion;

import dominio.Domain_Controller;

import javax.swing.*;

public class Presentation_Controller {

    // He creado esta clase solo para poder tenerla referenciada en las otras controladoras.
    // Aqui se supone que van todos los metodos necesarios para comunicarse tanto con persistencia como con dominio.

    private JFrame frame;

    private Domain_Controller domain_controller;
    Formprova formprova;
    MetodoCompresion metodoCompresion;
    SeleccionarDestino seleccionarDestino;
    JPEGselect jpeGselect;
    private String path;
    public Presentation_Controller () {

        formprova = new Formprova(this);
        metodoCompresion = new MetodoCompresion(this);
        seleccionarDestino = new SeleccionarDestino(this);
        jpeGselect = new JPEGselect(this);
    }

    public void initializeInterface() {
        frame = new JFrame("Egg");
        frame.setContentPane(formprova.getPanel1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        /*frame.setContentPane(metodoCompresion.getPanel1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);*/
    }

    public void setDomain_controller(Domain_Controller domain_controller) {
        this.domain_controller = domain_controller;
    }

    public void sendPath(String s) {

        path = s;
        domain_controller.sendPath(s);
    }

    public void sendJPEGvalue(int a) {domain_controller.sendJPEGvalue(a);}

    public void switchToMetodoCompresion () {

        frame.setVisible(false);
        frame = new JFrame("Egg");
        frame.setContentPane(metodoCompresion.getPanel1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void switchToSeleccionarDestino() {
        frame.setVisible(false);
        frame = new JFrame("Egg");
        frame.setContentPane(seleccionarDestino.getPanel1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void switchToJPEGselect() {
        frame.setVisible(false);
        frame = new JFrame("Egg");
        frame.setContentPane(jpeGselect.getPanel1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public String samePath() {
        return path;
    }



}
