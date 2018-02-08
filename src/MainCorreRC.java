import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class MainCorreRC {

	public static int nbSim = 1000000;
	public static int nbStock = 40;
	public static ArrayList<Loan> listLoans = new ArrayList<Loan>();
	public static double randProba;
	public static double probaVar = 0.95;
	public static int seuil = (int) ((1 - probaVar) * nbSim);
	// public static double [] randProba;
	public static double lossObligor[] = new double[nbStock];
	//public static ArrayList<Double> totalLossList = new ArrayList<Double>();
	public static double sum = 0;
	public static double sumCarre = 0;
	public static double volatility;
	public static double sumES = 0;
	public static double average = 0;
	public static double[] unCorrelatedRN;
	public static double[] correlatedRN = new double[nbStock];
	public static int n = 0;
	public static ArrayList<ArrayList<Double>> batcheArray = new ArrayList<ArrayList<Double>>();
	//public static double[][] batche = new double[nbSim][nbStock];
	public static ArrayList<Double> batcheList = new ArrayList<Double>();
	public static ArrayList<ArrayList<Double>> allBatche = new ArrayList<ArrayList<Double>>();
	public static ArrayList<ArrayList<Double>> finalBatche = new ArrayList<ArrayList<Double>>();
	public static double moyenne = 0;
	public static double totalLoss = 0;
	
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
		
		Simulator simulator = new Simulator();
		
		for (int m = 0; m < 10; m++){
			sum = 0;
			ArrayList<ArrayList<Double>> batcheArray = new ArrayList<ArrayList<Double>>();
			for (int i = nbSim/10*m; i < nbSim/10*(m+1); i++) {
				//ArrayList<Double> batcheList = new ArrayList<Double>();
				double totalLoss =0;
				int n = 0;
				unCorrelatedRN = new double[nbStock];
				correlatedRN = new double[nbStock];
				for (int t = 0; t< nbStock; t++){
					unCorrelatedRN[t] = simulator.getRandomGauss(0, 1);
				}
				for (int k = 0; k < nbStock; k++) {
					for (int j = 0; j < nbStock; j++) {
						correlatedRN[k] += L[k][j] * unCorrelatedRN[j];
					}
				}
				
				Iterator<Loan> loanIterator = portfolio.getLoan().iterator();
				ArrayList<Double> batcheList = new ArrayList<Double>();
				while (loanIterator.hasNext()) {
					Loan loan = loanIterator.next();
					if (correlatedRN[n] < -2.8) {
						batcheList.add((1 - loan.getRecoveryRate()) * loan.exposure);
						totalLoss += (1 - loan.getRecoveryRate()) * loan.exposure;
					} else {
						batcheList.add(0.0);
					}
					n++;
				}
				batcheList.add(totalLoss);
				batcheArray.add(batcheList);
				
				
				if (batcheArray.size() == nbSim/10){
					Collections.sort(batcheArray, new Comparator<List<Double>>() {
						  @Override
						  public int compare(List<Double> list1, List<Double> list2) {
						    return list1.get(nbStock).compareTo(list2.get(nbStock));
						  }
						});
					
					for (int a = seuil ; a < nbSim/10; a++){
						allBatche.add(batcheArray.get(a));
						}
					
				}
			}
		}
		//System.out.println(allBatche.size());
		
		Collections.sort(allBatche, new Comparator<List<Double>>() {
			  @Override
			  public int compare(List<Double> list1, List<Double> list2) {
			    return list1.get(nbStock).compareTo(list2.get(nbStock));
			  }
			});
		
		for (int i = allBatche.size() -seuil ; i < allBatche.size(); i++){
			finalBatche.add(allBatche.get(i));
		}
		
		for(int i = 0; i < nbStock;i++){
			for(int j = 0; j < finalBatche.size(); j++){
				lossObligor[i] += finalBatche.get(j).get(i);
			}
			lossObligor[i] /= finalBatche.size();
		}
		
		for (int i = 0; i < nbStock; i++){
			moyenne += lossObligor[i];
		}
		moyenne /= nbStock;
		System.out.println("Moyenne of Risk contribution: " + moyenne);
	}
}

