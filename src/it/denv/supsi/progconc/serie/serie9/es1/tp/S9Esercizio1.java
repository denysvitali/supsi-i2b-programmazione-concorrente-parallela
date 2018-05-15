package it.denv.supsi.progconc.serie.serie9.es1.tp;

import java.util.concurrent.CountDownLatch;

public class S9Esercizio1 extends Thread {
	static final CountDownLatch cdl = new CountDownLatch(2);
	static volatile boolean finished = false;
	static volatile int sum = 0;
	static volatile int cnt = 0;

	public static void main(final String[] args) {
		final Thread thread1 = new Thread(() -> {
			Thread.currentThread().setPriority(MAX_PRIORITY);
			cdl.countDown();
			try {
				cdl.await();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			int count = 0;

			while (!S9Esercizio1.finished) {
				S9Esercizio1.cnt = ++count;
				System.out.println("sum " + S9Esercizio1.sum);
			}
		});

		final Thread thread2 = new Thread(() -> {
			cdl.countDown();
			try {
				cdl.await();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			for (int i = 1; i <= 50000; i++) {
				S9Esercizio1.sum = i;
			}
			Thread.currentThread().setPriority(MAX_PRIORITY);
			S9Esercizio1.finished = true;
			System.out.println("cnt " + S9Esercizio1.cnt);
		});

		thread1.start();
		thread2.start();

		try {
			thread1.join();
			thread2.join();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}
