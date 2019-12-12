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

    public MetodoCompresion(Presentation_Controller presentation_controller) {

        panel1.setPreferredSize(new Dimension(700, 400));

        escogeremosElMejorAlgoritmoTextField.setEditable(false);
        MÉTODODECOMPRESIÓNTextArea.setEditable(false);

        LZ78Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presentation_controller.switchToSeleccionarDestino();
            }
        });
        LZWButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presentation_controller.switchToSeleccionarDestino();
            }
        });
        LZSSButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presentation_controller.switchToSeleccionarDestino();
            }
        });
        automaticButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presentation_controller.switchToSeleccionarDestino();
            }
        });
    }

    public JPanel getPanel1() {
        return panel1;
    }
}


