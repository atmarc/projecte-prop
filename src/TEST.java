import dominio.Domain_Controller;

class TEST {
    public static void main(String[] args) throws Exception {
        System.out.println("Good To Go.");
        Domain_Controller DC = new Domain_Controller();
//        DC.compress("auto/1TB", "auto/1TB.egg", 1);
//        DC.compress("auto/1TB.txt", "auto/1TB.egg", 2);
//        DC.compress("auto/1TB", "auto/1TB.egg", 2);
//        DC.decompress("auto/1TB.egg", "auto/1TB_dec");
//        DC.decompress("auto/1TB.egg", "auto/1TB_dec");
//        DC.compress("auto/KO", "auto/KO.egg", 0);
//        DC.decompress("auto/KO.egg", "auto/KO_dec");
        DC.compress("auto/1TB.txt", "auto/1TB.egg", 0);
        DC.decompress("auto/1TB.egg", "auto/1TB_dec.txt");
//        byte[] arr = new byte[]{0, 0, 1, -29};
//        System.out.println(DC.toLong(arr));
    }
}