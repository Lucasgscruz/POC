//package menorCaminho;

import java.util.ArrayList;

public class Grafo {
    private int n, m, p, l; // p - patamares, L - max postos
    int adj[][];
    double demanda[][];
    
    ArrayList<Vertice> vertices;
    ArrayList<Aresta> arestas;
    
    public Grafo(int n, int p, int l){
        this.n = n;
        this.p = p;
        this.m = 0;
        this.l = l;
        this.adj = new int [n][n];
        this.demanda = new double [n][n];
        this.vertices = new ArrayList<>(n);
        this.arestas = new ArrayList<>();
        
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                adj[i][j] = -1;
    }
    
    public void addVertice(Vertice Vertice){
        this.vertices.add(Vertice);
    }
    
    public void addAresta(Aresta Aresta){
        this.m++;
        this.arestas.add(Aresta);
        this.adj[Aresta.getOrigem()][Aresta.getDestino()] = Aresta.getId();
    }
    
    public void addDemanda(int orig, int dest, double value){
        this.demanda[orig][dest] = value;
    }
    
    public int getN(){
        return this.n;
    }
    
    public int getM(){
        return this.m;
    }
    
    public int getP(){
        return this.p;
    }
    
    public int getL(){
        return this.l;
    }
    
    public int getAdj(int i, int j){
        return this.adj[i][j];
    }
    
    public double getDemanda(int i, int j){
        return this.demanda[i][j];
    }
    
    public ArrayList<Vertice> getVertices(){
        return this.vertices;
    }
    
    public ArrayList<Aresta> getArestas(){
        return this.arestas;
    }
}
