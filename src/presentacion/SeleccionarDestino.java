package presentacion;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

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
    private JTextField nombreTextField;
    private JTextArea seleccionaLaUbicacionDeTextArea;

    private String path;


    public SeleccionarDestino(Presentation_Controller presentation_controller) {

        panel1.setPreferredSize(new Dimension(800, 600));

        sobreescribirCheckBox.setSelected(false);

        nombreTextField.setVisible(false);

        ButtonGroup group = new ButtonGroup();
        group.add(radioButton1);
        group.add(radioButton2);

        radioButton1.setSelected(true);

        textField1.setEnabled(false);
        browseButton.setEnabled(false);
        seleccionaLaUbicacionDeTextArea.setEnabled(false);

        nombreTextField.setVisible(true);

        String name = presentation_controller.getSourcePath();
        int barra = name.lastIndexOf("/");
        if (barra > 0) name = name.substring(barra + 1);

        int punto = name.lastIndexOf(".");
        if (punto > 0) name = name.substring(0, punto);

        nombreTextField.setText(name);

        // seleccionar otra carpeta
        radioButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField1.setEnabled(true); // browse
                browseButton.setEnabled(true);
                seleccionaLaUbicacionDeTextArea.setEnabled(true);
            }
        });

        // browse de la carpeta
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.showOpenDialog(panel1);

                if (fc.getSelectedFile() != null) {
                    path = fc.getSelectedFile().toString();
                    textField1.setText(path);
                }
            }
        });

        // guardar en la carpeta origen
        radioButton1.addActionListener(new ActionListener() { // GUARDAR EN CARPETA ORIGINAL
            @Override
            public void actionPerformed(ActionEvent e) {
                textField1.setEnabled(false); // browse
                browseButton.setEnabled(false);
                seleccionaLaUbicacionDeTextArea.setEnabled(false);
            }
        });

        // boton DONE
        OK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (radioButton1.isSelected()) {
                    path = presentation_controller.getSourcePath();     // path origen
                    int bar = path.lastIndexOf("/");
                    if (bar > 0) path = path.substring(0, bar);
                }
                else path = textField1.getText();                        // path personalizado


                presentation_controller.setOutPath(path + "/" + nombreTextField.getText());

                try {
                    presentation_controller.sendInfo();
                } catch (FileAlreadyExistsException exp) {
                    String message = "Ya existe un archivo con este nombre. \n Marque la casilla si desea sobreescribirlo.";
                    JOptionPane.showMessageDialog(null, message);
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
                } else {
                    presentation_controller.setSobreEscribir(false);
                }
            }
        });

        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (presentation_controller.getAction() == 0) {
                    if (!presentation_controller.getIsFolder()) {
                        if (presentation_controller.getAlgorithm() != 3) {
                            presentation_controller.switchToMetodoCompresion();
                        } else {
                            presentation_controller.switchToJPEGselect();
                        }
                    } else {
                        presentation_controller.switchToSeleccionarArchivo();
                    }
                } else {
                    presentation_controller.switchToSeleccionarArchivo();
                }
            }
        });

        radioButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public JPanel getPanel1() {
        return panel1;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBackground(new Color(-526345));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(30, 10, 0, 10), -1, -1));
        panel2.setBackground(new Color(-526345));
        panel1.add(panel2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setBackground(new Color(-526345));
        panel2.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        textField1 = new JTextField();
        panel3.add(textField1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        browseButton = new JButton();
        browseButton.setText("Browse");
        panel3.add(browseButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nombreTextField = new JTextField();
        nombreTextField.setText("");
        panel3.add(nombreTextField, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        sobreescribirCheckBox = new JCheckBox();
        sobreescribirCheckBox.setText("Sobreescribir");
        panel3.add(sobreescribirCheckBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        seleccionaLaUbicacionDeTextArea = new JTextArea();
        seleccionaLaUbicacionDeTextArea.setBackground(new Color(-526345));
        seleccionaLaUbicacionDeTextArea.setText("Selecciona la ubicacion de destino:");
        panel3.add(seleccionaLaUbicacionDeTextArea, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 20), null, 0, false));
        final JTextArea textArea1 = new JTextArea();
        textArea1.setBackground(new Color(-526345));
        textArea1.setText("Nombre del archivo comprimido:");
        panel3.add(textArea1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 20), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 10, 0, 10), -1, -1));
        panel4.setBackground(new Color(-526345));
        panel1.add(panel4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        SELECCIONELACARPETADONDETextArea = new JTextArea();
        SELECCIONELACARPETADONDETextArea.setBackground(new Color(-526345));
        SELECCIONELACARPETADONDETextArea.setEditable(false);
        Font SELECCIONELACARPETADONDETextAreaFont = this.$$$getFont$$$("Lato Black", Font.PLAIN, 16, SELECCIONELACARPETADONDETextArea.getFont());
        if (SELECCIONELACARPETADONDETextAreaFont != null)
            SELECCIONELACARPETADONDETextArea.setFont(SELECCIONELACARPETADONDETextAreaFont);
        SELECCIONELACARPETADONDETextArea.setForeground(new Color(-12828863));
        SELECCIONELACARPETADONDETextArea.setText("SELECCIONE LA CARPETA DONDE SE GUARDARA EL ARCHIVO");
        SELECCIONELACARPETADONDETextArea.setWrapStyleWord(false);
        panel4.add(SELECCIONELACARPETADONDETextArea, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel4.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(20, 20), new Dimension(20, 50), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 3, new Insets(0, 10, 10, 10), -1, -1));
        panel5.setBackground(new Color(-526345));
        panel1.add(panel5, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        OK = new JButton();
        OK.setText("Done");
        panel5.add(OK, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel5.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        Back = new JButton();
        Back.setText("Back");
        panel5.add(Back, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 2, new Insets(0, 10, 0, 10), -1, -1));
        panel6.setBackground(new Color(-526345));
        panel1.add(panel6, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        radioButton1 = new JRadioButton();
        radioButton1.setBackground(new Color(-526345));
        radioButton1.setText("Guardar en la carpeta orígen");
        panel6.add(radioButton1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        radioButton2 = new JRadioButton();
        radioButton2.setBackground(new Color(-526345));
        radioButton2.setText("Seleccionar otra carpeta");
        panel6.add(radioButton2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(50, 50), null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel1.add(spacer4, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(50, 50), null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel1.add(spacer5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(50, 50), null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel1.add(spacer6, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(50, 50), null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
