package presentacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JPEGselect {
    private JPanel panel1;
    private JSlider slider1;
    private JButton OKButton;
    private JTextArea seleccioneElRadioDeTextArea;
    private JButton Back;

    public JPEGselect(Presentation_Controller presentation_controller) {

        panel1.setPreferredSize(new Dimension(700, 400));

        seleccioneElRadioDeTextArea.setEditable(false);

        slider1.setPaintTicks(true);
        slider1.setPaintLabels(true);
        slider1.setPaintTrack(true);

        slider1.setMajorTickSpacing(1);
        slider1.setMinorTickSpacing(1);


        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //presentation_controller.sendJPEGvalue(slider1.getValue());
                presentation_controller.setJPEGratio(slider1.getValue());
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


