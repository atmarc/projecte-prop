import java.util.ArrayList;

public class Compressor_LZ78 extends Compressor {

    private static ArrayList<ArrayList<Pair>> files;    // Conjunto de archivos comprimidos
    private ArrayList<Pair> comp_file;                  // Archivo sobre el que se escribe la compresion actual
    private int next_index;

    Compressor_LZ78() {
        files = new ArrayList<>();
        add_comp_file();
    }

    protected String getExtension() {
        return ".lz78";
    }

    private void add_comp_file() {
        files.add(new ArrayList<>());
        comp_file = files.get(files.size() - 1);
        comp_file.add(new Pair(0, (byte) 0x00));
    }

    protected void compress() {

        int B;
        Tree tree = new Tree(1);
        next_index = 0;
        boolean new_searching = true;

        while ((B = super.readByte()) > 0)
            new_searching = compress((byte) (B & 0xFF), tree, new_searching);

        super.closeReader();

        if (!new_searching) compress((byte) 0x00, tree, false);

        // Set del tamano total del archivo comprimido
        comp_file.set(0, new Pair(comp_file.size() - 1, (byte) 0x00));

        write_compressed_file();
    }

    /**
     * Pre: Si top_search = false, previamente ha habido llamadas a esta funcion, con el mismo tree y top_search = true;
     * Post:
     *  top_search = true -> El byte B esta insertado en el primer nivel del arbol (o bien ya lo estaba, o se ha realizado en esta instancia con indice = comp_file.size()-1). El arbol ahora recuerda el nodo referente a este Byte como ultima visita.
     *  top_search = false -> El byte B esta insertado en el nivel iesimo del arbol, donde i = numero de llamadas previas con top_search = false desde la ultima llamada con top_search = true (numero de Bytes del submote) (o bien ya lo estaba, o se ha realizado en esta instancia con indice = comp_file.size()-1). El arbol ahora recuerda el nodo referente a este Byte como ultima visita.
     *
     * Parametros:
     * @param B Byte que se quiere comprimir.
     * @param tree Arbol de motes conocidos sobre el que se realizan las inserciones y busquedas.
     * @param top_search Indica si se desea reiniciar la busqueda desde arriba del arbol.
     *
     * @return Retorna el estado en el que se encuentra la compresion.
     *  - true  -> Se ha anadido un mote nuevo. La siguiente entrada empezara a buscar desde arriba en el arbol.
     *  - false -> El mote buscado existe. Se solicita otro byte para continuar buscando en la actual altura del arbol.
     */
    private boolean compress(byte B, Tree tree, boolean top_search) {

        int index = tree.progressive_find(B, top_search);

        if (index == comp_file.size()) {
            comp_file.add(new Pair(next_index, B));
            next_index = 0;
            return true;
        }

        next_index = index;
        return false;
    }

    private void write_compressed_file() {

        int i = 1;
        byte[] buffer = new byte[4];
        int index = comp_file.size();

        buffer[0] = ((byte) ((index & 0xFF000000) >> 24));
        buffer[1] = ((byte) ((index & 0x00FF0000) >> 16));
        buffer[2] = ((byte) ((index & 0x0000FF00) >> 8));
        buffer[3] = ((byte) (index & 0x000000FF));

        writeBytes(buffer);

        buffer = new byte[2];
        for (; i < 128 && i < comp_file.size(); i++) { // 1 + 1 Byte
            buffer[0] = (byte) (comp_file.get(i).index & 0xFF);
            buffer[1] = comp_file.get(i).offset;
            writeBytes(buffer);
        }
        buffer = new byte[3];
        for (; i < 32768 && i < comp_file.size(); i++) {    // 2 + 1 Byte
            index = comp_file.get(i).index;
            buffer[0] = ((byte) ((index & 0x0000FF00) >> 8));
            buffer[1] = ((byte) (index & 0x000000FF));
            buffer[2] = comp_file.get(i).offset;
            writeBytes(buffer);
        }
        buffer = new byte[4];
        for (; i < 8388608 && i < comp_file.size(); i++) { // 3 + 1 Byte
            index = comp_file.get(i).index;
            buffer[0] = ((byte) ((index & 0x00FF0000) >> 16));
            buffer[1] = ((byte) ((index & 0x0000FF00) >> 8));
            buffer[2] = ((byte) (index & 0x000000FF));
            buffer[3] = comp_file.get(i).offset;
            writeBytes(buffer);
        }
        buffer = new byte[5];
        for (; i < comp_file.size(); i++) {                 // 4 + 1 Byte
            index = comp_file.get(i).index;
            buffer[0] = ((byte) ((index & 0xFF000000) >> 24));
            buffer[1] = ((byte) ((index & 0x00FF0000) >> 16));
            buffer[2] = ((byte) ((index & 0x0000FF00) >> 8));
            buffer[3] = ((byte) (index & 0x000000FF));
            buffer[4] = comp_file.get(i).offset;
            writeBytes(buffer);
        }
        closeWriter();
    }











}
