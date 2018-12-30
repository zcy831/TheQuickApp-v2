package group12.thequickapp.model;

public class DrawingModel {
	public final int N = 20;
	private double[][] b;
	
	public DrawingModel(){
		b = new double[N][N];
	}
	
	public double get(int x, int y){
		return b[x][y];
	}
	
	public double[][] getArray(){
		return this.b;
	}
	
	public void set(int x, int y){
		b[x][y] = 1.0;
	}
	
	public void clear(){
		b = new double[N][N];
	}
	
	private int count(){
		int count = 0;
		for(int i=0;i<N;i++)
			for(int j=0;j<N;j++)
				if(b[i][j]!=0)
					count++;
		return count;
	}
}
