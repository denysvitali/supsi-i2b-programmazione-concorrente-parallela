package it.denv.supsi.progconc.serie.serie2.es3;

public class User {
    private String name;
    private String surname;

    private int amount;
    private int amount_dec;

    public User(String name, String surname){
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public void addAmount(int amount, int amount_dec){
        if(amount_dec + this.amount_dec >= 100){
            this.amount += (amount_dec + this.amount_dec)/100;
            this.amount_dec = (amount_dec + this.amount_dec)%100;
        }
        this.amount += amount;
    }

    public int getAmount() {
        return amount;
    }

    public int getAmount_dec() {
        return amount_dec;
    }

    public String getBalance(){
        return String.format("%d.%02d", amount, amount_dec);
    }

    @Override
    public String toString() {
        return String.format("%s %s", name, surname);
    }
}
