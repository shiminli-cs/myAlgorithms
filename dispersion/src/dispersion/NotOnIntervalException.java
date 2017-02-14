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
public class NotOnIntervalException extends Exception {

    /**
     * Creates a new instance of <code>NotOnIntervalException</code> without
     * detail message.
     */
    public NotOnIntervalException() {
    }

    /**
     * Constructs an instance of <code>NotOnIntervalException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NotOnIntervalException(String msg) {
        super(msg);
    }
}
