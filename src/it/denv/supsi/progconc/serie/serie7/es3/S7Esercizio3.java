package it.denv.supsi.progconc.serie.serie7.es3;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

class User {
    String name;
    Inbox inbox;
    int letters_available = 150;
    int sent = 0;

    public User(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setInbox(Inbox inbox) {
        this.inbox = inbox;
    }

    public Inbox getInbox() {
        return inbox;
    }

    public int getSent() {
        return sent;
    }

    public void sendMessage(User receiver, Message message) {
        if(message == null){
            return;
        }

        if(sent == letters_available){
            return;
        }

        sent++;

        message.setSender(this);
        receiver.getInbox().addMessage(message);
    }

    public int getAvailable() {
        return letters_available - sent;
    }
}

class Message {
    String content;
    User sender;

    public Message(String s){
        this.content = s;
    }

    public Message(User u, String s) {
        this.sender = u;
        this.content = s;
    }

    public void setSender(User user) {
        this.sender = user;
    }

    public User getSender() {
        return this.sender;
    }
}

class Inbox {
    CopyOnWriteArrayList<Message> messages = new CopyOnWriteArrayList<>();

    public CopyOnWriteArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
}

public class S7Esercizio3 {
    public static void main(String[] args){
        ArrayList<Thread> tl = new ArrayList<>();

        ArrayList<User> users = new ArrayList<>();
        users.add(new User("Mario"));
        users.add(new User("Luigi"));

        for(User u : users){
            u.setInbox(new Inbox());
        }

        for(User u : users){
            User ru;
            do {
                ru = users.get((int) (Math.random() * users.size()));
            }
            while(ru == u);
            for(int i=0; i<(Math.random()*3 + 2); i++){
                System.out.println(u.getName() + " writes to " + ru.getName());
                u.sendMessage(ru, new Message(String.valueOf(u.getSent()+1)));
            }
            System.out.println();
        }

        for(User u : users){
            tl.add(new Thread(() -> {
                while(u.getAvailable() != 0){
                    for(Message m : u.getInbox().getMessages()){
                        u.getInbox().getMessages().remove(m);
                        try {
                            Thread.sleep((long) (Math.random() * 45 + 5));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Replying...");
                        u.sendMessage(m.getSender(), new Message("Reply " + u.getSent() + 1));
                        break;
                    }
                }
            }));
        }

        for(Thread t : tl){
            t.start();
        }

        for(Thread t : tl){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("End");
    }
}
