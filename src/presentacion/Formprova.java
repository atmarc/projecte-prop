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
    private JTextPane EGGCOMPRESSORTextPane;
    private JButton OKButton;
    private JTextArea EGGCOMPRESSORTextArea;
    private Presentation_Controller presentation_controller;


    private String path;

    public Formprova(Presentation_Controller presentation_controller) {

        panel1.setPreferredSize(new Dimension(700, 400));

        panel1.setBackground(Color.decode("#F7F7F7"));


        this.presentation_controller = presentation_controller;
        textField1.setText("Selecciona un fichero a comprimir o descomprimir");
        textField1.setEditable(false);

        EGGCOMPRESSORTextArea.setEditable(false);
        EGGCOMPRESSORTextArea.setBackground(Color.decode("#F7F7F7"));

        //button1.setBorderPainted(false);
        //button1.setFocusPainted(false);
        //button1.setContentAreaFilled(false);

        //button1.setBackground(Color.);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.showOpenDialog(panel1);
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                //fc.showSaveDialog(panel1);
                path = fc.getSelectedFile().toString();
                textField1.setText(path);

            }
        });

        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (path.endsWith(".txt")) {
                    presentation_controller.sendPath(path);
                    presentation_controller.switchToMetodoCompresion();
                }
                else if (path != null) {
                    presentation_controller.sendPath(path);
                    presentation_controller.switchToJPEGselect();
                }
                else {

                }

            }
        });
    }

    public JPanel getPanel1() {
        return panel1;
    }
}
