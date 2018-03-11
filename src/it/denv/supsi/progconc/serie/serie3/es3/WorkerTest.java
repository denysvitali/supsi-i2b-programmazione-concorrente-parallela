package it.denv.supsi.progconc.serie.serie3.es3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WorkerTest {
    public static void main(String[] args) throws InterruptedException {
        ArrayList<Worker> workers = new ArrayList<>();
        int[] ints = new int[5];
        ReadWriteLock rwlock = new ReentrantReadWriteLock();

        for(int i=0; i<10; i++){
            workers.add(new Worker(ints, rwlock));
        }

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for(int i = 0; i<10000; i++) {
            for (Worker w : workers) {
                executor.execute(w);
            }
        }

        executor.shutdown();
        while(!executor.isTerminated()){}
        Arrays.stream(ints).forEach(System.out::println);

    }
}
