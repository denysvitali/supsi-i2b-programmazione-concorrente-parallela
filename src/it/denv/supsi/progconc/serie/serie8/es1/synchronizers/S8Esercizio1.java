package it.denv.supsi.progconc.serie.serie8.es1.synchronizers;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Phaser;

public class S8Esercizio1 {
	final static int[][] matrix = new int[10][10];
	final static int[] rowSum = new int[matrix.length];
	final static int[] colSum = new int[matrix[0].length];
	private static Phaser phaser = new Phaser(10);

	public static void main(String[] args) {
		initMatrix();

		System.out.println("Matrice:");
		printMatrix();

		ArrayList<Thread> tal = new ArrayList<>();

		for (int row = 0; row < matrix.length; row++) {
			tal.add(new Thread(new RowSum(row)));
		}

		for(Thread t : tal){
			t.start();
		}

		phaser.awaitAdvance(0);

		System.out.println("Somme delle righe:");
		printArray(rowSum);

		tal = new ArrayList<>();
		for (int col = 0; col < matrix[0].length; col++) {
			tal.add(new Thread(new ColSum(col)));
		}

		for(Thread t : tal){
			t.start();
		}

		phaser.awaitAdvance(1);

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
		public RowSum(int row) {
			this.row = row;
		}

		@Override
		public void run() {
			int sum = sumRow(row);
			rowSum[row] = sum;
			phaser.arrive();
		}
	}

	private static class ColSum implements Runnable {
		private int col = -1;
		public ColSum(int col) {
			this.col = col;
		}

		@Override
		public void run() {
			int sum = sumColumn(col);
			colSum[col] = sum;
			phaser.arrive();
		}
	}
}
