package it.denv.supsi.progconc.serie.serie9.es4;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

public class SleepingBarber {
    private ConcurrentLinkedQueue<Customer> customers;
    public static void main(String[] args){
        BarberShop bs = new BarberShop();
        Barber b = new Barber(bs);

        System.out.println("Starting Simulation");

        (new Thread(b)).start();


        while(true){
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(450, 700));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Customer c = new Customer(bs);
            (new Thread(c)).start();
        }

    }
}
