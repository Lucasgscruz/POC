//package menorCaminho;
import java.util.ArrayList;

public class Rota implements Comparable<Rota> {
	int origem, destino;
	ArrayList<Integer> vertices = new ArrayList<Integer>();
	double demanda;
	
	public int getOrigem() {
		return origem;
	}
	public void setOrigem(int origem) {
		this.origem = origem;
	}
	public int getDestino() {
		return destino;
	}
	public void setDestino(int destino) {
		this.destino = destino;
	}
	public ArrayList<Integer> getRota() {
		return vertices;
	}
	public void setRota(ArrayList<Integer> rota) {
		this.vertices.clear();
		for(int i=0; i<rota.size(); i++)
			this.vertices.add(rota.get(i));
	}
	public double getDemanda() {
		return demanda;
	}
	public void setDemanda(double demanda) {
		this.demanda = demanda;
	}
    @Override
    public int compareTo(Rota outraRota) {
        if (this.demanda > outraRota.getDemanda()) {
            return -1;
       }
       if (this.demanda < outraRota.getDemanda()) {
            return 1;
       }
       return 0;
    }
//    @Override
//    public Rota clone() throws CloneNotSupportedException {
//        return (Rota) super.clone();
//    }
}
