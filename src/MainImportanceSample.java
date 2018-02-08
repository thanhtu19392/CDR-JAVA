import java.util.ArrayList;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;


public class MainImportanceSample extends utils{

	public static int nbStockType1 = 20;
	public static int nbStockType2 = 40;
	public static int nbStockType3 = 80;
	public static int nbStock = nbStockType1 + nbStockType2 + nbStockType3;
	public static ArrayList<Loan> listLoans = new ArrayList<Loan>();
	public static ArrayList<ArrayList<Double>> batcheArray = new ArrayList<ArrayList<Double>>();
	public static ArrayList<Double> batcheList = new ArrayList<Double>();
	public static ArrayList<ArrayList<Double>> finalBatche = new ArrayList<ArrayList<Double>>();

	public static void main(String[] args) {
		/**
		 * @param: (double sigma, int nbSim, double probaVar)
		 * sigma: scale level
		 * nbSim: number of simulation
		 * probaVar: probability of variance
		 * --------------------------------
		 * @return: print EL, Vol, Var, ES, risk contribution of obligor
		 */
		ImportanceSampling(1.5, 100000 , 0.95);
	}
	
	public static void ImportanceSampling(double sigma, int nbSim, double probaVar){
		// create our portfolio
		addLoans(listLoans, nbStockType1, 0.00125, 0.55, 200000000, 0.15);
		addLoans(listLoans, nbStockType2, 0.0025, 0.55, 100000000, 0.15);
		addLoans(listLoans, nbStockType3, 0.005, 0.55, 50000000, 0.15);
		Portfolio portfolio = new Portfolio(0.2, listLoans);
		int seuil = (int) ((1 - probaVar) * nbSim);
		// create matrix correlation
		Matrix MatrixCorrelation = createMatrixCorrelation(nbStock,portfolio.getCorrelation());

		// create correlated assets returns by using eigenvalue decomposition
		EigenvalueDecomposition eigen = MatrixCorrelation.eig();
		Matrix V = eigen.getV();
		double[] realEigenvalue = eigen.getRealEigenvalues();
		Matrix sqrtD = getSqrt(realEigenvalue);
		Matrix L = V.times(sqrtD);

		Matrix eigenvector = powerIteration(MatrixCorrelation, 10000);
		double lambda = eigenvector.transpose().times(MatrixCorrelation.times(eigenvector)).get(0, 0);
		double denominatorEigenvector = Math.sqrt(eigenvector.transpose().times(eigenvector).get(0, 0));
		Matrix orthogonalEigenvector = eigenvector.times(1 / denominatorEigenvector);

		Simulator simulator = new Simulator();
		double sum = 0;
		double sumCarre = 0;
		for (int m = 0; m < 10; m++) {
			ArrayList<ArrayList<Double>> batcheArray = new ArrayList<ArrayList<Double>>();
			for (int i = nbSim / 10 * m; i < nbSim / 10 * (m + 1); i++) {
				batcheList = simulator.generateOneSimulationImportanceSample(sigma, nbStock, L, 
																			orthogonalEigenvector, portfolio, lambda);
				sum += batcheList.get(nbStock);
				sumCarre += Math.pow(batcheList.get(nbStock), 2);
				batcheArray.add(batcheList);
			}
			//finish one batch
			//sort one batch
			sortBatch(batcheArray, nbStock);
			finalBatche.addAll(selectedBatcheIS(batcheArray, seuil));
			if (finalBatche.size() > 100000){
				sortBatch(finalBatche, nbStock);
				finalBatche = selectedBatcheIS(finalBatche, seuil);
			}
		}
		//sort all batch
		sortBatch(finalBatche, nbStock);
		finalBatche = selectedBatcheIS(finalBatche, seuil);
		System.out.println("sigma: " + sigma);
		printResults(finalBatche, nbStockType1, nbStockType2, nbStockType3, sum, sumCarre, nbSim);
	}
	
	public static ArrayList<ArrayList<Double>> selectedBatcheIS(ArrayList<ArrayList<Double>> BatchArray, double seuil){
		double sumWeight = 0;
		ArrayList<ArrayList<Double>> batchSelected = new ArrayList<ArrayList<Double>>();
		int indice = BatchArray.size() - 1;
		while (sumWeight < seuil) {
			sumWeight += BatchArray.get(indice).get(nbStock +1);
			indice -= 1;
		}
		for (int i = indice; i < BatchArray.size(); i++) {
			batchSelected.add(BatchArray.get(i));
		}
		return batchSelected;
	}
	
	
}
