package presentacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import javax.swing.JOptionPane;

public class SeleccionarDestino {
    private JPanel panel1;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private JTextArea SELECCIONELACARPETADONDETextArea;
    private JTextField textField1;
    private JButton browseButton;
    private JButton OK;
    private JCheckBox sobreescribirCheckBox;
    private JButton Back;

    private String path;


    public SeleccionarDestino(Presentation_Controller presentation_controller) {

        panel1.setPreferredSize(new Dimension(700, 400));

        sobreescribirCheckBox.setSelected(false);

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
        OK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (radioButton1.isSelected()) {
                    path = presentation_controller.getSourcePath();
                    path = path.substring(0, path.length() - 4);
                    path = path + ".egg";
                    System.out.println(path);
                    presentation_controller.setOutPath(path);
                }
                else {
                    path = presentation_controller.getSourcePath();
                    //if (presentation_controller.get)
                    path = presentation_controller.getNameNE(path);
                    System.out.println(path);
                    String auxpath = textField1.getText();
                    presentation_controller.setOutPath(auxpath + "\\" + path + ".egg");

                }

                try {
                    presentation_controller.sendInfo();
                }
                catch (FileAlreadyExistsException exp) {
                    String message = "Ya existe un archivo con este nombre";
                    JOptionPane.showMessageDialog(null,message);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }


            }
        });
        sobreescribirCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sobreescribirCheckBox.isSelected()) {
                    presentation_controller.setSobreEscribir(true);
                }
                else {
                    presentation_controller.setSobreEscribir(false);
                }
            }
        });
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (presentation_controller.getAlgorithm() != 3) {
                    presentation_controller.switchToMetodoCompresion();
                }
                else {
                    presentation_controller.switchToJPEGselect();
                }
            }
        });
    }

    public JPanel getPanel1() {
        return panel1;
    }
}
