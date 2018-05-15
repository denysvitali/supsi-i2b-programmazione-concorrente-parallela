package it.denv.supsi.progconc.serie.serie9.es4;

import java.util.concurrent.ThreadLocalRandom;

public class Barber implements Runnable {
    BarberShop barberShop;

    Barber(BarberShop bs){
        barberShop = bs;
    }

    private boolean isWaitingRoomFree(){
        barberShop.getLock().lock();
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(50,100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return barberShop.getWaitingRoom().isEmpty();
    }

    private void doHaircut(Customer c){
        System.out.println("Doing haircut to " + c);
        barberShop.setBarberStatus(BarberStatus.WORKING);
        barberShop.getLock().unlock();
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(500,1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        barberShop.setBarberStatus(BarberStatus.JUST_ENDED);
        System.out.println("Haircut done!");
    }

    @Override
    public void run() {
        while(true) {
            Customer c = barberShop.getNotifierCustomer();
            barberShop.getLock().lock();
            if (c != null) {
                barberShop.setNotifierCustomer(null);
                doHaircut(c);
            } else {
                if (!isWaitingRoomFree()) {
                    c = barberShop.getWaitingRoom().poll();
                    doHaircut(c);
                } else {
                    barberShop.getLock().unlock();
                    barberShop.setBarberStatus(BarberStatus.SLEEPING);
                    System.out.println("Sleeping...");
                    try {
                        synchronized (barberShop.getLock()) {
                            barberShop.getLock().wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
