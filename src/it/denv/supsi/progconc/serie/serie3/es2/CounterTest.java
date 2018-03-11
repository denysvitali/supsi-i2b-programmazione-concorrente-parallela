package it.denv.supsi.progconc.serie.serie3.es2;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Counter {
    public static volatile int value = 0;
    public static ReadWriteLock lock = new ReentrantReadWriteLock();
}

class Sensor extends Thread {
    private int threshold;

    public Sensor(int threshold){
        this.threshold = threshold;
    }
    @Override
    public void run() {
        while(Counter.value < threshold){ }
        System.out.println(String.format("Counter.value = %d  > %d - resetting!", Counter.value, threshold));
        Counter.lock.writeLock().lock();
        Counter.value = 0;
        Counter.lock.writeLock().unlock();
    }
}

public class CounterTest {
    public static void main(String[] args){
        ArrayList<Sensor> sensors = new ArrayList<>();
        for(int i = 0; i<10; i++) {
            sensors.add(new Sensor((i+1) * 10));
        }

        for(Sensor sensor : sensors){
            System.out.println("Starting");
            sensor.start();
        }

        Random r = new Random();
        while(Counter.value < 120){
            Counter.lock.writeLock().lock();
            Counter.value += r.nextInt(7) + 1;
            Counter.lock.writeLock().unlock();

            try {
                Thread.sleep(r.nextInt(5) + 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Programma terminato");
        System.exit(0);
    }
}
