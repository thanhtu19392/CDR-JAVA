
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
//import org.apache.commons.math3.distribution.NormalDistribution;


public class MainCorre {
	public static int nbSim = 100000;
	public static int nbStock = 40;
	public static ArrayList<Loan> listLoans = new ArrayList<Loan>();
	public static double randProba;
	public static double probaVar = 0.95;
	public static int seuil = (int) ((1 - probaVar) * nbSim);
	// public static double [] randProba;
	public static double totalLoss[] = new double[nbSim];
	public static ArrayList<Double> totalLossList = new ArrayList<Double>();
	public static double sum = 0;
	public static double sumCarre = 0;
	public static double volatility;
	public static double sumES = 0;
	public static double average = 0;
	public static double[] unCorrelatedRN;
	public static double[] correlatedRN = new double[nbStock];
	public static int n = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (int i = 0; i < nbStock; i++) {
			Loan loan = new Loan(i, 0.0025, 0.6, 100000000);
			listLoans.add(loan);
		}
		Portfolio portfolio = new Portfolio(0.25, listLoans);

		double[][] MatrixCorrelation = new double[nbStock][nbStock];
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
		double[][] L = choleskyDecomposition.getL;
		
		//double risk = NormalDistribution.InverseDistributionFunction(probaVar, 0, 1);
		
		Simulator simulator = new Simulator();
		
		for (int i = 0; i < nbSim; i++) {
			int n = 0;
			unCorrelatedRN = new double[nbStock];
			correlatedRN = new double[nbStock];
			//unCorrelatedRN = simulator.generateRandomNumberArray(nbStock);
			for (int t = 0; t< nbStock; t++){
				unCorrelatedRN[t] = simulator.getRandomGauss(0, 1);
			}
			for (int k = 0; k < nbStock; k++) {
				for (int j = 0; j < nbStock; j++) {
					correlatedRN[k] += L[k][j] * unCorrelatedRN[j];
				}
			}
			Iterator<Loan> loanIterator = portfolio.getLoan().iterator();
			
			while (loanIterator.hasNext()) {
				Loan loan = loanIterator.next();
				if (correlatedRN[n] < -2.8) {
					totalLoss[i] += (1 - loan.getRecoveryRate()) * loan.exposure;
				} else {
					// System.out.println("Not default of obligor " + i);
				}
				n++;
			}
			
		}
		
		for (int i = 0; i< nbSim ; i++){
			totalLossList.add(totalLoss[i]);
			sum += totalLoss[i];
			sumCarre += Math.pow(totalLoss[i], 2);
			//System.out.println(totalLoss[i]);
		}
		
		average = sum/nbSim;
		volatility = Math.sqrt((sumCarre/nbSim)- (sum/(nbSim-1)*(sum/nbSim)));
		System.out.println("Expected Loss:" + average); 
		System.out.println("Volatility:" + volatility);
		
		Collections.sort(totalLossList);
		Collections.reverse(totalLossList);
		
		/**
		for (int i =0; i< nbSim; i++){
			System.out.println(totalLossList.get(i));
		}
		*/
		
		System.out.println("VAR:"+ totalLossList.get(seuil));
		for (int i =0;i < seuil; i++){
			sumES += totalLossList.get(i);
		}
		System.out.println("ES:" + sumES/seuil);
		
	}
}
