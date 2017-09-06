
package de.hsp.aparapi.cluster;

import com.aparapi.Kernel;
import com.aparapi.Range;

import java.util.Random;

/**
 *
 * @author Heiko Spindler
 *
 */

public class ClusterMain {

   static public final int numberOfPoints = 200_000;

   public static void main(String[] _args) {

      System.setProperty("com.aparapi.enableExecutionModeReporting", "true");
      System.setProperty("com.aparapi.dumpProfileOnExecution", "true");
      System.setProperty("com.aparapi.enableProfiling", "true");
      System.setProperty("com.aparapi.enableShowGeneratedOpenCL", "false");

      final float[] x = new float[numberOfPoints];
      final float[] y = new float[numberOfPoints];

      /** Initialize input array. Create points. */
      Random random = new Random();

      for (int i = 0; i < numberOfPoints; i++) {
         x[i] = (float)random.nextInt(100);
         y[i] = (float)random.nextInt(100);
      }


      /** Aparapi Kernel which computes sum of distances for all
       * cluster points tested as cluster center.
       **/

      long time = System.currentTimeMillis();
      ClusterKernel kernel = new ClusterKernel(numberOfPoints, x, y );

      // Execute Kernel.
      kernel.execute(Range.create(numberOfPoints));

      System.out.println("Aparapi Time: "+(System.currentTimeMillis()-time));

      // Report target execution mode: GPU or JTP (Java Thread Pool).
      System.out.println("Device = " + kernel.getTargetDevice().getShortDescription());

      // Display computed square values.
      //showResult( x, y, kernel.getOut());

      kernel.dispose();

      time = System.currentTimeMillis();

      ClusterKernel kernel2 = new ClusterKernel(numberOfPoints, x, y);

      for ( int i = 0; i < numberOfPoints; i++)
      {
         kernel2.doRun( i );
      }
      System.out.println("CPU Time: "+(System.currentTimeMillis()-time));

      // TODO: Find cluster center: Minimal sum of distances in array out[]

      // Dispose Kernel resources.
      kernel2.dispose();
   }

   public static void showResult( float[] x, float[] y, float[] output )
   {
      // Display computed square values.
      float clusterSumMin = Float.MAX_VALUE;
      int minIndex = 0;
      for (int i = 0; i < numberOfPoints; i++) {
         System.out.printf("%8.2f %8.2f %8.2f\n", x[i], y[i], output[i]);
         if ( clusterSumMin > output[i])
         {
            clusterSumMin = output[i];
            minIndex = i;
         }
      }
      System.out.println( "Index:"+minIndex);
      System.out.printf("%8.2f %8.2f %8.2f\n", x[minIndex], y[minIndex], clusterSumMin);
   }

}
