
public class Loan {
	public int number;
	public double probaDefault;
	public double recoveryRate; 
	public double exposure;
	public double costOfCapital;
	
	public Loan(int number, double probaDefault, double recoveryRate, double exposure, double costOfCapital){
		this.number= number;
		this.probaDefault= probaDefault;
		this.recoveryRate= recoveryRate;
		this.exposure= exposure;
		this.costOfCapital = costOfCapital;
	}
	
	public int getLabel(){
		return number;
	}
	
	public void setLabel(int number){
		this.number = number;
	}
	
	public double getProbaDefault(){
		return probaDefault;
	}
	
	public void setProbaDefault(double probaDefault){
		this.probaDefault = probaDefault;
	}
	
	public double getRecoveryRate(){
		return recoveryRate;
	}
	
	public void setRecoveryRate(double recoveryRate){
		this.recoveryRate = recoveryRate;
	}
	
	public double getExposure(){
		return exposure;
	}
	
	public void setExposure(double exposure){
		this.exposure = exposure;
	}
	
	
}
