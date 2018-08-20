//package menorCaminho;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Main {
	
	Grafo grafo = null;
	ArrayList<Rota> path = new ArrayList<Rota>(); // Array com as rotas entre todos pares OD
	ArrayList<Vertice> f = new ArrayList<Vertice>(); // Vertices fexados
	
	double fft[][] = null;     // tempo de viagem com pista livre
	double d[][] = null;      // Matriz de custo(link travel time) para as arestas (i,j)
	double custo[][][] = null;      // Matriz de peso(link travel time) para as arestas (i,j)
	double demanda[][]; // Tabela com os fluxos de veiculos entre cada par OD
	double capacidade[][][] = null; // Armazena o fluxo maximo suportado em cada link
	double tariff[][][] = null; // tabela de Pedagios
	double t[][] = null;     // Tabela com o fluxo de veiculos em cada aresta
	double toll[][] = null;  // Pedagios	
	boolean a[] = null;        // Vertices abertos
	double dist[] = null;    // Armazena distancia da origem ao vertice i
	int pred[] = null;    // Armazena antecessor de i no caminho mais curto
	int n, pt, veiculos=0;	
	
	double uMax = 0, aux = 0;
	Scanner ler = new Scanner(System.in);
	
	public Main(String arquivo){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo)));
	
			String line;
			StringTokenizer aux;
			
            System.out.println(reader.readLine() + " "); // # Numero de nós #
            line = reader.readLine();
            System.out.println(line);
            aux = new StringTokenizer(line, " ");
            this.n = Integer.parseInt(aux.nextToken());
            
            System.out.println(reader.readLine() + " "); // # Numero de Patamares #
            line = reader.readLine(); 
            System.out.println(line);
            aux = new StringTokenizer(line, " ");
            pt = Integer.parseInt(aux.nextToken()) + 1;
            
                      
            this.fft = new double[n][n]; // Tabela de free flow time
            this.d = new double[n][n]; // Matriz de peso(link travel time) para as arestas (i,j)
            this.custo = new double[n][n][pt];   // Matriz de peso(link travel time) para as arestas (i,j)
            this.demanda = new double[n][n]; // Tabela com a demanda de veiculos entre cada par OD
            this.capacidade = new double[n][n][n]; // Armazena o fluxo maximo suportado em cada link
            this.tariff = new double[n][n][pt]; // tabela de Pedagios
            this.t = new double[n][n]; 	  // Tabela com o fluxo de veiculos em cada aresta
            this.toll = new double[n][n]; // tabela de Pedagios            
            this.a = new boolean[n];       // Vertices abertos		
        	this.dist = new double[n];    // Armazena distancia da origem ao vertice i
        	this.pred = new int[n];    // Armazena antecessor de i no caminho mais curto
        	        	
            System.out.println(reader.readLine()); // # Custo #
            for (int nivel = 0; nivel < pt; nivel++) {
                for (int i = 0; i < n; i++) {
                    // next line
                    line = reader.readLine();
                    aux = new StringTokenizer(line, " ");
                    for (int j = 0; j < n; j++) {
                        custo[i][j][nivel] = Double.parseDouble(aux.nextToken());
                    }
                }
            }
            
            System.out.println(reader.readLine()); // # t #
            for(int nivel = 0; nivel < pt; nivel++){
                for(int i = 0; i < n; i++){                
                    // next line
                    line = reader.readLine(); 
                    aux = new StringTokenizer(line, " ");            
                    for(int j = 0; j < n; j++){
                        tariff[i][j][nivel] = Double.parseDouble(aux.nextToken());
                    }
                }
            }
        	
            reader.readLine(); // # Limite de pedágios #
            line = reader.readLine(); 
            aux = new StringTokenizer(line, " ");
            int k = Integer.parseInt(aux.nextToken());
            
            grafo = new Grafo(n,pt,k);
            
            reader.readLine(); // # u #   capacidade
            for(int i = 0; i < n; i++){
                // next line
                line = reader.readLine(); 
                aux = new StringTokenizer(line, " ");
                for(int j = 0; j < n; j++){
	                for(int t = 0; t < pt; t++){
                        if (t == 0) 
                        	capacidade[i][j][0] = Double.parseDouble(aux.nextToken());
                        else if(t == 1)
                        	capacidade[i][j][1] = capacidade[i][j][0] * 2;
                        else if(t == 2)
                        	capacidade[i][j][2] = capacidade[i][j][0] * 2.5;
                    }
					if(capacidade[i][j][0] > uMax){
						uMax = capacidade[i][j][0]; //encontrando o link de maior capacidade na rede
					}
					toll[i][j] = 1;
                }
            }
            
            reader.readLine(); // # Demanda #
            for(int i = 0; i < n; i++){                
                // next line
                line = reader.readLine(); 
                aux = new StringTokenizer(line, " ");            
                for(int j = 0; j < n; j++){
                    double d = Double.parseDouble(aux.nextToken());
                    demanda[i][j] = d;
                    veiculos += d;
                }
            }
            reader.close();
		}	 
		catch (IOException e) {
			System.out.println("Erro na leitura do arquivo! " + e.getMessage());
		}
		
		// Cria o grafo com base na matriz capacidade (matriz de adjacencia)
        for(int i = 0; i < n; i++){
            grafo.addVertice(new Vertice(i));
        }
		
        int cont = 0;
        for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if(capacidade[i][j][0] > 0){
					Aresta arco = new Aresta(cont, i, j, pt);
					Vertice no = new Vertice(j);					
					grafo.addAresta(arco);
					grafo.getVertices().get(i).addNeighbor(no);
                    
                    //cont aresta
					cont++;
				}			
			}
		}        
       
		// Exibir Grafo
		System.out.println("Rede:");
		for (int i = 0; i < n; i++) {
			System.out.print("Vertice " + i + " -> ");
			for (int j = 0; j < n; j++) {
				if(grafo.getAdj(i, j) >= 0)
					System.out.print(j + ", ");
			}
			System.out.println("");
		}
		
		aux = ils();
//		ArrayList<Integer> o1 = new ArrayList<Integer>();
//		ArrayList<Integer> o2 = new ArrayList<Integer>();
//		
//		o1.add(22);
//		o2.add(o1.get(0));
//		o2.clear();
//		o2.add(25);
//		o2.add(25);o2.add(25);
//		o1.clear();
//		o1.add(3);
//		
//		System.out.println("o1: "+ o1 + " o2:  "+o2);

//		Solucao o1 = new Solucao(2);
//		Solucao o2=null;
//        try {
// 
//        	o2= o1.clone();
//    
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
		
		// Exibir matriz de link travel time e pedagios após a realização do metodo construtivo
		//showMatriz(d, "\nMatriz link travel time apos realização do metodo construtivo");
		//showMatriz(t, "\nFluxo nas vias");
		//showMatriz(toll, "\nPEDAGIOS");
		System.out.printf("\nFunção Objetivo: %.4f -> %d pedágios\n", aux, contaTolls());
	}


/********************************* Auxiliares *********************************/
	public void showMatriz(double matriz[][], String legenda){
		System.out.println(legenda);
		for (int i = 0; i < n; i++) {
			System.out.printf(i + " -> ");
			for (int j = 0; j < n; j++) {
				if(matriz[i][j] == 0 || matriz[i][j] == 1)
					continue;
				System.out.printf("( " + j + ": " + "%.3f" + ") ", matriz[i][j]);
				//System.out.printf("%.2f ", matriz[i][j]);
			}
			System.out.println("");
		}
		System.out.println("");
	}
	
	public void limpaMatiz(double matriz[][]){
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				matriz[i][j] = 0;
	}
	
	public void copiaMatiz(double copia[][], double original[][]){
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				copia[i][j] = original[i][j];
	}
	
	public void copiaArray(ArrayList<Integer> origem, ArrayList<Integer> dest){
		dest.clear();
		for(int i=0; i<origem.size(); i++)
			dest.add(origem.get(i));
	}
	
	// Retorna em qual nivel de ocupacao uma aresta está
	public int nivel(int i, int j){
		if(t[i][j] > capacidade[i][j][2])
			return -1;
		if(t[i][j] > capacidade[i][j][1] && t[i][j] <= capacidade[i][j][2])
			return 2;
		if(t[i][j] > capacidade[i][j][0] && t[i][j] <= capacidade[i][j][1])
			return 1;
		return 0;	
	}
	
	public int contaTolls(){
		int numTolls = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if(toll[i][j] > 1) numTolls++;
			}
		}
		return numTolls;
	} 
	
	public double calculaFO(){
		int M = 10000, patamar;
		double FO = 0;		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if(capacidade[i][j][0] == 0)
					continue;				
				patamar = nivel(i, j);
				if(patamar != -1){
					FO += custo[i][j][patamar]*t[i][j] + tariff[i][j][patamar]*t[i][j];					
				}
				else{
//					System.out.println("i: "+i+" j: "+j+ "  t->"+t[i][j]+"  capacidade nivel 2 ->"+ capacidade[i][j][2]
//					+ " pedagio: " + tariff[i][j][2]);
					FO += custo[i][j][2] * t[i][j] + tariff[i][j][2]*t[i][j] + M*(t[i][j] - capacidade[i][j][2]);
				}				
			}
		}
		return FO;
	}
	public double calculaFO2(){
		int M = 10000, patamar;
		double FO = 0;		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if(capacidade[i][j][0] == 0)
					continue;				
				patamar = nivel(i, j);
				if(patamar != -1){
					FO += custo[i][j][patamar]*t[i][j] + tariff[i][j][patamar]*t[i][j];					
				}
				else{
					System.out.println("i: "+i+" j: "+j+ "  t->"+t[i][j]+"  capacidade nivel 2 ->"+ capacidade[i][j][2]
					+ " pedagio: " + tariff[i][j][2]);
					FO += custo[i][j][2] * t[i][j] + tariff[i][j][2]*t[i][j] + M*(t[i][j] - capacidade[i][j][2]);
				}				
			}
		}
		return FO;
	}
	
	public void construtivo(){
		int cont = 0;
		for (int i = 0; i < n; i++){
			for (int j = 0; j < n; j++){
				if(demanda[i][j] == 0) continue;
				path.add(new Rota());
				path.get(cont).setDemanda(demanda[i][j]);
				path.get(cont).setOrigem(i);
				path.get(cont).setDestino(j);
				cont++;
			}
		}
		Collections.sort(path);
		
		atualizaCapacidade();
		for (int i = 0; i < path.size(); i++){		
			dijkstra(path.get(i).getOrigem());
			printCaminho(path.get(i));			
			//System.out.println("Rota: "+i+" "+path.get(i).getOrigem()+"-"+path.get(i).getDestino()+" Enviando: "+path.get(i).getDemanda()+path.get(i).getRota());
		}		
	}
	
	public void setFluxo(ArrayList<Integer> caminho, double demanda, int opcao){
		if(opcao == 1){
			for(int i = 0; i+1 < caminho.size(); i++)
				t[caminho.get(i)][caminho.get(i+1)] += demanda; //coloca fluxo na rota	
		}
		else if(opcao == 2){		
			for(int i = 0; i+1 < caminho.size(); i++)
				t[caminho.get(i)][caminho.get(i+1)] -= demanda; //retira fluxo da rota		
		}
	}
	
	// Remove a aresta mais ocupada de cada rota
	public double buscaLocal(ArrayList<Rota> sol){
		ArrayList<Integer> aux = new ArrayList<Integer>();
		double piorAresta = Integer.MIN_VALUE, fo, fo_star, razao;
		boolean melhoria = true;
		int maxOri = 0, maxDest = 0;
		fo_star = calculaFO();	
		//System.out.printf("\nFo na entrada: %.2f\n", fo_star);
		
		while(melhoria){
			melhoria = false;
			for(int i = 0; i < sol.size(); i++){
				copiaArray(sol.get(i).getRota(), aux);
				maxOri = sol.get(i).getOrigem();
				maxDest = sol.get(i).getDestino();
				piorAresta = Integer.MIN_VALUE;
				
				setFluxo(aux, sol.get(i).getDemanda(), 2);

				for(int j = 0; j+1 < aux.size(); j++){
					razao = t[aux.get(j)][aux.get(j+1)]/capacidade[aux.get(j)][aux.get(j+1)][2];
					if(razao > piorAresta){
						piorAresta = razao;
						maxOri = aux.get(j);
						maxDest = aux.get(j+1);
					}
				}		
				atualizaCapacidade();
				d[maxOri][maxDest] = 1000000; // bloqueia pior aresta
				dijkstra(sol.get(i).getOrigem());
				printCaminho(sol.get(i));
				
				fo = calculaFO();
				if(fo < fo_star){
					melhoria = true;
//					System.out.println(maxOri+ "---"+maxDest+" caminho antes: "+aux);
//					System.out.println("caminho depois: "+sol.get(i).getRota());
					fo_star = fo;
				}
				else{
					setFluxo(sol.get(i).getRota(), sol.get(i).getDemanda(), 2);
					sol.get(i).setRota(aux);
					setFluxo(sol.get(i).getRota(), sol.get(i).getDemanda(), 1);
				}				
			}
		}
		return fo_star;
	}
	
	// Remove cada aresta de cada rota
	public double buscaLocal2(ArrayList<Rota> sol){
		ArrayList<Integer> aux = new ArrayList<Integer>();
		double fo, fo_star, fo_ori;
		boolean melhoria = true;
		int maxOri = 0, maxDest = 0;
		fo_ori = calculaFO();	
		fo_star = fo_ori;
		//System.out.printf("\nFo na entrada: %.2f\n", fo_ori);
		
		while(melhoria){
			melhoria = false;
			for(int i = 0; i < sol.size(); i++){	
				copiaArray(sol.get(i).getRota(), aux);								
				setFluxo(aux, sol.get(i).getDemanda(), 2);
				for(int j = 0; j+1 < aux.size(); j++){
					atualizaCapacidade();
					d[aux.get(j)][aux.get(j+1)] = 1000000; // bloqueia pior aresta
					dijkstra(sol.get(i).getOrigem());
					printCaminho(sol.get(i));
					fo = calculaFO();
					if(fo < fo_star){
						fo_star = fo;
						maxOri = aux.get(j);
						maxDest = aux.get(j+1);
						setFluxo(sol.get(i).getRota(), sol.get(i).getDemanda(), 2);
					}
					else{
						setFluxo(sol.get(i).getRota(), sol.get(i).getDemanda(), 2);
					}
				}
				if(fo_star < fo_ori){
					melhoria = true;
					fo_ori = fo_star;
					atualizaCapacidade();
					d[maxOri][maxDest] = 1000000; // bloqueia pior aresta
					//System.out.println(maxOri+ "---"+maxDest+" caminho antes: "+aux);
					dijkstra(sol.get(i).getOrigem());
					printCaminho(sol.get(i));
					//System.out.println("caminho depois: "+sol.get(i).getRota());
				}
				else{
					sol.get(i).setRota(aux);
					setFluxo(sol.get(i).getRota(), sol.get(i).getDemanda(), 1);	
				}				
			}
		}
		return fo_ori;
	}
	
	//Remove todas as arestas para cada rota
	public double buscaLocal3(ArrayList<Rota> sol){
		ArrayList<Integer> aux = new ArrayList<Integer>();
		double fo, fo_star, fo_ori;
		boolean melhoria = true;
		int maxOri = 0, maxDest = 0;
		fo_ori = calculaFO();	
		fo_star = fo_ori;
	//	System.out.printf("\nFo na entrada: %.2f\n", fo_ori);
		
		while(melhoria){
			melhoria = false;
			for(int i = 0; i < sol.size(); i++){
				copiaArray(sol.get(i).getRota(), aux);								
				setFluxo(aux, sol.get(i).getDemanda(), 2);
				for(int j = 0; j < n; j++){
					for(int k = 0; k < n; k++){
						if(capacidade[j][k][0] == 0)
							continue;
						atualizaCapacidade();
						d[j][k] = 1000000; // bloqueia pior aresta
						dijkstra(sol.get(i).getOrigem());
						printCaminho(sol.get(i));
						fo = calculaFO();
						if(fo < fo_star){
							fo_star = fo;
							maxOri = j;
							maxDest = k;
							setFluxo(sol.get(i).getRota(), sol.get(i).getDemanda(), 2);
						}
						else{
							setFluxo(sol.get(i).getRota(), sol.get(i).getDemanda(), 2);
						}
					}				
				}
				if(fo_star < fo_ori){
					melhoria = true;
					fo_ori = fo_star;
					atualizaCapacidade();
					d[maxOri][maxDest] = 1000000; // bloqueia pior aresta
				//	System.out.println(maxOri+ "---"+maxDest+" caminho antes: "+aux);
					dijkstra(sol.get(i).getOrigem());
					printCaminho(sol.get(i));
				//	System.out.println("caminho depois: "+sol.get(i).getRota());
				}
				else{
					sol.get(i).setRota(aux);
					setFluxo(sol.get(i).getRota(), sol.get(i).getDemanda(), 1);	
				}				
			}
		}
		return fo_ori;
	}
	
	// Refaz o dijikstra para todas as rotas 
	public double buscaLocal4(ArrayList<Rota> sol){
		ArrayList<Integer> aux = new ArrayList<Integer>();
		double fo, fo_star;
		boolean melhoria = true;
		int maxOri = 0, maxDest = 0;
		fo_star = calculaFO();	
	//	System.out.printf("\nFo na entrada: %.2f\n", fo_star);
		
		while(melhoria){
			melhoria = false;
			for(int i = 0; i < sol.size(); i++){	
				copiaArray(sol.get(i).getRota(), aux);								
				setFluxo(aux, sol.get(i).getDemanda(), 2);
				atualizaCapacidade();
				dijkstra(sol.get(i).getOrigem());
				printCaminho(sol.get(i));
				fo = calculaFO();				
				if(fo < fo_star){
					melhoria = true;
					fo_star = fo;
				//	System.out.println("caminho antes: "+aux);
				//	System.out.println("caminho depois: "+sol.get(i).getRota());
				}
				else{
					setFluxo(sol.get(i).getRota(), sol.get(i).getDemanda(), 2);
					sol.get(i).setRota(aux);
					setFluxo(sol.get(i).getRota(), sol.get(i).getDemanda(), 1);
				}					
			}
		}
		return fo_star;
	}
	
	// Remove arestas aleatórias
	public double buscaLocal5(ArrayList<Rota> sol){
		ArrayList<Integer> aux = new ArrayList<Integer>();
		double fo, fo_star, fo_ori;
		boolean melhoria = true;
		int v1, v2;
		Random random = new Random();
		fo_ori = calculaFO();	
		fo_star = fo_ori;	
		//System.out.printf("\nFo na entrada: %.2f\n", fo_star);
		
		while(melhoria){
			melhoria = false;
			for(int i = 0; i < sol.size(); i++){
				copiaArray(sol.get(i).getRota(), aux);
				setFluxo(aux, sol.get(i).getDemanda(), 2);
				atualizaCapacidade();
				for(int j = 0; j < 10; j++){
					v1 = random.nextInt(n-1);
					v2 = random.nextInt(grafo.getVertices().get(v1).getVizinhos().size());
					v2 = grafo.getVertices().get(v1).getVizinhos().get(v2).getId();
					atualizaCapacidade();
					d[v1][v2] = 1000000; // bloqueia aresta escolhida
					dijkstra(sol.get(i).getOrigem());
					printCaminho(sol.get(i));
					fo = calculaFO();
					if(fo < fo_star){
						//System.out.println("melhorou. Removeu "+v1+" - "+v2);
						//System.out.println("velho: " + aux);
						//System.out.println("Novo: " + sol.get(i).getRota());
						//System.out.printf("Valor:%.2f  para %.2f\n\n", fo_star,fo);
						fo_star = fo;
						j = 0;
						copiaArray(sol.get(i).getRota(), aux);
						setFluxo(aux, sol.get(i).getDemanda(), 2);
					}
					else{
						setFluxo(sol.get(i).getRota(), sol.get(i).getDemanda(), 2);
						sol.get(i).setRota(aux);
					}
				}
				if(fo_star < fo_ori){
					melhoria = true;
					fo_ori = fo_star;
					sol.get(i).setRota(aux);
					setFluxo(aux, sol.get(i).getDemanda(), 1);
				}
				else{
					sol.get(i).setRota(aux);
					setFluxo(aux, sol.get(i).getDemanda(), 1);	
				}
			}
		}
		return fo_ori;
	}
	
	public double perturbacao(ArrayList<Rota> sol, int iter){
		int sorteio,a1,a2;
		Random random = new Random();
		atualizaCapacidade();
		for(int i=0; i<iter; i++){
			sorteio = random.nextInt(sol.size()-1);
			setFluxo(sol.get(sorteio).getRota(), sol.get(sorteio).getDemanda(), 2);
			a1 = random.nextInt(n-1);
			a2 = random.nextInt(n-1);
			d[a1][a2] = 1;
			dijkstra(sol.get(sorteio).getOrigem());
			printCaminho(sol.get(sorteio));
		}
		atualizaCapacidade();
		return calculaFO();
	}
	
	public void copiaSolucao(ArrayList<Rota> ori,ArrayList<Rota> dest){
		limpaMatiz(t);	
		dest.clear();
		for(int i=0; i<ori.size(); i++){
			dest.add(new Rota());
			dest.get(i).setDemanda(ori.get(i).getDemanda());
			dest.get(i).setOrigem(ori.get(i).getOrigem());
			dest.get(i).setDestino(ori.get(i).getDestino());
			dest.get(i).setRota(ori.get(i).getRota());
			setFluxo(ori.get(i).getRota(), ori.get(i).getDemanda(), 1);
		}		
	}
	
	public double ils(){
		ArrayList<Rota> path_linha = new ArrayList<Rota>();
		double fo, fo_linha;
		int melhoria = 0;
		
		construtivo(); //Solucao inicial
		System.out.printf("\nFO Construtivo: %.4f",calculaFO() );
		fo = buscaLocal5(path);
		System.out.printf("\nPrimeira busca local: %.4f",fo );
		copiaSolucao(path, path_linha);
		while(melhoria<1000){
			melhoria++;
			fo_linha = perturbacao(path_linha, (int)(path.size()*0.25));
			//System.out.printf("\nValor da fo_linha pos perturbacao: %.4f", fo_linha);
			fo_linha = buscaLocal(path_linha);
			//System.out.printf("\nValor da fo_linha pos busca: %.4f", fo_linha);
			if(fo_linha < fo){
				copiaSolucao(path_linha, path);
				System.out.println("\n\nMelhorou!");
				System.out.printf("Valor da fo anterior: %.4f", fo);
				fo = fo_linha;
				System.out.printf("\nValor da fo depois: %.4f", fo);
				//melhoria = 0;
			}
			else{
				copiaSolucao(path, path_linha);
			}
		}				
		return fo;
	}
	
	// Atualiza os valores de capacidade disponivel em cada aresta da matriz d	
	public void atualizaCapacidade(){		
		for (int i = 0; i < n; i++){
			for (int j = 0; j < n; j++){
				if(capacidade[i][j][0] == 0) // Pular arestas inexistentes				
					continue;
				d[i][j] = 2.5*uMax - (capacidade[i][j][0]-t[i][j]);
			}
		}
	}
	
	// Imprime o caminho da origem ate o destino / Preenche a tabela de fluxos 't'/ 
	//  Salva o trajeto percorrido em caminho
	public void printCaminho(Rota caminho){		
		ArrayList<Integer> aux = new ArrayList<Integer>();
		Integer antecessor = caminho.getDestino();
		aux.add(antecessor);		
 		
		for(;;){
			if(pred[antecessor] == -1) break;
			antecessor = pred[antecessor];
			aux.add(antecessor);
		}		
		for(int i = aux.size()-1; i >= 0; i--){
			if(i == 0) break;
			t[aux.get(i)][aux.get(i-1)] += caminho.getDemanda();			
		}
		Collections.reverse(aux);
		caminho.setRota(aux);		
	}
	
	// Verfica se existem vertices abertos na lista a
	public boolean aberto(){		
		for(int i = 0; i < a.length; i++)
			if(a[i] == true) return true;
		return false;
	}

	// Retorna o id do vertice mais proximo da origem
	public int maisProximo() {		
		double near = Integer.MAX_VALUE;
		int id = 0;
		for (int i = 0; i < a.length; i++) {
			if(a[i] == true){
				if (dist[i] < near) {
					near = dist[i];
					id = i;
				}
			}
		}
		return id;
	}	
	
	// Atualiza as distancias dos vizinhos de r	
	public double relaxaAresta(int atual, int vizinho) {
		if(d[atual][vizinho] != 0 && a[vizinho] == true) {
			if(dist[vizinho] < dist[atual] + d[atual][vizinho])
				return dist[vizinho];
			else
				return dist[atual] + d[atual][vizinho] + toll[atual][vizinho];
		}
		return dist[atual];
	}

	public void dijkstra(int origem) {
		double menorDist = 0;
		int r = 0;
		
		dist[origem] = 0;  //distancia da origem
		pred[origem] = -1; // Vertice de origem nao tem antecessor

		// Inicializa todos os vertices com maxima distancia ate a origem, e sem antecessor
		for (int i = 0; i < grafo.getN(); i++) {
			if (i != origem) {
				dist[i] = Integer.MAX_VALUE;
				pred[i] = -1;
			}
			a[i] = true; // Todos os vértices sao colocados na lista de abertos (true)
		}

		while (aberto()) {
			r = maisProximo();    // Vertice aberto mais proximo da origem
			//f.add(grafo.get(r));  // Adiciona vertice no conjunto fexados
			a[r] = false;         // Remove vertice do conjunto dos abertos
			
			for(int j = 0; j < grafo.getVertices().get(r).getVizinhos().size(); j++){
				menorDist = relaxaAresta(r, grafo.getVertices().get(r).getVizinhos().get(j).getId());
				if(menorDist < dist[grafo.getVertices().get(r).getVizinhos().get(j).getId()]){
					dist[grafo.getVertices().get(r).getVizinhos().get(j).getId()] = menorDist;
					pred[grafo.getVertices().get(r).getVizinhos().get(j).getId()] = r;
				}
			}				
		}	
	}

	public static void main(String[] args){
		Main rede = new Main(args[0]);      
	}
}
