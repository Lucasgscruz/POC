//package menorCaminho;

import java.util.ArrayList;


public class Vertice {
    private int id;
    private ArrayList<Vertice> vizinhos;
    
    public Vertice(int id){
        this.id = id;
        this.vizinhos = new ArrayList<>();
    }
    
    public int getId(){
        return this.id;
    }
    
    public ArrayList<Vertice> getVizinhos(){
        return this.vizinhos;
    }
    
    public void addNeighbor(Vertice viz){
        vizinhos.add(viz);
    }
}
