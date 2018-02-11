
import java.util.ArrayList;

public class Portfolio {
	public double correlation;  
	public ArrayList<Loan> loan = new ArrayList<Loan>() ;
	public ArrayList<ArrayList<Simulator>> simulator = new ArrayList<ArrayList<Simulator>>();
	public double loss;
	
	Portfolio(double correlation, ArrayList<Loan> loan){
		System.out.println("start our portfolio");
		System.out.println("-------------------------------------");
		this.loan = loan;
		this.correlation = correlation;
	}
	
	public ArrayList<Loan> getLoan() {
		return loan;
	}
	
	public void setLoan(ArrayList<Loan> loan){
		this.loan = loan;
	}
	
	public double getCorrelation(){
		return correlation;
	}
	
	public void setCorrelation(double correlation){
		this.correlation = correlation;
	}
	
	public double getLoss(){
		return loss;
	}
	
	public void setLoss(double loss){
		this.loss = loss;
	}
}
