package it.denv.supsi.progconc.serie.serie9.es4;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BarberShop {
    private ConcurrentLinkedQueue<Customer> waitingRoom;
    private BarberStatus barberStatus = BarberStatus.SLEEPING;
    private Customer notifierCustomer = null;
    private Lock lock = new ReentrantLock();

    BarberShop(){
        waitingRoom = new ConcurrentLinkedQueue<>();
    }

    public ConcurrentLinkedQueue<Customer> getWaitingRoom() {
        return waitingRoom;
    }

    public Lock getLock() {
        return lock;
    }

    public BarberStatus getBarberStatus() {
        return barberStatus;
    }

    public void setBarberStatus(BarberStatus barberStatus) {
        this.barberStatus = barberStatus;
    }

    public void setNotifierCustomer(Customer customer) {
        notifierCustomer = customer;
    }

    public Customer getNotifierCustomer() {
        return notifierCustomer;
    }
}
