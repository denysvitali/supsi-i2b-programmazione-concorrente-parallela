package it.denv.supsi.progconc.serie.serie1.es2;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args){
        long now = System.currentTimeMillis();
        ArrayList<Thread> threadList = new ArrayList<Thread>();
        long runtimes[] = new long[2];
        for(int i=0; i<2; i++){
            runtimes[i] = (long) ((Math.random()*500)+1500);
            Thread t = new Thread(new MyRunnable(i, runtimes[i]));
            threadList.add(t);
        }

        System.out.println("Partono tutti i threads.");
        for(Thread t : threadList){
            t.start();
        }

        System.out.println("In attesa che i threads abbiano terminato.");
        for(Thread t : threadList){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Tutti i thread hanno terminato.");

    }
}
