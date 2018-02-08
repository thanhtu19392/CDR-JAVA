import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.distribution.NormalDistribution;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class MainCorreImportanceSampling extends utils {

	public static int nbSim = 100000;
	public static int nbStockType1 = 20;
	public static int nbStockType2 = 40;
	public static int nbStockType3 = 80;
	public static int nbStock = nbStockType1 + nbStockType2 + nbStockType3;

	public static ArrayList<Loan> listLoans = new ArrayList<Loan>();
	public static double randProba;
	public static double probaVar = 0.95;
	public static int seuil = (int) ((1 - probaVar) * nbSim);
	public static double lossObligor[] = new double[nbStock];
	public static double sum = 0;
	public static double sumCarre = 0;
	public static double volatility;
	public static double sumES = 0;
	public static double average = 0;
	public static double[] unCorrelatedRN;
	public static double[] correlatedRN = new double[nbStock];
	public static int n = 0;
	public static ArrayList<ArrayList<Double>> batcheArray = new ArrayList<ArrayList<Double>>();
	public static ArrayList<Double> batcheList = new ArrayList<Double>();
	public static ArrayList<ArrayList<Double>> allBatche = new ArrayList<ArrayList<Double>>();
	public static ArrayList<ArrayList<Double>> finalBatche = new ArrayList<ArrayList<Double>>();
	public static double moyenne = 0;
	public static double totalLoss = 0;
	public static double sigma = 2;

	public static void main(String[] args) {
		// create our portfolio
		addLoans(listLoans, nbStockType1, 0.00125, 0.55, 200000000, 0.15);
		addLoans(listLoans, nbStockType2, 0.0025, 0.55, 100000000, 0.15);
		addLoans(listLoans, nbStockType3, 0.005, 0.55, 50000000, 0.15);
		Portfolio portfolio = new Portfolio(0.2, listLoans);

		// create matrix correlation
		Matrix MatrixCorrelation = createMatrixCorrelation(nbStock, portfolio.getCorrelation());

		// create correlated assets returns by using eigenvalue decomposition
		EigenvalueDecomposition eigen = MatrixCorrelation.eig();
		Matrix V = eigen.getV();
		Matrix D = eigen.getD();
		double[] realEigenvalue = eigen.getRealEigenvalues();
		Matrix sqrtD = getSqrt(realEigenvalue);
		Matrix L = V.times(sqrtD);

		Matrix eigenvector = powerIteration(MatrixCorrelation, 10000);
		double lambda = eigenvector.transpose().times(MatrixCorrelation.times(eigenvector)).get(0, 0);
		double denominatorEigenvector = Math.sqrt(eigenvector.transpose().times(eigenvector).get(0, 0));
		Matrix orthogonalEigenvector = eigenvector.times(1/denominatorEigenvector);
		
		NormalDistribution distribution = new NormalDistribution(0, 1);
        double inverseRisk = distribution.inverseCumulativeProbability((1- probaVar)/20);
        
		Simulator simulator = new Simulator();
		for (int m = 0; m < 10; m++) {
			sum = 0;
			ArrayList<ArrayList<Double>> batcheArray = new ArrayList<ArrayList<Double>>();
			for (int i = nbSim / 10 * m; i < nbSim / 10 * (m + 1); i++) {
				double totalLoss = 0;
				int n = 0;
				unCorrelatedRN = new double[nbStock];
				correlatedRN = new double[nbStock];
				for (int t = 0; t< nbStock; t++){
					unCorrelatedRN[t] = simulator.getRandomGauss(0, 1);
				}
				
				Matrix unCorrelatedRNMatrix = array1DtoMatrixColumn(unCorrelatedRN);
				Matrix correlatedRNMatrix = L.times(unCorrelatedRNMatrix); 

				//create matrix e*
				Matrix e1Matrix = correlatedRNMatrix;
				
				//create matrix e
				Matrix eMatrix = eMatrix(sigma, orthogonalEigenvector, e1Matrix);
				double weight = weight(sigma, lambda, orthogonalEigenvector, e1Matrix);
				eMatrix = eMatrix.times(weight);
				sum += weight;
				Iterator<Loan> loanIterator = portfolio.getLoan().iterator();
				ArrayList<Double> batcheList = new ArrayList<Double>();
				while (loanIterator.hasNext()) {
					Loan loan = loanIterator.next();
					if (eMatrix.get(n, 0) < inverseRisk) {
						batcheList.add(loan.exposure* (1 + loan.costOfCapital - loan.getRecoveryRate()));
						totalLoss += (1 - loan.getRecoveryRate())* loan.exposure;
					} else {
						batcheList.add(0.0);
					}
					n++;
				}
				batcheList.add(totalLoss);
				batcheArray.add(batcheList);
				
				//finish one batch
				if (batcheArray.size() == nbSim / 10) {
					//sort one batch
					sortBatch(batcheArray, nbStock);
					for (int a = nbSim / 10 - seuil; a < nbSim / 10; a++) {
						allBatche.add(batcheArray.get(a));
					}
					System.out.println(sum/(nbSim/10));

				}
			}
		}
		//sort all batch
		sortBatch(allBatche, nbStock);
		for (int i = allBatche.size() - seuil; i < allBatche.size(); i++) {
			finalBatche.add(allBatche.get(i));
		}
		
		for (int i = 0; i <= nbStock; i++) {
			for (int j = 0; j < finalBatche.size(); j++) {
				if ( i < nbStock){
					lossObligor[i] += finalBatche.get(j).get(i);
				}
				else sumES += finalBatche.get(j).get(i);
			}
			if (i<nbStock) lossObligor[i] /= finalBatche.size();
		}

		for (int i = 0; i < nbStock; i++) {
			moyenne += lossObligor[i];
		}
		moyenne /= nbStock;
		sumES /= finalBatche.size();
		System.out.println("Moyenne of Risk contribution: " + moyenne);
		System.out.println("ES: "+  sumES);
		//System.out.println(sum/nbSim);
	}

	
}
