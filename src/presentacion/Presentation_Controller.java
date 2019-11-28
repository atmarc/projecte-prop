package presentacion;

import dominio.Domain_Controller;
import persistencia.Persistence_Controller;

public class Presentation_Controller {

    // He creado esta clase solo para poder tenerla referenciada en las otras controladoras.
    // Aqui se supone que van todos los metodos necesarios para comunicarse tanto con persistencia como con dominio.

    private Domain_Controller domain_controller;
    private Persistence_Controller persistence_controller;



}
