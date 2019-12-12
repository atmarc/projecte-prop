import dominio.Domain_Controller;

class TEST {
    public static void main(String[] args) {
        System.out.println("Good To Go.");
        Domain_Controller DC = new Domain_Controller();
        DC.compress("auto/normal", "auto/normal.egg", 2);
        
    }
}