import dominio.Domain_Controller;

class TEST {
    public static void main(String[] args) throws Exception {
        System.out.println("Good To Go.");
        Domain_Controller DC = new Domain_Controller();
        // DC.compress("auto/normal", "auto/normal.egg", 2);
        // DC.compress("auto/1TB.txt", "auto/1TB.egg", 2);
        // DC.compress("auto/tt", "auto/tt.egg", 2);
        // DC.decompress("auto/normal.egg", "auto/normal_dec");
        DC.decompress("auto/tt.egg", "auto/tt_dec");
        
    }
}