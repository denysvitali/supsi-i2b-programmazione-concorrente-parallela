package it.denv.supsi.progconc.serie.serie6.es3;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Company {
    private ArrayList<Branch> branches;
    public volatile ExchangeRates currentER;
    public ReentrantReadWriteLock readWriteLock;

    public Company() {
        branches = new ArrayList<>();
        getNewExchangeRates();

        for(int i = 0; i < 10; i++){
            branches.add(new Branch(this));
        }

        for(Branch b : branches){
            b.start();
        }

        while(true){
            getNewExchangeRates();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private ExchangeRates generateRandomER() {
        double[] er = new double[ExchangeRates.size];
        for(int i = 0; i<ExchangeRates.size; i++){
            er[i] = Math.random() + 0.5;
        }
        return new ExchangeRates(er);
    }

    public void getNewExchangeRates() {
        System.out.println("Getting new exchange rates!");
        readWriteLock.readLock().lock();
        currentER = generateRandomER();
        System.out.println("New ER: " + currentER);
        readWriteLock.readLock().unlock();
    }
}
