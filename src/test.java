import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import Jama.CholeskyDecomposition;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

public class test {

	public static int nbSim = 10000;
	public static int nbStock = 40;
	public static ArrayList<Loan> listLoans = new ArrayList<Loan>();
	public static double randProba;
	public static double probaVar = 0.95;
	public static int seuil = (int) ((1 - probaVar) * nbSim);
	// public static double [] randProba;
	public static double lossObligor[] = new double[nbStock];
	// public static ArrayList<Double> totalLossList = new ArrayList<Double>();
	public static double sum = 0;
	public static double sumCarre = 0;
	public static double volatility;
	public static double sumES = 0;
	public static double average = 0;
	public static double[] unCorrelatedRN;
	public static double[] correlatedRN = new double[nbStock];
	public static int n = 0;
	public static ArrayList<ArrayList<Double>> batcheArray = new ArrayList<ArrayList<Double>>();
	// public static double[][] batche = new double[nbSim][nbStock];
	public static ArrayList<Double> batcheList = new ArrayList<Double>();
	public static ArrayList<ArrayList<Double>> allBatche = new ArrayList<ArrayList<Double>>();
	public static ArrayList<ArrayList<Double>> finalBatche = new ArrayList<ArrayList<Double>>();
	public static double moyenne = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int N = 3;
        double[][] A = { { 1, 0.25,  0.25 },
                         { 0.25, 1,  0.25 },
                         { 0.25, 0.25, 1 }
                       };
        Cholesky choleskyDecomposition = new Cholesky(A);
		double[][] L = choleskyDecomposition.getL;
		for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(L[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
        
		/*
		RealMatrix realMatrix = MatrixUtils.createRealMatrix(A);
		EigenDecomposition egDecomposition = new EigenDecomposition(realMatrix);
		
		final RealMatrix DMatrix = egDecomposition.getD();
		final RealMatrix VMatrix = egDecomposition.getV();
		final RealMatrix VTMatrix = egDecomposition.getVT();
		
		RealMatrix B = VMatrix.multiply(VMatrix.transpose());
				
		double [] realEigenvalues  = egDecomposition.getRealEigenvalues();
		
		//RealMatrix sqrtDMatrix = getSr(realEigenvalues);
		//final RealMatrix aMatrix = sqrtDMatrix.multiply(VTMatrix);
		double[][] datas = DMatrix.getData();
		
		for (double[] data : datas) {
            System.out.println("  ");
            for (double number : data) {
                System.out.print(number + ",");
            }
        }*/
	
		
		Matrix MatrixCorrelation = new Matrix(3, 3);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j <= i; j++) {
				if (i == j) {
					MatrixCorrelation.set(i, j, 1.);
				} else {
					MatrixCorrelation.set(i, j, 0.25);
					MatrixCorrelation.set(j, i, 0.25);
				}
			}
		}
		CholeskyDecomposition choleskyDecomposition2 = new CholeskyDecomposition(MatrixCorrelation);
		Matrix L1 = choleskyDecomposition2.getL();
		Matrix L1T = L1.transpose();
		
		L1.times(L1T).print(1, 2);

		
		EigenvalueDecomposition eigen = MatrixCorrelation.eig();
		Matrix V = eigen.getV();
		Matrix D = eigen.getD();
		double [] realEigenvalue  = eigen.getRealEigenvalues();
		Matrix sqrtD = getSqrt(realEigenvalue);
		//D.print(1, 4);
		//V.times(V.transpose()).print(1, 4);
		//sqrtD.print(1, 4);
		Matrix A1=  sqrtD.times(V.transpose());
		Matrix A1T = A1.transpose();
		A1.print(1, 4);
		A1T.times(A1).print(1, 2);
	}

	public static Matrix getSqrt(double [] realEigenvalues) {		
		Matrix result = new Matrix(realEigenvalues.length, realEigenvalues.length);
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
}
