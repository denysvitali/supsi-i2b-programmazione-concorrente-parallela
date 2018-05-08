package it.denv.supsi.progconc.serie.serie8.es3;

import javax.sound.midi.Soundbank;
import java.util.concurrent.ThreadLocalRandom;

public class Runner extends Thread {
    private int number = -1;
    private Team team;
    private boolean hasBaton = false;
    private long startTime = 0;
    private long endTime = 0;

    public Runner(int number, Team t){
        this.number = number;
        this.team = t;
    }

    public int getNumber() {
        return number;
    }

    public void setHasBaton(boolean b) {
        for(Runner r : team.getRunners()){
            if(r != this) {
                r.hasBaton = false;
            }
        }
        hasBaton = b;
    }

    public boolean hasBaton() {
        return hasBaton;
    }

    @Override
    public void run() {
        System.out.println("Runner" + getNumber() + "_Team" + team.getNumber() + ": Starting!");
        startTime = System.currentTimeMillis();
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(100,150));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        endTime = System.currentTimeMillis();
        System.out.println("Runner" + getNumber() + "_Team"
                + team.getNumber() + ": End run, took " +
                (endTime-startTime)/10E3 +
                " millis");
        if(team.isLastRunner()) {
            if (RelayRace.endPositions.size() == 0) {
                System.out.println("VINTO!");
            } else {
                System.out.println("PERSO!");
            }
        }
    }
}
