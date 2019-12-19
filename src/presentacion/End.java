package presentacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class End {
    private JPanel panel1;
    private JLabel foto;
    private JTextArea text;
    private JButton inicioButton;
    private JButton cerrarButton;
    private JTextArea Estadisticas;


    public End(Presentation_Controller presentation_controller) {


        panel1.setSize(new Dimension(400, 700));

        System.out.println("hola2");
        String path = presentation_controller.getOutPath();
        text.append(path + "\n");
        text.setEditable(false);

        ImageIcon icon = new ImageIcon(getClass().getResource("egg/resized3.gif"));
        foto.setIcon(icon);

        Estadisticas.setEditable(false);
        Estadisticas.append("La operación ha tardado " + presentation_controller.getTime() +"milisegundos\n");
        if (presentation_controller.getAction() == 0) {
            Estadisticas.append("y el ratio de compresión ha sido de " + presentation_controller.getRatio());
        }




        inicioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presentation_controller.setVariables();
                presentation_controller.switchToWelcome();


            }
        });
        cerrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presentation_controller.close();
            }
        });
    }

    public JPanel getPanel1() {
        return panel1;
    }
}
