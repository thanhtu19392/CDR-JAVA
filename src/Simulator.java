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
    	//System.out.println(nextItem);
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
        final double number2 = Math.sqrt( -2 * Math.log(u1)) * Math.sin(2 * Math.PI * u2);


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
    
    
    
    

}
