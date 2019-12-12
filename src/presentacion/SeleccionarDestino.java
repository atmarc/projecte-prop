package presentacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class SeleccionarDestino {
    private JPanel panel1;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private JTextArea SELECCIONELACARPETADONDETextArea;
    private JTextField textField1;
    private JButton browseButton;
    private JButton button1;

    private String path;


    public SeleccionarDestino(Presentation_Controller presentation_controller) {

        panel1.setPreferredSize(new Dimension(700, 400));

        SELECCIONELACARPETADONDETextArea.setEditable(false);
        SELECCIONELACARPETADONDETextArea.setForeground(Color.black);

        ButtonGroup group = new ButtonGroup();
        group.add(radioButton1);
        group.add(radioButton2);
        radioButton1.setSelected(true);
        textField1.setVisible(false);
        browseButton.setVisible(false);

        radioButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField1.setText("Selecciona una carpeta");
                textField1.setVisible(true);
                browseButton.setVisible(true);

            }
        });
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.showOpenDialog(panel1);

                path = fc.getSelectedFile().toString();
                textField1.setText(path);
            }
        });
        radioButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField1.setVisible(false);
                browseButton.setVisible(false);
            }
        });
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (radioButton1.isSelected()) {
                    path = presentation_controller.samePath();
                }
                else {
                    path = textField1.getText();
                    presentation_controller.sendPath(path);
                }

                String message = "Tu archivo se ha guardado en " + path;
                JOptionPane.showMessageDialog(null, message);
                

            }
        });
    }

    public JPanel getPanel1() {
        return panel1;
    }
}
