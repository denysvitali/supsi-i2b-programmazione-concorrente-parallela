package it.denv.supsi.progconc.serie.serie10.es3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class S10Esercizio3 {
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
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executorService);
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
            completionService.submit((new Multiplication(m0, m1)));
        }
        executorService.shutdown();

        int biggest = 0;

        for(int i = 0; i<NUM_OPERATIONS; i++){
            try {
                Future<Integer> future_result = completionService.take();
                int result = future_result.get();

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

    int[][] m0;
    int[][] m1;
    int[][] m2 = new int[S10Esercizio3.MATRIX_SIZE][S10Esercizio3.MATRIX_SIZE];
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