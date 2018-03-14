package it.denv.supsi.progconc.serie.serie4.es1.part2;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Classe usata per misurare tempo d'esecuzione
class S4Es1Timer {
	private long startTime = -1;
	private long endTime = -1;

	public void start() {
		startTime = System.currentTimeMillis();
	}

	public void stop() {
		endTime = System.currentTimeMillis();
	}

	public long getElapsedTime() {
		if (startTime < 0 || endTime < 0)
			return 0;
		return endTime - startTime;
	}
}

// Classe che si occupa di effettuare scritture ad uno stato condiviso
class S4Es1Updater implements Runnable {
	final private int delay;
	final private int id;

	public S4Es1Updater(final int id, final int delay) {
		this.id = id;
		this.delay = delay;
	}

	@Override
	public void run() {
		log("started");
		while (true) {
			final long curValue = RWLocks.increment();
			log("value incremented to " + curValue);

			if (curValue >= RWLocks.NUM_UPDATES)
				break;

			try {
				Thread.sleep(delay);
			} catch (final InterruptedException e) {
				/* do nothing */
			}
		}
		log("finished");
	}

	private final void log(final String message) {
		System.out.println(getClass().getSimpleName() + id + " :" + message);
	}
}

class S4Es1Worker implements Runnable {
	final private int id;
	final private S4Es1Timer timer;
	private long localValue = 0;
	private long reads = 0;
	private long changes = 0;

	public S4Es1Worker(final int id) {
		this.id = id;
		this.timer = new S4Es1Timer();
		this.localValue = RWLocks.getValue();
	}

	@Override
	public void run() {
		log("started");
		timer.start();

		while (true) {
			final long curShared = RWLocks.getValue();

			try {
				Thread.sleep(1);
			} catch (final InterruptedException e) { }

			reads++;
			if (localValue != curShared) {
				localValue = curShared;
				changes++;
			}

			if (curShared >= RWLocks.NUM_UPDATES)
				break;
		}

		timer.stop();
		log("finished");
	}

	private final void log(final String message) {
		System.out.println(getClass().getSimpleName() + id + " :" + message);
	}

	public void logResults() {
		log("time:\t" + timer.getElapsedTime() + "\tms. Reading done:\t"
				+ reads + "\t. Changes recognized:\t" + changes);
	}
}

public class RWLocks {
	private static long value = 0;
	public static ReadWriteLock lock = new ReentrantReadWriteLock();

	public static long increment() {
		lock.writeLock().lock();
		value++;
		lock.writeLock().unlock();
		try {
			Thread.sleep(10);
		} catch (final InterruptedException e) { }
		return value;
	}

	public static long getValue() {
		lock.readLock().lock();
		try {
			Thread.sleep(1);
		} catch (final InterruptedException e) { }
		long rval = value;
		lock.readLock().unlock();
		return rval;
	}

	// Limite updates, oltre al quale i thread terminano
	final public static int NUM_UPDATES = 100;

	public static void main(final String[] args) {
		final S4Es1Timer mainTimer = new S4Es1Timer();
		final ArrayList<Thread> threads = new ArrayList<Thread>();
		final ArrayList<S4Es1Worker> workers = new ArrayList<S4Es1Worker>();

		// Crea 3 Updaters che si occupano di incrementare il contatore
		// condiviso secondo le proprie tempistiche
		threads.add(new Thread(new S4Es1Updater(0, 200)));
		threads.add(new Thread(new S4Es1Updater(1, 250)));
		threads.add(new Thread(new S4Es1Updater(2, 300)));

		// Crea 10 Workers
		for (int i = 0; i < 10; i++) {
			final S4Es1Worker worker = new S4Es1Worker(i);
			workers.add(worker);
			threads.add(new Thread(worker));
		}

		System.out.println("Simulation started");
		System.out.println("------------------------------------");

		mainTimer.start();

		// Fa partire tutti i threads
		for (final Thread t : threads)
			t.start();

		try {
			// Attende che tutti i threads terminano
			for (final Thread t : threads)
				t.join();
		} catch (final InterruptedException e) {
			/* do nothing */
		}
		mainTimer.stop();

		// Stampa tempi d'esecuzione
		for (final S4Es1Worker worker : workers)
			worker.logResults();

		System.out.println("Simulation took: " + mainTimer.getElapsedTime()
				+ " ms");
		System.out.println("------------------------------------");
		System.out.println("Simulation finished");
	}
}
