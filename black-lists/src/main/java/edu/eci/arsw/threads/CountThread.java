package edu.eci.arsw.threads;

/**
 * This class represents a thread that prints a range of integers
 * from A to B on the console.
 *
 * <p>
 * Each {@code CountThread} instance receives two integers
 * in its constructor and, when started, it prints all numbers
 * within that range, including the boundaries.
 * </p>
 *
 * @author sergio.bejarano-r
 * @author laura.rsanchez
 */
public class CountThread extends Thread {

    private int A;
    private int B;

    /**
     * Creates a new {@code CountThread} with the specified range.
     *
     * @param A lower bound of the range (inclusive).
     * @param B upper bound of the range (inclusive).
     */
    public CountThread(int A, int B) {
        this.A = A;
        this.B = B;
    }

    /**
     * Starts the execution of the thread by printing the range of numbers.
     */
    @Override
    public void run() {
        for (int i = A; i <= B; i++) {
            System.out.println(i);
        }
    }
}
