//package menorCaminho;

public class Solucao implements Cloneable {
	private int id;
	public Solucao(int oi){
		this.id = oi;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
    @Override
    public Solucao clone() throws CloneNotSupportedException {
        return (Solucao) super.clone();
    }
}
