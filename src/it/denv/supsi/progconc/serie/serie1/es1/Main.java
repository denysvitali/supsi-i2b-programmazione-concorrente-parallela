package it.denv.supsi.progconc.serie.serie1.es1;

import java.util.ArrayList;
import java.util.Collection;

public class Main{
    public static void main(String[] args){
        // Proivded code from iCorsi
        Collection<Thread> allThreads = new ArrayList<Thread>();

        /* Creazione dei threads */
        for (int i = 1; i <= 5; i++) {
            System.out.println("Main: creo thread " + i);
            Thread t = new Thread() {

                @Override
                public void run() {
                    long fibo1 = 1, fibo2 = 1, fibonacci = 1;
                    for (int i = 3; i <= 700; i++) {
                        fibonacci = fibo1 + fibo2;
                        fibo1 = fibo2;
                        fibo2 = fibonacci;
                    }
                    /* Stampa risultato */
                    System.out.println(this + ": " + fibonacci);
                }
            };
            allThreads.add(t);
        }

        /* Avvio dei threads */
        for (Thread t : allThreads)
            t.start();

        /* Attendo terminazione dei threads */
        for (Thread t : allThreads) {
            try {
                System.out.println("Attendo la terminazione di " + t);
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}