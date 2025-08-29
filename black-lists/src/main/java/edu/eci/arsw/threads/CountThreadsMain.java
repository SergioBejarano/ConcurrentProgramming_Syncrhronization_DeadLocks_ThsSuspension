package edu.eci.arsw.threads;

/**
 * 
 * @author sergio.bejarano-r
 * @author laura.rsanchez
 */
public class CountThreadsMain {
    
    public static void main(String a[]){
   
        CountThread countThread1 = new CountThread(0, 99);
        CountThread countThread2 = new CountThread(99, 199);
        CountThread countThread3= new CountThread(199, 299);

        countThread1.run();
        countThread2.run();
        countThread3.run();
    }
    
}
