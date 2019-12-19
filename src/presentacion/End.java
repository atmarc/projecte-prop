package presentacion;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class End {
    private JPanel panel1;
    private JLabel foto;
    private JTextArea text;
    private JButton inicioButton;


    public End(Presentation_Controller presentation_controller) {

        System.out.println("hola2");
        String path = presentation_controller.getOutPath();
        text.append(path);
        text.setEditable(false);

        ImageIcon icon = new ImageIcon(getClass().getResource("egg/resized3.gif"));
        foto.setIcon(icon);

        inicioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presentation_controller.setVariables();
                presentation_controller.switchToWelcome();

            }
        });
    }

    public JPanel getPanel1() {
        return panel1;
    }
}
