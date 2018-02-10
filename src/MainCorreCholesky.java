import java.util.ArrayList;
import Jama.Matrix;
import Jama.CholeskyDecomposition;

public class MainCorreCholesky extends utils{

	public static int nbStockType1 = 20;
	public static int nbStockType2 = 40;
	public static int nbStockType3 = 80;
	public static int nbStock = nbStockType1 + nbStockType2 + nbStockType3;
	
	public static ArrayList<Loan> listLoans = new ArrayList<Loan>();
	public static ArrayList<ArrayList<Double>> batcheArray = new ArrayList<ArrayList<Double>>();
	public static ArrayList<Double> batcheList = new ArrayList<Double>();
	public static ArrayList<ArrayList<Double>> allBatche = new ArrayList<ArrayList<Double>>();

	
	public static void main(String[] args) {
		CorrelationCholesky(0.95, 100000);
	}
	public static void CorrelationCholesky(double probaVar, int nbSim){
		// create our portfolio
		addLoans(listLoans, nbStockType1, 0.00125, 0.55, 200000000, 0.15);
		addLoans(listLoans, nbStockType2, 0.0025, 0.55, 100000000, 0.15);
		addLoans(listLoans, nbStockType3, 0.005, 0.55, 50000000, 0.15);
		Portfolio portfolio = new Portfolio(0.2, listLoans);
		int seuil = (int) ((1 - probaVar) * nbSim);
		
		Matrix MatrixCorrelation = createMatrixCorrelation(nbStock, portfolio.getCorrelation());
		CholeskyDecomposition choleskyDecomposition = new CholeskyDecomposition(MatrixCorrelation);
		Matrix L = choleskyDecomposition.getL();
		
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
			allBatche.addAll(selectedBatch(batcheArray, seuil));
		}
		sortBatch(allBatche, nbStock);
		allBatche = selectedBatch(allBatche, seuil);
		printResults(allBatche, nbStockType1, nbStockType2, nbStockType3, sum, sumCarre, nbSim, seuil, false);
	}
 }
