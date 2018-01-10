import java.util.Random;


public class Simulator {
	private Random r = new Random();
	 
    public double getRandomGauss(double mean, double stdDev) {
        double nextGauss = r.nextGaussian();
        double rand = nextGauss * stdDev + mean;
        System.out.println(rand);
        return rand;
    }
    
    public double getRandomUniform(){
    	double nextItem = r.nextDouble();
    	System.out.println(nextItem);
    	return nextItem;
    }

}
