package it.denv.supsi.progconc.serie.serie9.es2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Depot {
	final private int id;
	private final ConcurrentLinkedQueue<String> elements = new ConcurrentLinkedQueue<>();
	private Lock lock = new ReentrantLock();

	public Depot(final int id) {
		this.id = id;
		for (int i = 0; i < 1000; i++)
			elements.add("Dep#" + id + "_item#" + i);
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public int getStockSize() {
		return elements.size();
	}

	public Lock getLock() {
		return lock;
	}

	public String getElement() {
		return elements.poll();
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Depot" + id;
	}
}

class AssemblingWorker implements Runnable {
	private final int id;

	public AssemblingWorker(final int id) {
		this.id = id;
	}

	@Override
	public void run() {
		final Random random = new Random();
		int failureCounter = 0;
		while (true) {
			// Choose randomly 3 different suppliers
			final List<Depot> depots = new ArrayList<>();
			while (depots.size() != 3) {
				final Depot randomDepot = S9Factory.suppliers[random.nextInt(S9Factory.suppliers.length)];
				if (!depots.contains(randomDepot)) {
					depots.add(randomDepot);
				}
			}

			depots.sort((a,b) -> (a.getId() <= b.getId()? -1 : 1));
			final Depot supplier1 = depots.get(0);
			final Depot supplier2 = depots.get(1);
			final Depot supplier3 = depots.get(2);
			boolean proceed = true;

			log("assembling from : " + supplier1 + ", " + supplier2 + ", " + supplier3);
			supplier1.getLock().lock();
			if(supplier1.isEmpty()) {
				log("not all suppliers have stock available!");
				failureCounter++;
				proceed = false;
			}
			supplier1.getLock().unlock();

			supplier2.getLock().lock();
			if(supplier2.isEmpty()) {
				log("not all suppliers have stock available!");
				failureCounter++;
				proceed = false;
			}
			supplier2.getLock().unlock();

			supplier3.getLock().lock();
			if(supplier3.isEmpty()) {
				log("not all suppliers have stock available!");
				failureCounter++;
				proceed = false;
			}
			supplier3.getLock().unlock();

			if(proceed){
				supplier1.getLock().lock();
				supplier2.getLock().lock();
				supplier3.getLock().lock();
				final String element1 = supplier1.getElement();
				final String element2 = supplier2.getElement();
				final String element3 = supplier3.getElement();
				log("assembled product from parts:  " + element1 + ", " + element2 + ", "
						+ element3);
				supplier1.getLock().unlock();
				supplier2.getLock().unlock();
				supplier3.getLock().unlock();
			}

			if (failureCounter > 1000) {
				log("Finishing after " + failureCounter + " failures");
				break;
			}
		}
	}

	private final void log(final String msg) {
		System.out.println("AssemblingWorker" + id + ": " + msg);
	}
}

public class S9Factory {
	final static Depot[] suppliers = new Depot[10];

	public static void main(final String[] args) {
		for (int i = 0; i < 10; i++)
			suppliers[i] = new Depot(i);

		final List<Thread> allThreads = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			allThreads.add(new Thread(new AssemblingWorker(i)));
		}

		System.out.println("Simulation started");
		for (final Thread t : allThreads) {
			t.start();
		}

		for (final Thread t : allThreads) {
			try {
				t.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Simulation finished");
	}
}
