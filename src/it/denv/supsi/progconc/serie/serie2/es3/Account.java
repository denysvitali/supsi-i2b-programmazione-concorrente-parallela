package it.denv.supsi.progconc.serie.serie2.es3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private int amount;
    private int amount_dec;
    private Lock lock;

    public Account(int initial_amount, int initial_amount_decimal) {
        this.amount = initial_amount;
        this.amount_dec = initial_amount_decimal;
        this.lock = new ReentrantLock();
    }

    public Lock getLock() {
        return lock;
    }

    public boolean withdraw(User actor, int amount, int amount_dec) {

        getLock().lock();
        if (amount + amount_dec * 0.1 > (this.amount + amount_dec * .1)) {
            return withdraw(actor, amount, amount_dec, true);
        }

        return withdraw(actor, amount, amount_dec, false);
    }

    public boolean withdraw(User actor, int amount, int amount_dec, boolean full) {
        if(this.amount == 0 && this.amount_dec == 0){
            getLock().unlock();
            return false;
        }

        this.amount -= amount;
        this.amount_dec -= amount_dec;

        actor.addAmount(amount, amount_dec);
        getLock().unlock();
        return true;
    }
}
