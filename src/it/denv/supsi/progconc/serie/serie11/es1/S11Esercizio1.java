package it.denv.supsi.progconc.serie.serie11.es1;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

class S11Es1Timer {
	private long start = -1, stop = -1;

	public void start() {
		this.start = System.currentTimeMillis();
	}

	public void stop() {
		this.stop = System.currentTimeMillis();
	}

	public long getElapsed() {
		if (start < 0 || stop < 0)
			return 0;
		return stop - start;
	}
}

class MergeSort extends RecursiveTask<int[]> {
	private int[] a;
	private int[] helper;
	private int lo;
	private int hi;

	public MergeSort(int[] unsortedInts) {
		this(unsortedInts, new int[unsortedInts.length], 0, unsortedInts.length-1);
	}

	public MergeSort(final int[] a, final int[] helper, final int lo, final int hi) {
		this.a = a;
		this.helper = helper;
		this.lo = lo;
		this.hi = hi;
	}

	private static void merge(final int[] a, final int[] helper, final int lo, final int mid, final int hi) {
		for (int i = lo; i <= hi; i++) {
			helper[i] = a[i];
		}
		int i = lo, j = mid + 1;
		for (int k = lo; k <= hi; k++) {
			if (i > mid)
				a[k] = helper[j++];
			else if (j > hi)
				a[k] = helper[i++];
			else if (helper[i] < helper[j])
				a[k] = helper[i++];
			else
				a[k] = helper[j++];
		}
	}

	@Override
	protected int[] compute() {
		if (lo >= hi) {
			return null;
		}

		final int mid = lo + (hi - lo) / 2;
		ForkJoinTask<int[]> left = (new MergeSort(a, helper, lo, mid));
		left.fork();
		ForkJoinTask<int[]> right = (new MergeSort(a, helper, mid + 1, hi));
		right.fork();

		left.join();
		right.join();
		merge(a, helper, lo, mid, hi);
		return a;
	}
}

public class S11Esercizio1 {
	static final int NUM_ELEMENTS = 134_217_728;

	public static void main(final String[] args) {

		// Generate random array
		final Random r = new Random();
		System.out.println("Generating random array");
		final int[] unsortedInts = new int[NUM_ELEMENTS];
		for (int i = 0; i < unsortedInts.length; i++)
			unsortedInts[i] = r.nextInt(10000);

		// Sort array
		System.out.println("Sorting");
		final S11Es1Timer t = new S11Es1Timer();
		t.start();
		ForkJoinPool forkJoinPool = new ForkJoinPool(Integer.valueOf(args[0]));
		forkJoinPool.invoke(new MergeSort(unsortedInts));
		forkJoinPool.shutdown();
		t.stop();

		// Check that values are sorted!
		System.out.print("Validating results :");
		if (validateResult(unsortedInts))
			System.out.println(" valid!");
		else
			System.out.println(" invalid!");
		System.out.println("Elapsed: " + t.getElapsed() + " ms");
	}

	private static boolean validateResult(final int[] sorted) {
		for (int i = 1; i < sorted.length; i++)
			if (sorted[i - 1] > sorted[i])
				return false;
		return true;
	}
}