package it.denv.supsi.progconc.serie.serie3.es3;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReadWriteLock;

public class Worker implements Runnable {

    private int[] integers;
    private ReadWriteLock lock;

    Worker(int[] integers, ReadWriteLock lock){
        this.integers = integers;
        this.lock = lock;
    }

    @Override
    public void run() {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        int pos = threadLocalRandom.nextInt(5);

        lock.writeLock().lock(); // Acquire W lock
        integers[pos] += threadLocalRandom.nextInt(40) + 10;
        if(integers[pos] > 500){
            integers[pos] = 0;
        }
        lock.writeLock().unlock();

        try {
            Thread.sleep(threadLocalRandom.nextInt(3) + 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
