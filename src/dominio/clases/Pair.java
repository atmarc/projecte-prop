package dominio.clases;

public class Pair {
    private Integer l;
    private Byte r;
    public Pair(Integer l, Byte r){
        this.l = l;
        this.r = r;
    }
    public Integer getL(){ return l; }
    public Byte getR(){ return r; }
    public void setL(Integer l){ this.l = l; }
    public void setR(Byte r){ this.r = r; }
}