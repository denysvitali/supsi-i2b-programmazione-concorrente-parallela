package it.denv.supsi.progconc.serie.test1_prova.es1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Classe che simula periodi di carestia per i paesi
 */
class Carestia implements Runnable {
    @Override
    public void run() {
        final Random random = new Random();
        // ad ogni giro introduce carestia per un determinato paese scelto a caso
        for (int i = 0; i < 50; i++) {
            final int popoloScelto = random.nextInt(EsercizioPopolazioni.popolazione.length);

            // 0.1 .. 0.7
            final double fattoreDecimazione = random.nextDouble() * 0.6 + 0.1;
            final AtomicLong popolazioneAggiornata;

            // decima la popolazione
            EsercizioPopolazioni.popolazione[popoloScelto].getAndUpdate(operand -> (long) (operand*fattoreDecimazione));
            popolazioneAggiornata = EsercizioPopolazioni.popolazione[popoloScelto];

            System.out.println("Carestia: Popolazione " + popoloScelto + " diminuita a " + popolazioneAggiornata
                    + " del fattore " + fattoreDecimazione);

            try {
                // pausa fra un periodo di carestia e l'altro
                Thread.sleep(100);
            } catch (final InterruptedException e) {
            }
        }
    }
}

/**
 * Classe che simula periodi di prosperita per i paesi
 */
class Prosperita implements Runnable {
    @Override
    public void run() {
        final Random random = new Random();
        // ad ogni giro introduce prosperità per un determinato paese scelto a caso
        for (int i = 0; i < 100; i++) {
            final int popoloScelto = random.nextInt(EsercizioPopolazioni.popolazione.length);

            final double fattoreCrescita = random.nextDouble() * 0.55 + 1.05;
            final AtomicLong popolazioneAggiornata;

            // incrementa la popolazione
            EsercizioPopolazioni.popolazione[popoloScelto].getAndUpdate(operand -> (long) (operand*fattoreCrescita));
            popolazioneAggiornata = EsercizioPopolazioni.popolazione[popoloScelto];

            System.out.println("Prosperita: Popolazione " + popoloScelto + " cresciuta a " + popolazioneAggiornata.get()
                    + " del fattore " + fattoreCrescita);

            try {
                // pausa fra un periodo di prosperità e l'altro
                Thread.sleep(50);
            } catch (final InterruptedException e) {
            }
        }
    }
}

/**
 * Programma che simula la variazione demografica di 5 paesi
 */
public class EsercizioPopolazioni {
    static volatile AtomicLong popolazione[] = new AtomicLong[5];

    public static void main(final String[] args) {
        // la popolazione iniziale è di 1000 abitanti per ogni paese
        for (int i = 0; i < 5; i++)
            popolazione[i] = new AtomicLong(1000);

        final List<Thread> allThreads = new ArrayList<>();
        allThreads.add(new Thread(new Prosperita()));
        allThreads.add(new Thread(new Prosperita()));
        allThreads.add(new Thread(new Carestia()));

        System.out.println("Simulation started");
        for (final Thread t : allThreads)
            t.start();

        for (final Thread t : allThreads)
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        System.out.println("Simulation finished");
    }
}