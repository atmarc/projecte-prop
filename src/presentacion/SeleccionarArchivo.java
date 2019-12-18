package presentacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Normalizer;

public class SeleccionarArchivo {


    private JPanel panel1;
    private JTextField textField1;
    private JButton button1;
    private JTextPane EGGCOMPRESSORTextPane;
    private JButton OKButton;
    private JTextArea EGGCOMPRESSORTextArea;
    private Presentation_Controller presentation_controller;


    private String path;

    public SeleccionarArchivo(Presentation_Controller presentation_controller) {

        panel1.setPreferredSize(new Dimension(700, 400));

        System.out.println(presentation_controller.getAction());

        if (presentation_controller.getAction() == 0) {
            textField1.setText("Selecciona el fichero o carpeta a comprimir");
        }
        else if (presentation_controller.getAction() == 1){
            textField1.setText("Selecciona el fichero o carpeta a descomprimir");

        }
        textField1.setEditable(false);

        EGGCOMPRESSORTextArea.setEditable(false);

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

                path = fc.getSelectedFile().toString();
                textField1.setText(path);

            }
        });

        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presentation_controller.sendPath(path);

                if (path.endsWith(".txt")) {
                    presentation_controller.switchToMetodoCompresion();
                }
                else if (path.endsWith(".ppm")) {
                    presentation_controller.setAlgorithm(0);
                    presentation_controller.switchToJPEGselect();
                }
                else { //carpeta
                    presentation_controller.setCarpeta();
                    presentation_controller.switchToSeleccionarDestino();

                }


            }
        });
    }

    public JPanel getPanel1() {
        return panel1;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
