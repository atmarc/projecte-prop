package dominio;


public class PairInt<L,R> {
    private Integer l;
    private Integer r;
    public PairInt(Integer l, Integer r){
        this.l = l;
        this.r = r;
    }
    public Integer getFirst(){ return l; }
    public Integer getSecond(){ return r; }
    public void setL(Integer l){ this.l = l; }
    public void setR(Integer r){ this.r = r; }
    public void incR() { this.r++; }
}
