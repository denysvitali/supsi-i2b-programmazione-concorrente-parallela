package it.denv.supsi.progconc.serie.serie5.es3;

public class Esercizio3 {
    public static void main(String[] args){
        System.out.println("This program will never stop. Press CTRL+C to stop it.");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Company c = new Company();
        
        while(true){
            c.getNewExchangeRates();    
        }
    }
}
