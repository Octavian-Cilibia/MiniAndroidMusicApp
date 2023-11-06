package com.example.proiectpdm;

public class Nota {
    private String name;
    private int delay;

    public Nota(String name) {
        this.name = name;
        this.delay = 200;
    }

    public Nota(String name, int delay){
        this.name = name;
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
