//package menorCaminho;

import java.util.ArrayList;

public class Individuo {
	ArrayList<Rota> cromossomo = new ArrayList<Rota>();
	double fitness = Integer.MAX_VALUE;
	double t[][] = null;     // Tabela com o fluxo de veiculos em cada aresta
	int n;   // numero de vertices da rede
	
	public Individuo(int tamanhoRepresentacao, int n){
		this.t = new double[n][n];
		this.n = n;
		for(int i=0; i < tamanhoRepresentacao; i++)
			cromossomo.add(new Rota());
	}

	public ArrayList<Rota> getCromossomo() {
		return cromossomo;
	}

	public void setCromossomo(ArrayList<Rota> cromossomo) {
		this.cromossomo = cromossomo;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public double[][] setFluxo(double demanda){
		for(int i=0; i<cromossomo.size(); i++){
			for(int j=0; j+1 < cromossomo.get(i).getRota().size(); j++){
				this.t[cromossomo.get(i).getRota().get(j)][cromossomo.get(i).getRota().get(j+1)] += 
						demanda;
			}
		}
		return this.t;
	}
	
	public Rota getGene(int indice) {
		return cromossomo.get(indice);
	}
	
	public double[][] getT(){
		return this.t;
	}
	
	public int nivel(int i, int j, double capacidade[][][]){
		if(t[i][j] > capacidade[i][j][2])
			return -1;
		if(t[i][j] > capacidade[i][j][1] && t[i][j] <= capacidade[i][j][2])
			return 2;
		if(t[i][j] > capacidade[i][j][0] && t[i][j] <= capacidade[i][j][1])
			return 1;
		return 0;	
	}	
}
