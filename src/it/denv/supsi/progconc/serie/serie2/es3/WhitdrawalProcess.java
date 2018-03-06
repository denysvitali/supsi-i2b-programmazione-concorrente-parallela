package it.denv.supsi.progconc.serie.serie2.es3;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class WhitdrawalProcess extends Thread {

    private User user;
    private Account account;
    private Random random;

    public WhitdrawalProcess(User user, Account account){
        this.user = user;
        this.account = account;
    }

    @Override
    public void run() {
        int amount = 5 + ThreadLocalRandom.current().nextInt(45);
        int amount_dec = 0;
        //System.out.println(String.format("%s", account.getBalance()));
        System.out.println(String.format("%s vuole prelevare %d.%02d...", user, amount, amount_dec));
        account.withdraw(user, amount, amount_dec);
        try {
            Thread.sleep(5 + (new Random()).nextInt(15));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
