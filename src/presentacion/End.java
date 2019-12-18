package presentacion;

import javax.swing.*;

public class End {
    private JPanel panel1;
    private JLabel foto;
    private JTextArea text;




    public End(Presentation_Controller presentation_controller) {

        System.out.println("hola2");
        String path = presentation_controller.getOutPath();
        text.append(path);
        text.setEditable(false);

        ImageIcon icon = new ImageIcon(getClass().getResource("egg/resized3.gif"));
        foto.setIcon(icon);

    }

    public JPanel getPanel1() {
        return panel1;
    }
}
