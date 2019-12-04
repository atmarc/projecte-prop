package presentacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Normalizer;

public class Formprova {


    private JPanel panel1;
    private JTextField textField1;
    private JButton button1;
    private Presentation_Controller presentation_controller;

    public Formprova(Presentation_Controller presentation_controller) {
        this.presentation_controller = presentation_controller;
        textField1.setText("Selecciona un .txt o .ppm");

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.showOpenDialog(panel1);
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                String path = fc.getSelectedFile().toString();
                textField1.setText(path);
                presentation_controller.sendPath(path);
            }
        });
    }

    public JPanel getPanel1() {
        return panel1;
    }
}
