package it.denv.supsi.progconc.serie.test1_prova.es3;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

class Magazzino{

}

class Cliente implements Runnable {
    private int soldi = 20;

    public int getSoldi() {
        return soldi;
    }

    public void run() {
        int attempts = 0;
        while(soldi != 0 && attempts < 10){
            attempts++;
            int productIndex = ThreadLocalRandom.current().nextInt(9);
            Prodotto p = EsercizioMagazzino.productList[productIndex].get();
            Prodotto newProduct = new Prodotto(p.nr, p.qty - 1, p.price);
            do {
                if (p.qty > 0 && p.price <= soldi) {
                    attempts = 0;
                    soldi -= p.price;
                    System.out.println("Bought " + p.nr);
                } else {
                    break;
                }

            } while(EsercizioMagazzino.productList[productIndex].compareAndSet(p, newProduct));
            try {
                Thread.sleep((long) (ThreadLocalRandom.current().nextInt(4)+ 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Client is leaving the mall");
    }
}

class Prodotto {
    final int nr;
    final int qty;
    final int price;
    ReentrantLock lock = new ReentrantLock();

    Prodotto(int nr){
        this.nr = nr;
        this.qty = (int) (Math.random() * 9) + 1;
        this.price = (int) (Math.random() * 4) + 1;
    }

    Prodotto(int nr, int qty, int price){
        this.nr = nr;
        this.qty = qty;
        this.price = price;
    }

    void print(){
        System.out.println("Prodotto " + nr + " (price=" + price+"): QTY=" + qty);
    }
}

public class EsercizioMagazzino {
    public volatile static AtomicReference<Prodotto>[] productList;

    public static void main(String[] args){
        productList = new AtomicReference[10];
        for(int i=0; i<10; i++){
            productList[i] = new AtomicReference(new Prodotto(i));
            productList[i].get().print();
        }

        ArrayList<Thread> tlist = new ArrayList<>();
        ArrayList<Cliente> clients = new ArrayList<>();

        for(int i=0; i<10; i++){
            clients.add(new Cliente());
        }

        for(Cliente c : clients){
            tlist.add(new Thread(c));
        }

        for(Thread t : tlist){
            t.start();
        }

        for(Thread t : tlist){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int i = 0;
        for(Cliente c : clients) {
            System.out.println("Cliente " + i + " : " + c.getSoldi());
            i++;
        }

        for(AtomicReference<Prodotto> p : productList){
            p.get().print();
        }
    }
}
