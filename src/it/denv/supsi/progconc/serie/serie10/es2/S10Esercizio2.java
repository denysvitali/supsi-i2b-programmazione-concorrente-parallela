package it.denv.supsi.progconc.serie.serie10.es2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class S10Esercizio2 {
    public static final int NUM_OPERATIONS = 100_000;
    public static final int MATRIX_SIZE = 64;

    public static void main(final String[] args) {
        final Random rand = new Random();
        System.out.println("Simulazione iniziata");
        ExecutorService executorService =
                Executors.newFixedThreadPool(
                        Runtime.getRuntime()
                                .availableProcessors()
                );
        List<Future<Integer>> futures = new ArrayList<>();
        for (int operation = 0; operation < NUM_OPERATIONS; operation++) {
            // Crea matrici
            final int[][] m0 = new int[MATRIX_SIZE][MATRIX_SIZE];
            final int[][] m1 = new int[MATRIX_SIZE][MATRIX_SIZE];

            // Inizializza gli array con numeri random
            for (int i = 0; i < MATRIX_SIZE; i++) {
                for (int j = 0; j < MATRIX_SIZE; j++) {
                    m0[i][j] = rand.nextInt(10);
                    m1[i][j] = rand.nextInt(10);
                }
            }
            futures.add(executorService.submit((new Multiplication(m0, m1))));
        }
        executorService.shutdown();

        int biggest = 0;

        for(Future<Integer> future : futures){
            try {
                int result = future.get();
                if(result > biggest){
                    biggest = result;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Biggest: " + biggest);


        System.out.println("Simulazione terminata");
    }
}

class Multiplication implements Callable<Integer> {
    double result = 0;

    int[][] m0 = new int[S10Esercizio2.MATRIX_SIZE][S10Esercizio2.MATRIX_SIZE];
    int[][] m1 = new int[S10Esercizio2.MATRIX_SIZE][S10Esercizio2.MATRIX_SIZE];
    int[][] m2 = new int[S10Esercizio2.MATRIX_SIZE][S10Esercizio2.MATRIX_SIZE];
    int sum = 0;

    Multiplication(int[][] m0, int[][] m1){
        this.m0 = m0;
        this.m1 = m1;
    }

    @Override
    public Integer call() {
        for (int i = 0; i < m0[0].length; i++) {
            for (int j = 0; j < m1.length; j++) {
                for (int k = 0; k < m0.length; k++) {
                    m2[i][j] += m0[i][k] * m1[k][j];
                    sum += m2[i][j];
                }
            }
        }
        return sum;
    }
}