import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.lang.Math;


public class Main {

	public static int nbSim = 10000;
	public static int nbStock = 40;
	public static ArrayList<Loan> listLoans = new ArrayList<Loan>();
	public static double randProba;
	public static double probaVar = 0.95;
	public static int seuil = (int) ((1-probaVar)*nbSim);
	//public static double [] randProba;
	public static double totalLoss[] = new double[nbSim];
	public static ArrayList<Double> totalLossList = new ArrayList<Double>();
	public static double sum = 0;
	public static double sumCarre = 0;
	public static double volatility;
	public static double sumES =0;
	public static double average =0;
	public static double [] unCorrelatedRN;
	public static double [] correlatedRN = new double[nbStock];
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (int i =0; i< nbStock; i++){
			Loan loan = new Loan(i, 0.25, 0.6, 100000000);
			listLoans.add(loan);
		}
		Portfolio portfolio = new Portfolio(0.25, listLoans);
		
		Simulator simulator = new Simulator();

		for (int i = 0; i < nbSim ; i++){
			Iterator<Loan> loanIterator= portfolio.getLoan().iterator();
			while(loanIterator.hasNext()){
				Loan loan = loanIterator.next();
				randProba = simulator.getRandomUniform();
				//System.out.println(randProba);
				//randProba = simulator.generateRandomNumberArray(nbStock);
				
				if (randProba <= loan.getProbaDefault() ){
					//System.out.println("Default of obligor " + i);
					totalLoss[i] += (1- loan.getRecoveryRate()) * loan.exposure;
				}
				else{
					//System.out.println("Not default of obligor " + i);
				}
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
		
		for (int i =0; i< nbSim; i++){
			
			//System.out.println(totalLossList.get(i));
		}
		
		System.out.println("VAR:"+ totalLossList.get(seuil));
		for (int i =0;i < seuil; i++){
			sumES += totalLossList.get(i);
		}
		System.out.println("ES:" + sumES/seuil);
	}
	
	
	
	
}
