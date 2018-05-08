package it.denv.supsi.progconc.serie.serie8.es1.notify_wait;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class S8Esercizio1 {
	final static int[][] matrix = new int[10][10];
	public static ConcurrentHashMap<Integer, Integer> rowcam = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<Integer, Integer> colcam = new ConcurrentHashMap<>();
	public static AtomicInteger tc = new AtomicInteger(0);
	public static ArrayList<Thread> tal;
	public static int status = 0;

	public static void main(String[] args) {
		// Inizializza matrice con valori random
		initMatrix();

		// Stampa matrice
		System.out.println("Matrice:");
		printMatrix();

		tal = new ArrayList<>();
		for(int i=0; i<matrix.length; i++){
		    int m_i = i;
            tal.add(new SumThread(m_i));
        }

        for(Thread t : tal){
		    t.start();
        }

        System.out.println("Notification sent");

        int rowsum = 0;
        for(Integer i : rowcam.values()){
		    rowsum += i;
        }

        // Stampa somma delle righe
        System.out.println("Somme delle righe:");
        printArray(rowcam);

        for(Thread t : tal){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

		// Stampa somma delle colonne
		System.out.println("Somme delle colonne:");
		printArray(colcam);

        System.out.println("Main End");
	}

	private static void initMatrix() {
		Random r = new Random();
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				matrix[row][col] = 1 + r.nextInt(100);
			}
		}
	}

	private static void printMatrix() {
		for (int i = 0; i < matrix.length; i++)
			printArray(matrix[i]);
	}

	private static void printArray(final ConcurrentHashMap<Integer, Integer> cam) {
		for (int i : cam.values())
			System.out.print(i + "\t");
		System.out.println();
	}

    private static void printArray(final int[] array) {
        for (int i=0; i<array.length; i++)
            System.out.print(array[i] + "\t");
        System.out.println();
    }

    private static class SumThread extends Thread {
	    private int i = 0;

	    SumThread(int i){
	        this.i = i;
        }

	    @Override
        public void run(){
            int ps = 0;
            for(int k=0; k<matrix.length; k++){
                ps += matrix[i][k];
            }
            rowcam.putIfAbsent(i, ps);
            int m_tc = tc.addAndGet(1);

            for(Thread t: tal){
                synchronized (t) {
                    t.notify();
                }
            }

            while(m_tc != 10 && status != 1){
                m_tc = tc.get();
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


            if(status == 0){
                synchronized (this){
                    status++;
                }
                System.out.println("Called only once");
                tc.set(0);
                for(Thread t : tal){
                    synchronized (t){
                        t.notify();
                    }
                }
            }
            System.out.println("End wait for T" + i);

            ps = 0;
            for (int[] aMatrix : matrix) {
                ps += aMatrix[i];
            }
            colcam.putIfAbsent(i, ps);
            m_tc = tc.incrementAndGet();

            while(m_tc != 10 && status != 2){
                m_tc = tc.get();
                synchronized (this){
                     try {
                         this.wait();
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                 }
            }

            if(status != 2){
                synchronized (this){
                    status = 2;
                }
            }
            for(Thread t : tal){
                synchronized (t){
                    t.notify();
                }
            }
            System.out.println("End T"+i+"!");

        }
    }
}
