package it.denv.supsi.progconc.serie.serie1.es2;

public class MyRunnable implements Runnable {
    long sleeptime;
    int index;
    MyRunnable(int i, long sleeptime){
        this.index = i;
        this.sleeptime = sleeptime;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(this.sleeptime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("Thread %d risveglio dopo %d ms", index, sleeptime));
    }
}
