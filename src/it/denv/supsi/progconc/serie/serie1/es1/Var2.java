package it.denv.supsi.progconc.serie.serie1.es1;

import java.util.ArrayList;

public class Var2 {
    public static void main(String[] args){
        ArrayList<Thread> threadList = new ArrayList<Thread>();
        for (int i = 1; i <= 5; i++) {
            Thread thread = new Thread(new MyRunnable());
            thread.start();
            threadList.add(thread);
        }

        for(Thread t : threadList){
            try {
                t.join();
                System.out.println("Thread " + t + " terminato");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
