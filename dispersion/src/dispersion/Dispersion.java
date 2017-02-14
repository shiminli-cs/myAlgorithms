/* This is the implementation of an algorithm of my paper:
 * Dispersing Points on Intervals
 * URL:
 * http://drops.dagstuhl.de/opus/volltexte/2016/6824/pdf/LIPIcs-ISAAC-2016-52.pdf
 * 
 * Author: Shimin Li
 *  Email: shiminli@aggiemail.usu.edu
 */

package dispersion;

import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.LinkedList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shimin
 */
public class Dispersion {
    
    private static LinkedList<Interval> interval_list;
    private static LinkedList<Interval> critical_list;
    private static ListIterator<Interval> cur_intvl;
    private static ListIterator<Interval> done_intvl;
    private static Interval iFirst, iLast, iNew;
    private static double dmin;
    
    public Dispersion() {}
    
    public static void useage() {
        System.out.println("java Dispersion input_file [cycle_length]");
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean cycle = false;
        
        String endpoints[];
        Interval intvl;
        interval_list = new LinkedList<>();
        
        if (args.length == 0) {
            useage();
            return;
        }

        if (args.length == 2) { cycle = true; }
        
        try {
            // Open the file containing the input data
            FileInputStream finput = new FileInputStream(args[0]);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(finput);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            // Read File Line By Line
            while ((strLine = br.readLine()) != null)   {
                if (strLine.charAt(0) != '#') {
                    endpoints = strLine.split(",");

                    if (endpoints.length>2) {
                        System.err.println("Format error in the input file.");
                        System.exit(1);
                    }

                    interval_list.add(
                                new Interval(Double.parseDouble(endpoints[0]),
                                             Double.parseDouble(endpoints[1])));
                }
            }
            in.close();
        } catch (Exception e) { //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        // Output the loaded intervals
        System.out.println("Input Intervals:");
        Interval iArray[] = new Interval[interval_list.size()];
        iArray = interval_list.toArray(iArray);
        for (Interval it : iArray) {
            System.out.println(it.getLeftEndpoint() + " -- " + it.getRightEndpoint());
        }

        // sort the input intervals by their left endpoints
        interval_list.sort( new Comparator<Interval>() {
            public int compare(Interval i1, Interval i2) {
                return i1.compareTo(i2);
            }
        });

        // Output the sorted intervals
        System.out.println("Sorted Intervals:");
        iArray = new Interval[interval_list.size()];
        iArray = interval_list.toArray(iArray);
        for (Interval it : iArray) {
            System.out.println(it.getLeftEndpoint() + " -- " + it.getRightEndpoint());
        }
        
        // run the first round as it is a line version
        critical_list = new LinkedList<>();
        cur_intvl = interval_list.listIterator();
        done_intvl = interval_list.listIterator();

        // Initialization
        
        if (cur_intvl.hasNext()) {
            iFirst = getNextwIndex(cur_intvl);
        } else {
            System.out.println("No interval in the input.");
            return;
        }
        
        if (cur_intvl.hasNext()) {
            iLast = getNextwIndex(cur_intvl);
        } else {
            System.out.println("Only one interval in the input.");
            return;
        }

        dmin = iLast.getRightEndpoint() - iFirst.getLeftEndpoint();
        
        try {
            iFirst.setPoint(iFirst.getLeftEndpoint());
            iLast.setPoint(iLast.getRightEndpoint());
        } catch (NotOnIntervalException ex) {
            Logger.getLogger(Dispersion.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        critical_list.addLast(iFirst);
        critical_list.addLast(iLast);
        
        // the main process
        while (cur_intvl.hasNext()) {
            iNew = getNextwIndex(cur_intvl);
            
            // the first case
            if (iLast.getPoint()+dmin <= iNew.getLeftEndpoint()) {
                try {
                    iNew.setPoint(iNew.getLeftEndpoint());
                } catch (NotOnIntervalException ex) {
                    Logger.getLogger(Dispersion.class.getName()).log(Level.SEVERE, null, ex);
                }

                clearCriticalList();
                critical_list.addFirst(iNew);
                iFirst = iNew;
                iLast = iNew;
            // the second case
            } else if (iLast.getPoint()+dmin <= iNew.getRightEndpoint()) {
                try {
                    iNew.setPoint(iLast.getPoint()+dmin);
                } catch (NotOnIntervalException ex) {
                    Logger.getLogger(Dispersion.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                pollLastCriticalList();
                critical_list.addLast(iNew);
                iLast = iNew;
            // the third case
            } else {
                try {
                    iNew.setPoint(iNew.getRightEndpoint());
                } catch (NotOnIntervalException ex) {
                    Logger.getLogger(Dispersion.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                pollLastCriticalList();
                critical_list.addLast(iNew);
                iLast = iNew;
                
                ListIterator<Interval> tl = critical_list.listIterator();
                tl.next();
                Interval iSecond = tl.next();
                while (critical_list.size()>2 &&
                       ((iLast.getRightEndpoint()-iFirst.getLeftEndpoint())
                       /(iLast.getIndex() - iFirst.getIndex())) <= 
                       ((iSecond.getLeftEndpoint()-iFirst.getLeftEndpoint())
                       /(iSecond.getIndex() - iFirst.getIndex()))) {
                    pollCriticalList();
                    iFirst = iSecond;
                    iSecond = tl.next();
                }
                
                dmin = (iLast.getRightEndpoint()-iFirst.getLeftEndpoint())
                       /(iLast.getIndex() - iFirst.getIndex());
            }
        }
        
        clearCriticalList();
        output();
    }
    
    private static Interval getNextwIndex(ListIterator<Interval> iter) {
        int i = iter.nextIndex();
        Interval it = iter.next();
        it.setIndex(i);
        
        return it;
    }
    
    private static void pollCriticalList() {
        double d;
        Interval ci1, ci2, it;
        
        ci1 = critical_list.poll();
        ci2 = critical_list.peek();
        
        d = (ci2.getLeftEndpoint()-ci1.getLeftEndpoint()) /
            (ci2.getIndex()-ci1.getIndex());
        
        if (d<dmin) {dmin = d;}
        
        do {
            it = done_intvl.next();
            try {
                if (it == ci2) {
                    it.setPoint(it.getLeftEndpoint());  // let p right on the left endpoint
                } else {
                    it.setPoint(ci1.getLeftEndpoint()+d*(it.getIndex()-ci1.getIndex()));
                }
            } catch (NotOnIntervalException ex) {
                Logger.getLogger(Dispersion.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (it != ci2);
    }
    
    private static void clearCriticalList() {
        if (critical_list.size() >= 2) {
            double d;
            Interval it;

            if (Math.abs(iLast.getRightEndpoint()-iLast.getPoint())
                           > Constants.EQUALITY_TOLERANCE) {
                d = dmin;
            } else {
                d = (iLast.getRightEndpoint()-iFirst.getLeftEndpoint()) /
                    (iLast.getIndex()-iFirst.getIndex());

                if (d<dmin) {dmin = d;}
            }
            
            do {
                it = done_intvl.next();
                try {
                    if (it != iLast) {
                        it.setPoint(iFirst.getLeftEndpoint()+d*(it.getIndex()-iFirst.getIndex()));
                    }
                } catch (NotOnIntervalException ex) {
                    Logger.getLogger(Dispersion.class.getName()).log(Level.SEVERE, null, ex);
                }
            } while (it != iLast);
        } else {    // critical_list.size() == 1
            try {
                iFirst.setPoint(iFirst.getLeftEndpoint());
            } catch (NotOnIntervalException ex) {
                Logger.getLogger(Dispersion.class.getName()).log(Level.SEVERE, null, ex);
            }
            done_intvl.next();
        }
        
        critical_list.clear();
    }
    
    // maintain the property of the critical list by removing some elements from the tail
    private static void pollLastCriticalList() {
        int rCnt = 0;
        
        Iterator<Interval> inv_cl = critical_list.descendingIterator();
        inv_cl.next();
        Interval preLast = inv_cl.next();
        while ((iNew.getLeftEndpoint()-preLast.getLeftEndpoint())
              /(iNew.getIndex() - preLast.getIndex()) > 
               (iLast.getLeftEndpoint()-preLast.getLeftEndpoint())
              /(iLast.getIndex() - preLast.getIndex())) {
            rCnt++;
            iLast = preLast;
            if (inv_cl.hasNext()) {
                preLast = inv_cl.next();
            } else {
                break;
            }
        }
        
        inv_cl = critical_list.descendingIterator();
        while (rCnt-- != 0) {
            inv_cl.next();
            inv_cl.remove();
        }
    }
    
    private static void output() {
        System.out.println("\nSolution:\nthe minimum distance is "+dmin);
        
        cur_intvl = interval_list.listIterator();
        while (cur_intvl.hasNext()) {
            iFirst = cur_intvl.next();
            System.out.println("["+iFirst.getLeftEndpoint()+", "
                              +iFirst.getRightEndpoint()+"] P: "
                              +iFirst.getPoint());
        }
    }
}
