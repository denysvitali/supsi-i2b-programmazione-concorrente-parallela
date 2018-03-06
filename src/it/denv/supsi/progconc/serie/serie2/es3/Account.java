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

        if(this.amount + this.amount_dec * 0.1 == 0.0){
            System.out.println("Il conto è vuoto...");
            getLock().unlock();
            return false;
        }

        System.out.println(String.format("%s, Bilancio del conto: %s", actor, getBalance()));

        if (amount + amount_dec * 0.1 > (this.amount + this.amount_dec * .1)) {
            return withdraw(actor, amount, amount_dec, true);
        }

        return withdraw(actor, amount, amount_dec, false);
    }

    public boolean withdraw(User actor, int amount, int amount_dec, boolean full) {

        if(this.amount + this.amount_dec * 0.1 == 0.0){
            getLock().unlock();
            return false;
        }

        this.amount -= amount;
        this.amount_dec -= amount_dec;

        if(full) {
            System.out.println(
                    String.format("%s preleva i soldi rimanenti sul conto (%d.%02d)", actor, amount, amount_dec)
            );
            this.amount = 0;
            this.amount_dec = 0;
        }

        System.out.println(String.format("%s, bilancio dopo il prelievo: %s", actor, getBalance()));

        actor.addAmount(amount, amount_dec);
        getLock().unlock();
        return true;
    }

    public String getBalance() {
        return String.format("%d.%02d", amount, amount_dec);
    }
}
