package it.denv.supsi.progconc.serie.serie9.es3;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Fork extends ReentrantLock {
	public static final char FORK = '|';
	public static final char NO_FORK = ' ';
	int id;

	public Fork(final int id) {
		this.id = id;
	}
}

class Philosopher extends Thread {
	public static final char PHIL_THINKING = '-';
	public static final char PHIL_LEFT_FORK = '=';
	public static final char PHIL_EATING = 'o';
	public static final char PHIL_HUNGRY = 'H';
	private final int id;

	public Philosopher(final int id) {
		this.id = id;
	}

	@Override
	public void run() {
		final Random random = new Random();
		final int tableOffset = 4 * id;
		final Fork leftLock = S9Philosophers.listOfLocks[id];
		final Fork rightLock = S9Philosophers.listOfLocks[(id + 1) % S9Philosophers.NUM_PHILOSOPHERS];
		final int table__farL = tableOffset;
		final int table__left = tableOffset + 1;
		final int table_philo = tableOffset + 2;
		final int table_right = tableOffset + 3;
		final int table__farR = (tableOffset + 4) % (4 * S9Philosophers.NUM_PHILOSOPHERS);

		while (!isInterrupted()) {
			try {
				Thread.sleep(S9Philosophers.UNIT_OF_TIME * (random.nextInt(6)));
			} catch (final InterruptedException e) {
				break;
			}

			// Try to get the fork on the left
            S9Philosophers.lock.lock();
			//S9Philosophers.dinerTable[table_philo] = PHIL_HUNGRY;
            S9Philosophers.dinerTable[table_philo] = PHIL_HUNGRY;

            if (leftLock.tryLock()) {
                synchronized (S9Philosophers.class) {
                    S9Philosophers.dinerTable[table__farL] = Fork.NO_FORK;
                    S9Philosophers.dinerTable[table__left] = Fork.FORK;
                    S9Philosophers.dinerTable[table_philo] = PHIL_LEFT_FORK;
                }


                try {
                    sleep(S9Philosophers.UNIT_OF_TIME);
                } catch (final InterruptedException e) {
                    break;
                }


                if(rightLock.tryLock()) {
                    synchronized (S9Philosophers.class) {
                        S9Philosophers.dinerTable[table_philo] = PHIL_EATING;
                        S9Philosophers.dinerTable[table_right] = Fork.FORK;
                        S9Philosophers.dinerTable[table__farR] = Fork.NO_FORK;
                    }

                    S9Philosophers.lock.unlock();
                    try {
                        sleep(S9Philosophers.UNIT_OF_TIME);
                    } catch (final InterruptedException e) {
                        break;
                    }
                } else {
					synchronized (leftLock) {
						try {
							leftLock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
                // Release fork
                synchronized (S9Philosophers.class) {
                    S9Philosophers.dinerTable[table__farL] = Fork.FORK;
                    S9Philosophers.dinerTable[table__left] = Fork.NO_FORK;
                    S9Philosophers.dinerTable[table_philo] = PHIL_THINKING;
                    S9Philosophers.dinerTable[table_right] = Fork.NO_FORK;
                    S9Philosophers.dinerTable[table__farR] = Fork.FORK;
                }
                rightLock.unlock();
                synchronized (leftLock) {
					leftLock.notify();
				}
            } else {
            	synchronized (leftLock) {
					try {
						leftLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

            S9Philosophers.lock.unlock();
		}
	}
}

public class S9Philosophers {
	public static final int NUM_PHILOSOPHERS = 5;
	public static final int UNIT_OF_TIME = 50;
	public static final Fork[] listOfLocks = new Fork[NUM_PHILOSOPHERS];
	public static char[] dinerTable = null;
	public static Lock lock = new ReentrantLock();
	public static ConcurrentLinkedQueue<Philosopher> hungryPhilosophers = new ConcurrentLinkedQueue<>();

	static {
		for (int i = 0; i < NUM_PHILOSOPHERS; i++)
			listOfLocks[i] = new Fork(i);
	}

	public static void main(final String[] a) {
		final char[] lockedDiner = new char[4 * NUM_PHILOSOPHERS];
		for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
			lockedDiner[4 * i] = Fork.NO_FORK;
			lockedDiner[4 * i + 1] = Fork.FORK;
			lockedDiner[4 * i + 2] = Philosopher.PHIL_LEFT_FORK;
			lockedDiner[4 * i + 3] = Fork.NO_FORK;
		}
		final String lockedString = new String(lockedDiner);

		// safe publication of the initial representation
		synchronized (S9Philosophers.class) {
			dinerTable = new char[4 * NUM_PHILOSOPHERS];
			for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
				dinerTable[4 * i] = Fork.FORK;
				dinerTable[4 * i + 1] = Fork.NO_FORK;
				dinerTable[4 * i + 2] = Philosopher.PHIL_THINKING;
				dinerTable[4 * i + 3] = Fork.NO_FORK;
			}
		}

		for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
			final Thread t = new Philosopher(i);
			// uses this solution to allow terminating the application even if
			// there is a deadlock
			t.setDaemon(true);
			t.start();
		}

		System.out.println("The diner table:");
		long step = 0;
		while (true) {
			step++;

			String curTableString = null;
			synchronized (S9Philosophers.class) {
				curTableString = new String(dinerTable);
			}
			System.out.println(curTableString + "   " + step);

			if (lockedString.equals(curTableString))
				break;
			try {
				Thread.sleep(UNIT_OF_TIME);
			} catch (final InterruptedException e) {
				System.out.println("Interrupted.");
			}
		}
		System.out.println("The diner is locked.");
	}
}
