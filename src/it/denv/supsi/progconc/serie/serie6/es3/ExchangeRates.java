package it.denv.supsi.progconc.serie.serie6.es3;

public final class ExchangeRates {
    public static final int size = 10;
    private final double[] er;
    
    public ExchangeRates(double[] exchange_rates){
        er = exchange_rates;
    }

    public double[] getEr() {
        return er;
    }
}
