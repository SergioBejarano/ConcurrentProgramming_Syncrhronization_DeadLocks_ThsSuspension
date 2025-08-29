/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.List;

/**
 * 
 * @author sergio.bejarano-r
 * @author laura.rsanchez
 */
public class Main {
    
    public static void main(String a[]) {
        HostBlackListsValidator hblv = new HostBlackListsValidator();
        List<Integer> blackListOccurrences = null;

        String ipaddress = "200.24.34.55";
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Available cores: " + cores);
        long start = 0;
        long end = 0;

        // === Part III - Performance Evaluation ===

        // 1 thread
        System.out.println("\n ///// Ejecutando con 1 hilo /////");
        start = System.currentTimeMillis();
        blackListOccurrences = hblv.checkHost(ipaddress, 1);
        end = System.currentTimeMillis();
        System.out.println("The host was found in the following blacklists: " + blackListOccurrences);
        System.out.println("Execution time: " + (end - start));

        // Number of threads equal to number of cores
        System.out.println("\n ///// Ejecutando con " + cores + " hilos /////");
        start = System.currentTimeMillis();
        blackListOccurrences = hblv.checkHost(ipaddress, cores);
        end = System.currentTimeMillis();
        System.out.println("The host was found in the following blacklists: " + blackListOccurrences);
        System.out.println("Execution time: " + (end - start));

        // Number of threads equal to number of cores doubled
        System.out.println("\n ///// Ejecutando con " + cores*2 + " hilos /////");
        start = System.currentTimeMillis();
        blackListOccurrences = hblv.checkHost(ipaddress, cores*2);
        end = System.currentTimeMillis();
        System.out.println("The host was found in the following blacklists: " + blackListOccurrences);
        System.out.println("Execution time: " + (end - start));

        // 50 thread
        System.out.println("\n ///// Ejecutando con 50 hilos /////");
        start = System.currentTimeMillis();
        blackListOccurrences = hblv.checkHost(ipaddress, 50);
        end = System.currentTimeMillis();
        System.out.println("The host was found in the following blacklists: " + blackListOccurrences);
        System.out.println("Execution time: " + (end - start));

        // 100 thread
        System.out.println("\n ///// Ejecutando con 100 hilos /////");
        start = System.currentTimeMillis();
        blackListOccurrences = hblv.checkHost(ipaddress, 100);
        end = System.currentTimeMillis();
        System.out.println("The host was found in the following blacklists: " + blackListOccurrences);
        System.out.println("Execution time: " + (end - start));

        // === Part II - Black List Search ===

        // System.out.println("Checking host: 200.24.34.55");
        // blackListOccurrences = hblv.checkHost("200.24.34.55", 10);
        // System.out.println("The host was found in the following blacklists: " + blackListOccurrences);
        //
        // System.out.println("Checking host: 202.24.34.55");
        // blackListOccurrences = hblv.checkHost("202.24.34.55", 10);
        // System.out.println("The host was found in the following blacklists: " + blackListOccurrences);
        //
        // System.out.println("Checking host: 212.24.24.55");
        // blackListOccurrences = hblv.checkHost("212.24.24.55", 10);
        // System.out.println("The host was found in the following blacklists: " + blackListOccurrences);
    }
    
}
