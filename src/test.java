import java.util.ArrayList;

public class test {
	
	public static int nbStock = 40;
	public static ArrayList<Loan> listLoans = new ArrayList<Loan>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		for (int i =0; i< nbStock; i++){
			Loan loan = new Loan(i, 0.25, 0.6, 100000000);
			listLoans.add(loan);
		}
		Portfolio portfolio = new Portfolio(0.25, listLoans);
		
		double [][] MatrixCorrelation = new double [nbStock][nbStock];
		for (int row = 0; row < nbStock; row++) {
			for (int col = 0; col < nbStock; col++) {
				if (row == col) {
					MatrixCorrelation[row][col] = 1d;
				} else {
					MatrixCorrelation[row][col] = portfolio.getCorrelation();
				}
			}
		}
		
		Cholesky choleskyDecomposition = new Cholesky(MatrixCorrelation);
		double [][] L = choleskyDecomposition.getL ;
	
		
		for (int i = 0; i < nbStock; i++){
			for (int j = 0; j < nbStock; j++){
				System.out.print(L[i][j]);
			}
			System.out.println();
		}
		
		Simulator simulator = new Simulator();
		double [] simulatorArray=  simulator.generateRandomNumberArray(nbStock);
		for (int i = 0; i < nbStock; i ++){
			System.out.println(simulatorArray[i]);
		}
	} 
	
}
