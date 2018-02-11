import java.util.ArrayList;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;


public class MainImportanceSampling extends utils{

	public static int nbStockType1 = 20;
	public static int nbStockType2 = 40;
	public static int nbStockType3 = 80;
	public static int nbStock = nbStockType1 + nbStockType2 + nbStockType3;

	public static void main(String[] args) {

		double[] listSigma =  {0.5, 1, 1.5, 2, 3};
		for(int i = 0; i < listSigma.length; i++){
			ImportanceSampling(listSigma[i], 100000 , 0.95);
		}
		
	}
	
	public static void ImportanceSampling(double sigma, int nbSim, double probaVar){
		/**
		 * @param: (double sigma, int nbSim, double probaVar)
		 * sigma: scale-up factor level
		 * nbSim: number of simulation
		 * probaVar: probability of variance
		 * --------------------------------
		 * @return: print EL, Vol, Var, ES, risk contribution of obligor
		 */
		
		// create our portfolio
		ArrayList<Loan> listLoans = new ArrayList<Loan>();
		addLoans(listLoans, nbStockType1, 0.00125, 0.55, 200000000, 0.15);
		addLoans(listLoans, nbStockType2, 0.0025, 0.55, 100000000, 0.15);
		addLoans(listLoans, nbStockType3, 0.005, 0.55, 50000000, 0.15);
		Portfolio portfolio = new Portfolio(0.2, listLoans);
		
		int seuil = (int) ((1 - probaVar) * nbSim);
		ArrayList<Double> batcheList = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> finalBatche = new ArrayList<ArrayList<Double>>();
		
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
			for (int i = nbSim/10  * m; i < nbSim/10 * (m + 1); i++) {
				batcheList = simulator.generateOneSimulationImportanceSample(sigma, nbStock, L, 
																			orthogonalEigenvector, portfolio, lambda);
				double lossEachSimulation = batcheList.get(nbStock);
				double weightEachSimulation = batcheList.get(nbStock +1);
				sum += lossEachSimulation * weightEachSimulation;
				sumCarre += Math.pow(lossEachSimulation * weightEachSimulation, 2);
				batcheArray.add(batcheList);
			}
			//finish one batch
			//sort the batch
			sortBatch(batcheArray, nbStock);
			finalBatche.addAll(selectedBatcheIS(batcheArray, seuil));
			if (finalBatche.size() > 100000){
				sortBatch(finalBatche, nbStock);
				finalBatche = selectedBatcheIS(finalBatche, seuil);
			}
		}
		//sort final batch
		sortBatch(finalBatche, nbStock);
		finalBatche = selectedBatcheIS(finalBatche, seuil);
		System.out.println("sigma: " + sigma);
		printResults(finalBatche, nbStockType1, nbStockType2, nbStockType3, sum, sumCarre, nbSim, seuil, true);
	}
	
	public static ArrayList<ArrayList<Double>> selectedBatcheIS(ArrayList<ArrayList<Double>> BatchArray, double seuil){
		double sumWeight = 0;
		ArrayList<ArrayList<Double>> batchSelected = new ArrayList<ArrayList<Double>>();
		int indice = BatchArray.size() - 1;
		while (sumWeight < seuil) {
			sumWeight += BatchArray.get(indice).get(nbStock +1);
			indice -= 1;
		}
		for (int i = indice + 1; i < BatchArray.size(); i++) {
			batchSelected.add(BatchArray.get(i));
		}
		return batchSelected;
	}
	
	
}
