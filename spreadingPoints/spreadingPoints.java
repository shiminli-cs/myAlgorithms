/* This is the implementation of an algorithm of my paper:
 * Algorithms for Minimizing the Movements of Spreading Points in Linear Domains
 * URL: http://cccg.ca/proceedings/2015/08.pdf
 * 
 * Author: Shimin Li
 *  Email: shiminli@aggiemail.usu.edu
 */

import java.io.*;
import java.util.Arrays;
import java.lang.Integer;

public class spreadingPoints {
    public spreadingPoints() {}
    public static void useage() {
        System.out.println("java spreadingPoints file delta [cycle_length]");
    }
    private static int MAX_POINTS=128;
    public static void main( String[] args ) {
        float[] points;
        points = new float[MAX_POINTS];
        float[] dp;
        dp = new float[MAX_POINTS]; // moving distances of points
        
        boolean cycle = false;

        int i = 0;
        int count=0;
        
        float delta=0;
        float d = 0;
        float dmax = 0;
        float cycle_length = 0;
        
        if (args.length == 0) {
            useage();
            return;
        }

        if (args.length == 3) {
            cycle = true;
            cycle_length = Float.parseFloat(args[2]);
        }
        
        delta = Float.parseFloat(args[1]);
        
        try {
            // Open the file containing the input data
            FileInputStream finput = new FileInputStream(args[0]);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(finput);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            // Read File Line By Line
            while ((strLine = br.readLine()) != null)   {
                if (i == MAX_POINTS) {
                    System.err.println("Error: too many points...");
                    System.exit(1);
                }

                if (strLine.charAt(0) != '#') {
                    points[i]=Float.parseFloat(strLine);
                    i++;
                }
            }
            count = i;
            in.close();
        } catch (Exception e) { //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        // sort the input points, then move them from left to right
        System.out.println("Input Points:");
        for (i=0; i<count; i++) {
            System.out.println(i + ": " + points[i]);
        }
        Arrays.sort(points, 0, count);
        
        // run the first round as it is a line version
        dp[0]=0; // don't need to move p1
        for (i=1; i<count; i++) {
            dp[i]=points[i-1]+delta-points[i];
            if (dp[i]>0) {
                points[i]=points[i-1]+delta;
            } else {
                dp[i]=0;
            }

            if (dmax < dp[i]) {
                dmax = dp[i];
            }
        }

        // extra operations for cycle version, if the last point got too
        // close to p1.
        if (cycle && points[count-1]>cycle_length-delta) {
            if (cycle_length/count<delta) {
                System.err.println("It is IMPOSSILBE...");
                System.exit(1);
            }

            // second round for the cycle version, if the last point got too
            // close to p1.
            dp[0]=(points[count-1]+delta)%cycle_length - points[0];
            points[0]+=dp[0];

            if (dmax<dp[0]) { dmax = dp[i]; }

            for (i=1; i<count; i++) {
                d=points[i-1]+delta-points[i];
                if (d>0) {
                    points[i]=points[i-1]+delta;
                    dp[i]+=d;
                } else {
                    break;
                }

                if (dmax < dp[i]) { dmax = dp[i]; }
            }

            // enlarge the coordinates so that the would not be minus
            for (i=0; i<count; i++) {
                points[i]+=cycle_length;
            }
        }

        // shift back all points by dmax/2
        dmax = dmax / 2;
        System.out.println("Maximum Moving Distance: "+dmax);
        for (i=0; i<count; i++) {
            points[i]=points[i]-dmax;
        }

        // change back to the cycle coordinates for cycle version
        if (cycle) {
            for (i=0; i<count; i++) {
                points[i]=points[i]%cycle_length;
            }
        }

        // output the results
        System.out.println("Final Positions of Points:");
        for (i=0; i<count; i++) {
            System.out.println(i + ": " + points[i]);
        }
        
        return;
    }
}
