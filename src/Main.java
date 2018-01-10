import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class Main {

	public static int nbSim = 100;
	public static int nbStock = 40;
	public static ArrayList<Loan> listLoans = new ArrayList<Loan>();
	public static double randProba;
	public static double loss = 0;
	public static double severity = 100;
	public static double totalLoss[] = new double[nbSim];
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (int i =0; i< nbStock; i++){
			Loan loan = new Loan(i, 0.25, 0.6, 100000);
			listLoans.add(loan);
		}
		Portfolio portfolio = new Portfolio(0, listLoans);
		
		for (int i = 0; i < nbSim ; i++){
			Iterator<Loan> loanIterator= portfolio.getLoan().iterator();
			while(loanIterator.hasNext()){
				Loan loan = loanIterator.next();
				Simulator simulator = new Simulator();
				randProba = simulator.getRandomUniform();
				if (randProba < loan.getProbaDefault() ){
					System.out.println("Default of obligor " + i);
					totalLoss[i] += (1- loan.getRecoveryRate()) * loan.exposure;
				}
				else{
					System.out.println("Not default of obligor " + i);
				}
			}
		}
		
		for (int i = 0; i< nbSim ; i++){
			System.out.println(totalLoss[i]);
		}
		
		
	}

}
