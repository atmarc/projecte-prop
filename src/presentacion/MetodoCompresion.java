package presentacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MetodoCompresion {
    private JPanel panel1;
    private JButton automaticButton;
    private JTextField escogeremosElMejorAlgoritmoTextField;
    private JButton LZ78Button;
    private JButton LZWButton;
    private JTextArea MÉTODODECOMPRESIÓNTextArea;
    private JButton LZSSButton;
    private JButton Back;

    public MetodoCompresion(Presentation_Controller presentation_controller) {

        panel1.setPreferredSize(new Dimension(700, 400));

        escogeremosElMejorAlgoritmoTextField.setEditable(false);
        MÉTODODECOMPRESIÓNTextArea.setEditable(false);

        LZ78Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presentation_controller.setAlgorithm(0);
                presentation_controller.switchToSeleccionarDestino();
            }
        });
        LZWButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presentation_controller.setAlgorithm(2);
                presentation_controller.switchToSeleccionarDestino();
            }
        });
        LZSSButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                presentation_controller.setAlgorithm(1);

                presentation_controller.switchToSeleccionarDestino();
            }
        });
        automaticButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                presentation_controller.setAlgorithm(4);

                presentation_controller.switchToSeleccionarDestino();
            }
        });
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presentation_controller.switchToSeleccionarArchivo();
            }
        });
    }

    public JPanel getPanel1() {
        return panel1;
    }
}


