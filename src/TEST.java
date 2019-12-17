import dominio.Domain_Controller;

class TEST {
    public static void main(String[] args) throws Exception {
        System.out.println("Good To Go.");
        Domain_Controller DC = new Domain_Controller();
//        DC.compress("auto/pseudo_ansi", "auto/pseudo_ansi.egg", 1);
//        DC.compress("auto/pseudo_ansi.txt", "auto/pseudo_ansi.egg", 2);
//        DC.compress("auto/pseudo_ansi", "auto/pseudo_ansi.egg", 2);
//        DC.decompress("auto/pseudo_ansi.egg", "auto/pseudo_ansi_dec");
//        DC.decompress("auto/pseudo_ansi.egg", "auto/pseudo_ansi_dec");
//        DC.compress("auto/pseudo_ansi", "auto/pseudo_ansi.egg", 0);
//        DC.decompress("auto/pseudo_ansi.egg", "auto/pseudo_ansi_dec");
        DC.compress("auto/pseudo_ansi", "auto/pseudo_ansi.egg");
        DC.decompress("auto/pseudo_ansi.egg", "auto/pseudo_ansi_dec");
//        byte[] arr = new byte[]{0, 0, 1, -29};
//        System.out.println(DC.toLong(arr));
    }
}