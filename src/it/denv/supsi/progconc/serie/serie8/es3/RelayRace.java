package it.denv.supsi.progconc.serie.serie8.es3;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RelayRace {

    public static Lock lock = new ReentrantLock();
    public static final ArrayList<Team> teams = new ArrayList<>();
    public static final ConcurrentLinkedQueue<Team> endPositions = new ConcurrentLinkedQueue<>();

    public static void main(String[] args){
        teams.add(new Team(0));
        teams.add(new Team(1));
        teams.add(new Team(2));
        teams.add(new Team(3));

        for(Team t : teams){
            for(int i=0; i<10; i++){
                t.addRunner(new Runner(i,t));
            }
            t.getRunner(0).setHasBaton(true);
        }

        for(Team t : teams){
            t.setStart(true);
            t.start();
        }

        synchronized (teams) {
            teams.notifyAll();
        }

        for(Team t : teams){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
        System.out.println("Result: ");
        int pos = 1;
        for(Team t : endPositions){
            System.out.println(pos + ". Team " + t.getNumber());
            pos++;
            if(pos > 3){ break; }
        }

    }
}
