package it.denv.supsi.progconc.serie.serie9.es4;

import java.util.concurrent.ThreadLocalRandom;

public class Customer implements Runnable {
    BarberShop barberShop;

    public Customer(BarberShop bs) {
        barberShop = bs;
    }

    @Override
    public void run() {
        System.out.println("Customer entering the barber shop");
        if(barberShop.getBarberStatus() == BarberStatus.SLEEPING){
            barberShop.setNotifierCustomer(this);
            synchronized (barberShop.getLock()) {
                barberShop.getLock().notify();
            }
        } else {
            barberShop.getWaitingRoom().add(this);
            System.out.println("Customer is in the waiting room");
        }
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(80, 160));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
