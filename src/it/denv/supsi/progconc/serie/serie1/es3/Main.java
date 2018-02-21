package it.denv.supsi.progconc.serie.serie1.es3;

import java.util.ArrayList;

public class Main {
    private static final int ARRAY_SIZE = 10000;
    public static void main(String[] args){
        int numbers[] = new int[ARRAY_SIZE];

        for(int i=0; i<ARRAY_SIZE; i++){
            numbers[i] = (int) (Math.random() * 99) + 1;
        }

        ArrayList<Thread> threads = new ArrayList<>();

        for(int i=0; i<10; i++){
            int iter = i;
            Thread t = new Thread(()->{
                int sum = 0;
                for(int j=iter*1000; j<(iter+1)*1000; j++){
                    sum += numbers[iter];
                }
                System.out.println(String.format(
                        "Somma degli elementi nell'intervallo\t[%d;%d]%s=\t%d",
                        iter*1000,
                        (iter+1)*1000-1,
                        (iter==0?"\t\t":"\t"),
                        sum
                ));
            });
            threads.add(t);
        }

        for(Thread t: threads){
            t.start();
        }
    }
}
