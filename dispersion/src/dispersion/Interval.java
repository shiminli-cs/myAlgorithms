/* This is the implementation of an algorithm of my paper:
 * Dispersing Points on Intervals
 * URL:
 * http://drops.dagstuhl.de/opus/volltexte/2016/6824/pdf/LIPIcs-ISAAC-2016-52.pdf
 * 
 * Author: Shimin Li
 *  Email: shiminli@aggiemail.usu.edu
 * 
 * Although there is already an implementation of 1-dimensional interval data
 * structure in Java, it is too complex for this special case.
 * You can find it in the following URL:
 * http://introcs.cs.princeton.edu/java/32class/Interval.java.html
 */

package dispersion;

/**
 *
 * @author Shimin
 */
public class Interval implements Comparable<Interval>{
    private int index=0;
    private double leftEndpoint=0.0;
    private double rightEndpoint=0.0;
    private double p=0.0;

    public Interval() {}

    public Interval(double l, double r) {
        leftEndpoint = l;
        rightEndpoint = r;
        p = l;      // initialize the position of p on the interval
    }

    public void shift(double d) {
        leftEndpoint += d;
        rightEndpoint += d;
        p += d;
    }
    
    public void setIndex(int i) {
        index = i;
    }

    public int getIndex() {
        return index;
    }
    
    public double getLeftEndpoint() {
        return leftEndpoint;
    }

    public double getRightEndpoint() {
        return rightEndpoint;
    }

    public void setPoint(double v) throws NotOnIntervalException {
        if (v>=leftEndpoint && v<=rightEndpoint) {
            p = v;
        } else {
            throw new NotOnIntervalException("The point is outside!");
        }
    }

    public double getPoint() {
        return p;
    }

    @Override
    public int compareTo(Interval intvl) {
        if(!Constants.tolerantEqual(leftEndpoint, intvl.getLeftEndpoint())) {
            return leftEndpoint - intvl.getLeftEndpoint() < 0 ? -1 : 1;
        } else if(!Constants.tolerantEqual(rightEndpoint, intvl.getRightEndpoint())) {
            return rightEndpoint - intvl.getRightEndpoint() < 0 ? -1 : 1;
        } else {
            return 0;
        }
    }
}
