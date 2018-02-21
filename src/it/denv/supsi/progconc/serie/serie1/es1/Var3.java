package it.denv.supsi.progconc.serie.serie1.es1;

import java.util.ArrayList;

public class Var3 {
    public static void main(String[] args){
        ArrayList<Thread> threadList = new ArrayList<Thread>();
        for (int i = 1; i <= 5; i++) {
            final int thread_id = i;
            Thread thread = new Thread(()->{
                long fibo1 = 1, fibo2 = 1, fibonacci = 1;
                for (int j = 3; j <= 700; j++) {
                    fibonacci = fibo1 + fibo2;
                    fibo1 = fibo2;
                    fibo2 = fibonacci;
                }
                /* Stampa risultato */
                System.out.println(thread_id + ": " + fibonacci);
            });
            thread.start();
            threadList.add(thread);

            for(Thread t : threadList){
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
