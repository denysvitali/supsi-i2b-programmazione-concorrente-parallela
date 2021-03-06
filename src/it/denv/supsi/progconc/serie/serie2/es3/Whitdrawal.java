package it.denv.supsi.progconc.serie.serie2.es3;

import java.util.ArrayList;
import java.util.Random;

public class Whitdrawal {
    public static void main(String args[]){
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("Denys", "Vitali"));
        users.add(new User("Julyan", "Chase"));
        users.add(new User("Brandi", "Cristal"));
        users.add(new User("Jace", "Kyler"));
        users.add(new User("Jessie", "Serena"));

        Account a = new Account(500,0);

        System.out.println("=== Simulation START ===");

        ArrayList<WhitdrawalProcess> whitdrawalProcesses = new ArrayList<>();
        Random r = new Random();
        for(int i=0; i < 50 ; i++){
            whitdrawalProcesses.add(new WhitdrawalProcess(users.get(i%5), a));
        }

        for(WhitdrawalProcess w : whitdrawalProcesses){
            w.start();
        }

        for(WhitdrawalProcess w : whitdrawalProcesses){
            try {
                w.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("=== Simulation END ===");

    }
}
