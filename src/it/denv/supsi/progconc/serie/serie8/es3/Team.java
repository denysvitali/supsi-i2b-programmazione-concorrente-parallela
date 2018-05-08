package it.denv.supsi.progconc.serie.serie8.es3;

import java.util.ArrayList;
import java.util.concurrent.Phaser;

public class Team extends Thread {
    ArrayList<Runner> runners = new ArrayList<>();
    Phaser phaser = new Phaser(10);
    private int number = 0;
    private int currentRunner = 0;
    private long millisStart;
    private long millisEnd;
    private volatile boolean start = false;

    public Team(int number){
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void addRunner(Runner r){
        if(runners.size() < 10) {
            runners.add(r);
        }
    }

    public Runner getRunner(int i) {
        if(i >= runners.size()){
            return null;
        }
        return runners.get(i);
    }

    public ArrayList<Runner> getRunners() {
        return runners;
    }

    @Override
    public void run() {
        millisStart = System.currentTimeMillis();
        for(int i = 0; i< runners.size(); i++){
            Runner r = getRunner(i);
            currentRunner = i;
            if(i == 0){
                synchronized (RelayRace.teams) {
                    while (!start) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println("Team " + number + " started!");
            }

            r.start();
            try {
                r.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Runner newRunner = getRunner(i+1);
            if(newRunner != null){
                newRunner.setHasBaton(true);
            } else {
                // End!
            }
        }
        millisEnd = System.currentTimeMillis();;
        System.out.println("Team " + number + " ended! Took " + (millisEnd-millisStart) + " ms");
        RelayRace.endPositions.add(this);
    }

    public void setStart(boolean b) {
        start = b;
    }

    public boolean isLastRunner() {
        return currentRunner == (runners.size()-1);
    }
}
