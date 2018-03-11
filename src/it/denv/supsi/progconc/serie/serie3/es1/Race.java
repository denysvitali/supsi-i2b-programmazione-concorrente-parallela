package it.denv.supsi.progconc.serie.serie3.es1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Worker implements Runnable {
    public volatile static boolean isRunning = false;
    public volatile static int finished = 0;
    public static Lock lock = new ReentrantLock();
    private final int id;
    private final Random random;
    private int count = 0;

    public Worker(final int id) {
        this.id = id;
        this.random = new Random();
    }

    public static int getFinishedWorkers(){
        return finished;
    }

    @Override
    public void run() {
        System.out.println("Worker" + id + " waiting to start");
        while (!isRunning) {
            // Wait!
        }

        System.out.println("Worker" + id + " started");
        for (int i = 0; i < 10; i++) {
            count += random.nextInt(40) + 10;
            try {
                Thread.sleep(random.nextInt(151) + 100);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Worker" + id + " finished");
        Worker.lock.lock();
        finished++;
        Worker.lock.unlock();
    }

    public void printResult() {
        System.out.println("Worker" + id + " reached " + count);
    }
}

public class Race {

    public static void main(String[] args) throws InterruptedException {
        final List <Worker> allWorkers = new ArrayList < > ();
        final List <Thread> allThread = new ArrayList < > ();
        for (int i = 1; i <= 10; i++) {
            final Worker target = new Worker(i);
            allWorkers.add(target);
            final Thread e = new Thread(target);
            allThread.add(e);
            e.start();
        }

        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e){

        }

        System.out.println("Main thread starting the race!");
        Worker.isRunning = true;

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(10000);
                System.out.println("Timed out.");
                allThread.forEach(Thread::interrupt);
            } catch (InterruptedException e) {
            }
        });

        t.start();


        while (Worker.getFinishedWorkers() < allWorkers.size()){
            // Wait
        }

        t.interrupt();

        for (Worker worker: allWorkers){
            worker.printResult();
        }

        for (final Thread thread: allThread){
            try {
                thread.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}