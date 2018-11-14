/**
 * ThreadComputing.java
 * Purpose: 
 *
 * HeighMapGenerationTools
 * @author 
 * @version 1.0 
 */

package controllers;

import models.Map;
import models.algorithms.Random;
import models.algorithms.SquareDiamond;

public class ThreadComputing implements Runnable {
	
	public void run() {
		SquareDiamond SD = new SquareDiamond(9);
		SD.apply();
	}
	
}
