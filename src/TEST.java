import java.nio.file.FileAlreadyExistsException;

import dominio.Domain_Controller;

class TEST {
    public static void main(String[] args) throws FileAlreadyExistsException {
        System.out.println("Good To Go.");
        Domain_Controller DC = new Domain_Controller();
        // DC.compress("auto/normal", "auto/normal.egg", 2);
        // DC.compress("auto/1TB.txt", "auto/1TB.egg", 2);
        // DC.compress("auto/tt", "auto/tt.egg", 2);
        DC.decompress("auto/normal.egg", "normal_dec");
        
    }
}