package presentacion;

import dominio.controladores.Domain_Controller;
import persistencia.Persistence_Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

/*!
 *  \brief      Controladora de la presentación, encargada de gestionar la GUI y el envío de información de entrada a domain controller
 *  \details
 *  \author Nicolas Camerlynck (colaboradores: Marc Amorós, Edgar Perez)
 */

public class Presentation_Controller {

    // He creado esta clase solo para poder tenerla referenciada en las otras controladoras.
    // Aqui se supone que van todos los metodos necesarios para comunicarse tanto con persistencia como con dominio.

    private JFrame frame;

    private Domain_Controller domain_controller;
    private SeleccionarArchivo seleccionarArchivo;
    private MetodoCompresion metodoCompresion;
    private SeleccionarDestino seleccionarDestino;
    private JPEGselect jpeGselect;
    private Welcome welcome;
    private End end;

        private Integer action = 0; // 0 -> Comprimir, 1 -> Descomprimir
    private Integer algorithm; // 0 -> LZ78, 1 -> LZSS, 2 -> LZW, 3 -> JPEG, 4 -> Auto (nomes per txt)
    private boolean carpeta = false;
    private String SourcePath;
    private String OutPath;
    private int JPEGratio = 5;
    private boolean sobreEscribir;

    private int mode; ///< Parametro de control para la visualizacion final. (1 -> compresion de un .txt ||| 2 -> compresion de un ppm |||  -1 -> descompresion txt ||| -2 -> descompresion ppm)

    /**
     * Creadora de la clase Presentation controller, crea la referencia a donaim controller.
     */
    public Presentation_Controller () {
        this.domain_controller = new Domain_Controller();
        domain_controller.setPresentation_controller(this);
        Persistence_Controller persistence_controller = Persistence_Controller.getPersistence_controller();
        domain_controller.setPersistence_controller(persistence_controller);
    }

    /**
     * Crea la instancia de Jframe que usaremos, y la configura para que el form displayeado sea Welcome, que es el
     * primero que se tiene que ver. A parte define el action Listener para cuando aprietas la cruz de la ventana.
     */
    public void initializeInterface() {
        welcome = new Welcome(this);
        frame = new JFrame("Egg");
        frame.setContentPane(welcome.getPanel1());
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                deleteTemp();
                super.windowClosing(e);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            }
        });
    }

    /**
     * Cambia el form mostrado por el form MetodoCompresion
     */
    public void switchToMetodoCompresion () {

        metodoCompresion = new MetodoCompresion(this);
        frame.setVisible(false);
        frame.setContentPane(metodoCompresion.getPanel1());
        frame.setVisible(true);
    }


    /**
     * Cambia el form mostrado por el form SeleccionarDestino
     */
    public void switchToSeleccionarDestino() {

        seleccionarDestino = new SeleccionarDestino(this);
        frame.setVisible(false);
        frame.setContentPane(seleccionarDestino.getPanel1());
        frame.setVisible(true);
    }

    /**
     * Cambia el form mostrado por el form JPEGSelect
     */
    public void switchToJPEGselect() {

        jpeGselect = new JPEGselect(this);

        frame.setVisible(false);
        frame.setContentPane(jpeGselect.getPanel1());
        frame.setVisible(true);
    }

    /**
     * Cambia el form mostrado por el form SeleccionarArchivo
     */
    public void switchToSeleccionarArchivo() {

        seleccionarArchivo = new SeleccionarArchivo(this);
        frame.setVisible(false);
        frame.setContentPane(seleccionarArchivo.getPanel1());
        frame.setVisible(true);

    }

    /**
     * Cambia el form mostrado por el form End
     */
    public void switchToEnd() {

        end = new End(this);
        frame.setVisible(false);
        frame.setContentPane(end.getPanel1());
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Cambia el form mostrado por el form Welcome
     */
    public void switchToWelcome() {
        frame.setVisible(false);
        frame.setContentPane(welcome.getPanel1());
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Llama al método isfolder de domain controller.
     * @param path ruta hasta el archivo
     * @return true si el path es una carpeta, false si no lo es
     */
    public boolean isFolder(String path) {
        return domain_controller.isFolder(path);
    }

    /**
     * Borra la carpeta temp y cierra el frame
     */
    public void close() {
        deleteTemp();
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Función que llama a los métodos de compresión o decompresión correspondientes de domain controlelr
     * con la información introducid por el usuario en la GUI.
     * @throws Exception Excepción que salta cuando compress o decompress lanzan una excepción, y se trata en SeleccionarDestino
     */
    public void sendInfo() throws Exception {

        System.out.println("El path de salida que llega a sendInfo es: " + OutPath); // DEBUG

        //Comprimir
        if (action == 0) {
            if (carpeta) domain_controller.compress(SourcePath, OutPath + ".egg", sobreEscribir);

            else if (algorithm == 3)  {
                domain_controller.compress(SourcePath, OutPath + ".egg", 3, (byte) JPEGratio, sobreEscribir);
            }
            else if (algorithm == 4) {
                domain_controller.compress(SourcePath, OutPath + ".egg", sobreEscribir);
            }

            else domain_controller.compress(SourcePath, OutPath + ".egg", algorithm, sobreEscribir);
        }
        //Descomprimir
        else domain_controller.decompress(SourcePath, OutPath, sobreEscribir);

        switchToEnd();
    }

    /**
     * Función que llama el método visualiceFile de domain controller
     * @param path Ruta hasta el archivo
     */
    public void visualizeFile(String path) {
        domain_controller.visualiceFile(path);
    }

    /**
     * Función que llama el método decompress de domain controller con los parámetros correspondientes
     * @param out
     * @param in
     * @param sobreEscribir
     */
    public void decompress (String out, String in, boolean sobreEscribir) {
        try {
            domain_controller.decompress(out, in, sobreEscribir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Borra la carpeta temporal Temp
     */
    public void deleteTemp() {
        domain_controller.deleteFile("temp/decompressed.ppm");
        domain_controller.deleteFile("temp/decompressed.txt");
    }

//    /**
//     *
//     * @param label
//     */
//    public void setFrame(Label label) {
//        frame.getContentPane().add(label);
//    }


    public int getAction() {
        return action;
    }

    public String getSourcePath() {
        return SourcePath;
    }

    public String getOutPath() {
        return OutPath;
    }

    public boolean getSobreEscribir() {
        return sobreEscribir;
    }

    public String getNameNE(String path) {
        return domain_controller.getNameNE(path);
    }

    public int getAlgorithm() {
        return algorithm;
    }

    public boolean getIsFolder() {
        return carpeta;
    }

    public long getTime() {
        return domain_controller.getTime();
    }

    public double getRatio() {
        return domain_controller.getRatio();
    }

    public int getMode() {
        return mode;
    }

    public void setAction(int a) {
        action = a;
    }

    public void setAlgorithm(int a) {
        algorithm = a;
    }

    public void setCarpeta() {
        carpeta = true;
    }

    public void setOutPath(String path) {
        System.out.println(path);
        OutPath = path;
    }

    public void setJPEGratio(int a) {
        JPEGratio = a;
    }

    public void setVariables() {
        sobreEscribir = false;
        carpeta = false;
    }

    public void setSobreEscribir(boolean a) {
        sobreEscribir = a;
    }

    public void setMode(int m) {
        mode = m;
    }

    public void setSourcePath(String s) {
        SourcePath = s;
    }

}
