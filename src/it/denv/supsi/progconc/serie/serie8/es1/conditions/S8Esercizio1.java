package it.denv.supsi.progconc.serie.serie8.es1.conditions;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class S8Esercizio1 {
	final static int[][] matrix = new int[10][10];
	final static int[] rowSum = new int[matrix.length];
	final static int[] colSum = new int[matrix[0].length];
	final static Lock lock = new ReentrantLock();
	final static Condition rowSumNotDone = lock.newCondition();
	final static Condition columnSumNotDone = lock.newCondition();

	static volatile AtomicInteger rowDone = new AtomicInteger(0);
	static volatile AtomicInteger colDone = new AtomicInteger(0);

	public static boolean rowSumEnd = false;
	public static boolean colSumEnd = false;

	public static void main(String[] args) {
		// Inizializza matrice con valori random
		initMatrix();

		// Stampa matrice
		System.out.println("Matrice:");
		printMatrix();

		ArrayList<Thread> tal = new ArrayList<>();

		// Calcola somma delle righe
		for (int row = 0; row < matrix.length; row++){
			tal.add(new Thread(new RowSum(row)));
		}

		for(Thread t : tal){
			t.start();
		}

		lock.lock();
		try {
			while (!rowSumEnd) {
				try {
					rowSumNotDone.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} finally {
			lock.unlock();
		}

		tal = new ArrayList<>();

		System.out.printf("Row Sum Done!");

		// Stampa somma delle righe
		System.out.println("Somme delle righe:");
		printArray(rowSum);

		// Calcola somma delle colonne
		for (int col = 0; col < matrix[0].length; col++) {
			tal.add(new Thread(new ColSum(col)));
		}

		for(Thread t : tal){
			t.start();
		}

		lock.lock();
		try {
			while (!colSumEnd) {
				try {
					columnSumNotDone.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} finally {
			lock.unlock();
		}

		// Stampa somma delle colonne
		System.out.println("Somme delle colonne:");
		printArray(colSum);
	}

	public static int sumRow(final int row) {
		int result = 0;
		for (int col = 0; col < matrix[row].length; col++)
			result += matrix[row][col];
		return result;
	}

	public static int sumColumn(final int row) {
		int temp = 0;
		for (int col = 0; col < matrix.length; col++)
			temp += matrix[col][row];
		return temp;
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

	private static void printArray(final int[] array) {
		for (int i = 0; i < array.length; i++)
			System.out.print(array[i] + "\t");
		System.out.println();
	}

	private static class RowSum implements Runnable {

		private int row = -1;

		public RowSum(int row){
			this.row = row;
		}

		@Override
		public void run() {
			int result = sumRow(row);
			rowSum[row] = result;
			int parsed = rowDone.incrementAndGet();
			if (parsed == matrix.length){
				lock.lock();
				rowSumEnd = true;
				rowSumNotDone.signal();
				lock.unlock();
			}
		}
	}

	private static class ColSum implements Runnable {
		private int col = -1;

		public ColSum(int col) {
			this.col = col;
		}

		@Override
		public void run() {
			int result = sumColumn(col);
			colSum[col] = result;
			int parsed = colDone.incrementAndGet();
			if (parsed == matrix.length){
				lock.lock();
				colSumEnd = true;
				columnSumNotDone.signal();
				lock.unlock();
			}
		}
	}
}
