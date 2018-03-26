package it.denv.supsi.progconc.serie.serie5.es1;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

class Reader implements Runnable {
	private final int id;
	private int localValue;

	public Reader(final int id) {
		this.id = id;
		this.localValue = -1;
	}

	@Override
	public void run() {
		while (Esercizio1.isRunning.get()) {
			// Update local value if needed
			int local = Esercizio1.sharedValue;
			if (local != localValue) {
				localValue = local;
			}
			else {
				System.out.println("Reader" + id + ": (" + localValue
						+ " == " + Esercizio1.sharedValue + ")");
			}
		}
	}
}

public class Esercizio1 {

	final static AtomicBoolean isRunning = new AtomicBoolean(true);
	static volatile int sharedValue = 0;

	public static void main(final String[] args) {
		final ArrayList<Thread> allThread = new ArrayList<>();
		final Random random = new Random();

		// Create threads
		for (int i = 0; i < 10; i++)
			allThread.add(new Thread(new Reader(i)));

		// Start all threads
		for (final Thread t : allThread)
			t.start();

		for (int i = 0; i < 1000; i++) {
			Esercizio1.sharedValue = random.nextInt(10);
			// Wait 1 ms between next update
			try {
				Thread.sleep(1);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Switch flag in order to terminate workers threads
		isRunning.set(false);

		// Wait for all threads to complete
		for (final Thread t : allThread)
			try {
				t.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}

		System.out.println("Simulation terminated.");
	}
}
