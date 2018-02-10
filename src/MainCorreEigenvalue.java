import java.util.ArrayList;
import Jama.Matrix;
import Jama.EigenvalueDecomposition;

public class MainCorreEigenvalue extends utils {

	public static int nbStockType1 = 20;
	public static int nbStockType2 = 40;
	public static int nbStockType3 = 80;
	public static int nbStock = nbStockType1 + nbStockType2 + nbStockType3;	
	public static ArrayList<Loan> listLoans = new ArrayList<Loan>();
	public static ArrayList<Double> batcheList = new ArrayList<Double>();
	public static ArrayList<ArrayList<Double>> finalBatche = new ArrayList<ArrayList<Double>>();

	public static void main(String[] args) {
		/**
		 * @param: (double probaVar, int nbSim)
		 * probaVar: probability for calculating VAR
		 * nbSim: number of simulation
		 * --------------------------------------------------------------
		 * @return: print EL, vol, var, ES, risk contribution of each type
		 */
		CorrelationEigenvalue(0.95, 100000);
	}
	public static void CorrelationEigenvalue(double probaVar, int nbSim){
		// create our portfolio
		addLoans(listLoans, nbStockType1, 0.00125, 0.55, 200000000, 0.15);
		addLoans(listLoans, nbStockType2, 0.0025, 0.55, 100000000, 0.15);
		addLoans(listLoans, nbStockType3, 0.005, 0.55, 50000000, 0.15);
		Portfolio portfolio = new Portfolio(0.2, listLoans);
		int seuil = (int) ((1 - probaVar) * nbSim);
		//create matrix correlation
		Matrix MatrixCorrelation = createMatrixCorrelation(nbStock, portfolio.getCorrelation());
		
		//create correlated assets returns by using eigenvalue decomposition
		EigenvalueDecomposition eigen = MatrixCorrelation.eig();
		Matrix V = eigen.getV();
		double [] realEigenvalue  = eigen.getRealEigenvalues();
		Matrix sqrtD = getSqrt(realEigenvalue);
		Matrix L = V.times(sqrtD);
		
		Simulator simulator = new Simulator();
		double sum = 0;
		double sumCarre = 0;
		for (int m = 0; m < 10; m++){
			ArrayList<ArrayList<Double>> batcheArray = new ArrayList<ArrayList<Double>>();
			for (int i = nbSim/10*m; i < nbSim/10*(m+1); i++) {
				batcheList = simulator.generateOneSimulationCorrelated(nbStock, L, portfolio);
				//calculate sum and square of sum of all losses 
				sum += batcheList.get(nbStock);
				sumCarre += Math.pow(batcheList.get(nbStock), 2);
				batcheArray.add(batcheList);
			}
			sortBatch(batcheArray, nbStock);
			finalBatche.addAll(selectedBatch(batcheArray, seuil));
			if (finalBatche.size() > 100000){
				sortBatch(finalBatche, nbStock);
				finalBatche = selectedBatch(finalBatche, seuil);
			}
		}
		sortBatch(finalBatche, nbStock);
		finalBatche = selectedBatch(finalBatche, seuil);
		printResults(finalBatche, nbStockType1, nbStockType2, nbStockType3, sum, sumCarre, nbSim, seuil, false);
	}
}
