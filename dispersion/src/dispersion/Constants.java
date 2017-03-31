/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dispersion;

/**
 *
 * @author Shimin
 */
public class Constants {
    // The minimum value between two different numbers
    public static double EQUALITY_TOLERANCE = 0.000001;
    
    public static boolean tolerantEqual (double n1, double n2) {
        return Math.abs(n1-n2)<=Constants.EQUALITY_TOLERANCE;
    }
}
