package presentacion;

import javax.swing.*;

public class Loading {
    private JPanel panel1;
    private JLabel gifLabel;

    public Loading(Presentation_Controller presentation_controller) {
        System.out.println("loading");
        ImageIcon icon = presentation_controller.getIcon();
        gifLabel.setIcon(icon);
    }



    public JPanel getPanel1() {
        return panel1;
    }

}
