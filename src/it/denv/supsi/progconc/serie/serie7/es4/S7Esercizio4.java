package it.denv.supsi.progconc.serie.serie7.es4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

enum WeekDay {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday
}

public class S7Esercizio4 {
    public static volatile ConcurrentHashMap<WeekDay, String> hm = new ConcurrentHashMap<>();
    public static void main(String[] args){
        for(WeekDay wd : WeekDay.values()){
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i<10E3; i++){
                char a = (char) (Math.random()*256);
                sb.append(a);
            }
            hm.put(wd, sb.toString());
        }

        for(String s : hm.values()){
            System.out.println(s);
        }

        ArrayList<Thread> threads = new ArrayList<>();
        for(int i=0; i<30; i++){
            threads.add(new Thread(() -> {
                WeekDay wd = WeekDay.values()[(int) (Math.random() * (WeekDay.values().length - 1))];
                String wds;
                int j = 0;
                do{
                    wds = hm.get(wd);
                    j++;
                } while(!hm.replace(wd, wds, wds.substring(0, wds.length()-1)));
                System.out.println("Updated " + wd + " after " + j + " tries");
            }));
        }

        for(Thread t : threads){
            t.start();
        }

        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
