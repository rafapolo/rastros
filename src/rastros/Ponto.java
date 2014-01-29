package rastros;

class Ponto{
	int cor;
	int position;
	
	public Ponto(int cor, int pos){
		this.cor = cor;
		this.position = pos;
	}
	
	public int getCor() {
		return cor;
	}
	public void setCor(int cor) {
		this.cor = cor;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	
}
