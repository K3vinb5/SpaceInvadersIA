package controllers;

import java.util.Random;

public class RandomController implements GameController {

	private Random r;
	
	
	public RandomController(Random r) {
		super();
		this.r = r;
	}


	@Override
	public double[] nextMove(double[] currentState) {
		double [] res= new double[4];
		res[r.nextInt(3)]=1;
		if (r.nextDouble()>.5) {
			res[3] = 1;
		}
		return res;
	}

}
