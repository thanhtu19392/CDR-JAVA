import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import org.apache.commons.math3.distribution.NormalDistribution;

import Jama.Matrix;


public class Simulator extends utils {
	private static Random r = new Random();
	 
    public static double getRandomGauss(double mean, double stdDev) {
        double nextGauss = r.nextGaussian();
        double rand = nextGauss * stdDev + mean;
        return rand;
    }
    
    public double getRandomUniform(){
    	double nextItem = r.nextDouble();
    	return nextItem;
    }
    
    /**
     * Generate a random number
     * @return a random number
     */
    public static double generateRandomNumber() {

        final Random randomGenerator = new Random();
        final double u1 = randomGenerator.nextDouble();
        final double u2 = randomGenerator.nextDouble();

        final double number1 = Math.sqrt( -2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
        //final double number2 = Math.sqrt( -2 * Math.log(u1)) * Math.sin(2 * Math.PI * u2);


        return number1;
    }
    
    /**
     * Generate an array of random numbers
     *
     * @param size thr half size of numbers
     * @return a size * 2 array of random numbers
     */
    public static double[] generateRandomNumberArray(final int size) {
        final double[] numbers = new double[size];
        final Random randomGenerator = new Random();

        int numberIdx = 0;
        for (int idx = 0; idx < Math.ceil(size/2.0); idx++) {
            final double u1 = randomGenerator.nextDouble();
            final double u2 = randomGenerator.nextDouble();
            final double number1 = Math.sqrt( -2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
            final double number2 = Math.sqrt( -2 * Math.log(u1)) * Math.sin(2 * Math.PI * u2);

            numbers[numberIdx++] = number1;
            if (numberIdx < numbers.length) {
                numbers[numberIdx++] = number2;
            }
        }

        return numbers;
    }
    
    public ArrayList<Double> generateOneSimulationImportanceSample(double sigma, int nbStock, Matrix L,
    																Matrix orthogonalEigenvector, Portfolio portfolio, double lambda ){
    	/**
         * Generate an array of risk contributions of all obligor for every simulation in case of importance sample
         *
         * @param double sigma, int nbStock, Matrix L, double inverseRisk,
    			Matrix orthogonalEigenvector, Portfolio portfolio, double lambda 
         * @return a batch list
         */
    	
    	double totalLoss = 0;
		int n = 0;
		NormalDistribution distribution = new NormalDistribution(0, 1);
		Matrix unCorrelatedRNMatrix = new Matrix(nbStock, 1);
		for (int i = 0; i< nbStock; i++){
			unCorrelatedRNMatrix.set(i, 0, getRandomGauss(0, 1));
		}
		Matrix correlatedRNMatrix = L.times(unCorrelatedRNMatrix); 

		//create matrix e*
		Matrix e1Matrix = correlatedRNMatrix;
        
		//create matrix e
		Matrix eMatrix = eMatrix(sigma, orthogonalEigenvector, e1Matrix);
		double weight = weight(sigma, lambda, orthogonalEigenvector, e1Matrix);
		//System.out.println(weight);
		Iterator<Loan> loanIterator = portfolio.getLoan().iterator();
		ArrayList<Double> batcheList = new ArrayList<Double>();
		while (loanIterator.hasNext()) {
			Loan loan = loanIterator.next();
	        double inversePD = distribution.inverseCumulativeProbability(loan.getProbaDefault());
			if (eMatrix.get(n, 0) < inversePD) {
				batcheList.add(loan.getExposure() * (1 - loan.getRecoveryRate()) );
				totalLoss += (1 - loan.getRecoveryRate())* loan.getExposure() ;
			} else {
				batcheList.add(0.0);
			}
			n++;
		}
		batcheList.add(totalLoss);
		batcheList.add(weight);
		return batcheList;
    }
    
    public ArrayList<Double> generateOneSimulationCorrelated(int nbStock, Matrix L, Portfolio portfolio){
    	/**
         * Generate an array of risk contributions of all obligor for every simulation in case of correlation
         *
         * @param int nbStock, Matrix L, Portfolio portfolio, double inverseRisk
         * @return a batch list
         */
    	
    	double totalLoss =0;
		int n = 0;
		NormalDistribution distribution = new NormalDistribution(0, 1);
		Matrix unCorrelatedRNMatrix = new Matrix(nbStock, 1);
		for (int i = 0; i< nbStock; i++){
			unCorrelatedRNMatrix.set(i, 0, getRandomGauss(0, 1));
		}
		Matrix correlatedRNMatrix = L.times(unCorrelatedRNMatrix); 
		
		Iterator<Loan> loanIterator = portfolio.getLoan().iterator();
		ArrayList<Double> batcheList = new ArrayList<Double>();
		while (loanIterator.hasNext()) {
			Loan loan = loanIterator.next();
	        double inversePD = distribution.inverseCumulativeProbability(loan.getProbaDefault());
			if (correlatedRNMatrix.get(n, 0) < inversePD) {
				batcheList.add(loan.getExposure() * (1 -  loan.getRecoveryRate()) );
				totalLoss += (1 - loan.getRecoveryRate()) * loan.getExposure();
			} else {
				batcheList.add(0.0);
			}
			n++;
		}
		batcheList.add(totalLoss);
		return batcheList;
    }

}
