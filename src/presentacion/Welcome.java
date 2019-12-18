package presentacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Welcome {
    private JPanel panel1;
    private JLabel foto;
    private JTextArea bienvenidoAEggCompressorTextArea;
    private JButton comprimirButton;
    private JButton descomprimirButton;
    private JLabel Gif;



    public Welcome(Presentation_Controller presentation_controller) {

        panel1.setPreferredSize(new Dimension(700, 400));

        bienvenidoAEggCompressorTextArea.setEditable(false);

        ImageIcon icon = new ImageIcon(getClass().getResource("egg/resized2.gif"));


        foto.setIcon(icon);


        comprimirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presentation_controller.setAction(0);
                presentation_controller.switchToSeleccionarArchivo();
            }
        });
        descomprimirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presentation_controller.setAction(1);
                presentation_controller.switchToSeleccionarArchivo();
            }
        });
    }

    public JPanel getPanel1() {
        return panel1;
    }
}
