package it.denv.supsi.progconc.serie.serie5.es3;

import java.util.concurrent.ThreadLocalRandom;

public class Branch extends Thread {
    private Company c;
    
    public Branch(Company c){
        this.c = c;
    }
    
    @Override
    public void run() {
        while(true){
            double change_b = ThreadLocalRandom.current().nextDouble(50, 500);
            ExchangeRates er = c.currentER;
            System.out.println("Using er: " + er + " = " + er.getEr());
            double exr = c.currentER.getEr()[ThreadLocalRandom.current().nextInt(0,ExchangeRates.size)];
            System.out.println(
                    String.format(
                            "Cambio %f, ER: %f, ottengo %f", 
                            change_b,
                            exr,
                            change_b*exr)
            );
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1,4));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
    }
}
