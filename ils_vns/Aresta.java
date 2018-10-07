//package menorCaminho;

public class Aresta {
    private int id, origem, destino, levels;
    private double[] custo;
    private double[] tarifa;
    private double[] capacidade;
    
    public Aresta(int id, int origem, int destino, int levels){
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.levels = levels;
        this.custo = new double[levels];
        this.tarifa = new double[levels];
        this.capacidade = new double[levels];
    }
    
    public int getId(){
        return this.id;
    }
    
    public int getOrigem(){
        return this.origem;
    }
    
    public int getDestino(){
        return this.destino;
    }
    
    public double getCusto(int level){
        return this.custo[level];
    }
    
    public double getTarifa(int level){
        return this.tarifa[level];
    }
    
    public double getCapacidade(int level){
        return this.capacidade[level];
    }
    
    public void setCusto(int level, double value){
        this.custo[level] = value;
    }
    
    public void setTarifa(int level, double value){
        this.tarifa[level] = value;
    }
    
    public void setCapacidade(int level, double value){
        this.capacidade[level] = value;
    }
}
