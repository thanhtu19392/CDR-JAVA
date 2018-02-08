import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import Jama.Matrix;


public class utils {
	
	public static Matrix getSqrt(double[] realEigenvalues) {
		Matrix result = new Matrix(realEigenvalues.length,
				realEigenvalues.length);
		for (int i = 0; i < realEigenvalues.length; i++) {
			for (int j = 0; j <= i; j++) {
				if (i == j) {
					result.set(i, j, Math.sqrt(realEigenvalues[i]));
				} else {
					result.set(i, j, 0.);
					result.set(j, i, 0.);
				}
			}
		}

		return result;
	}
	
	public static void sortBatch (ArrayList<ArrayList<Double>> table, final int nbStock){
		Collections.sort(table, new Comparator<List<Double>>() {
			  @Override
			  public int compare(List<Double> list1, List<Double> list2) {
			    return list1.get(nbStock).compareTo(list2.get(nbStock));
			  }
			});
	}
	
	public static Matrix createMatrixCorrelation(int length, double correlationValue){
		Matrix MatrixCorrelation = new Matrix(length,length);
		for(int i = 0; i < length; i++) {
            for(int j = 0; j <= i; j++) {
            	if (i == j){
            		MatrixCorrelation.set(i, j, 1.);
            	}
                else {
					MatrixCorrelation.set(i, j, correlationValue);
					MatrixCorrelation.set(j, i, correlationValue);
				}
            }
		}
		return MatrixCorrelation;
	}
	
	public static void addLoans(ArrayList<Loan> listLoans, int nbStock, double probaDefault, 
								double recoveryRate, double exposure, double costOfCapital){
		for (int i = 0; i < nbStock; i++) {
			Loan loan = new Loan(i, probaDefault, recoveryRate, exposure, costOfCapital);
			listLoans.add(loan);
		}
	}
	
	public static Matrix array1DtoMatrixColumn (double[] array1D){
		Matrix matrix = new Matrix(array1D.length,1);
		for(int i=0; i< array1D.length; i++ ){
			matrix.set(i, 0, array1D[i]);
		}
		return matrix;
	}
	
	public static Matrix powerIteration(Matrix MatrixCorrelation,
			int num_simulations) {
		int length = MatrixCorrelation.getColumnDimension();
		Matrix v0 = new Matrix(length, 1);
		for (int i = 0; i < length; i++) {
			v0.set(i, 0, 1.0);
		}
		for (int j = 0; j < num_simulations; j++) {
			Matrix v1 = MatrixCorrelation.times(v0);
			double normV1 = v1.norm2();
			v0 = v1.times(1 / normV1);
		}
		return v0;
	}

	
	public static double weight(double sigma, double lambda, Matrix q1, Matrix e1) {
		return (sigma * Math.exp(-0.5 * ((Math.pow(sigma, 2) - 1)
						* Math.pow(q1.transpose().times(e1).get(0, 0), 2) / lambda)));
	}
	
	public static Matrix eMatrix(double sigma, Matrix q1, Matrix e1){
    	return (q1.times((sigma-1)*(q1.transpose().times(e1).get(0, 0))).plus(e1));
    }
	
	public static double volatility(int nbSim, double sumCarre, double sum){
		double volatility = Math.sqrt((sumCarre/nbSim)- (sum/(nbSim-1)*(sum/nbSim)));
		return volatility;
	}
	
	public static ArrayList<ArrayList<Double>> selectedBatch( ArrayList<ArrayList<Double>> batchArrayList, int numberSimSelected){
		ArrayList<ArrayList<Double>> finalBatche = new ArrayList<ArrayList<Double>>();
		for (int i = batchArrayList.size() - numberSimSelected ; i < batchArrayList.size(); i++){
			finalBatche.add(batchArrayList.get(i));
		}
		return finalBatche;
	}
	
	public static double moyenne(int start, int nbStock, double[] lossObligor){
		double moyenne = 0;
		for (int i = start; i < start + nbStock; i++){
			moyenne += lossObligor[i];
		}
		moyenne /= nbStock;
		return moyenne;
	}
	
	public static void printResults(ArrayList<ArrayList<Double>> finalBatche,
			int nbStockType1, int nbStockType2, int nbStockType3, double sum,
			double sumCarre, int nbSim) {
		int nbStock = nbStockType1 + nbStockType2 + nbStockType3;
		int lengthFinalBatch = finalBatche.size();
		double lossObligor[] = new double[finalBatche.get(0).size()];
		for (int i = 0; i < finalBatche.get(0).size(); i++) {
			for (int j = 0; j < lengthFinalBatch; j++) {
				lossObligor[i] += finalBatche.get(j).get(i);
			}
			lossObligor[i] /= lengthFinalBatch;
		}

		double ES = lossObligor[nbStock];
		double s = 0;
		for (int j = 0; j < lengthFinalBatch; j++) {
			s += Math.pow(finalBatche.get(j).get(nbStock) - ES, 2);
		}
		s /= lengthFinalBatch - 1;

		System.out.println("Expected Loss: " + sum / nbSim);
		System.out.println("Volatility: " + volatility(nbSim, sumCarre, sum));
		System.out.println("Var: " + finalBatche.get(0).get(nbStock));
		System.out.println("Expected Shortfall: " + ES);
		System.out.println("Dispersion de ES: " + Math.sqrt(s));
		System.out.println("Moyenne of risk contribution Type 1: " + moyenne(0, nbStockType1, lossObligor));
		System.out.println("Moyenne of risk contribution Type 2: " + moyenne(nbStockType1, nbStockType2, lossObligor));
		System.out.println("Moyenne of risk contribution Type 3: " + moyenne(nbStockType2, nbStockType3, lossObligor));
	}
	
}
