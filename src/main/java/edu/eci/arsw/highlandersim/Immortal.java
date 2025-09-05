package edu.eci.arsw.highlandersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;

    private int health;

    private int defaultDamageValue;

    private static List<Immortal> immortalsPopulation = Collections.synchronizedList(new ArrayList<>());

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private volatile boolean paused = false;

    private volatile boolean stopped = false;

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb ) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {
        while (!stopped) {

            if (immortalsPopulation.size() <= 1) {
                break;
            }

            int myIndex = immortalsPopulation.indexOf(this);
            if (myIndex == -1) {
                break;
            }

            int nextFighterIndex;
            do {
                nextFighterIndex = r.nextInt(immortalsPopulation.size());
            } while (nextFighterIndex == myIndex || immortalsPopulation.get(nextFighterIndex).getHealth() <= 0);

            Immortal im = immortalsPopulation.get(nextFighterIndex);

            if (im != null) {
                this.fight(im);
            }

            checkPaused();

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void fight(Immortal i2) {
        Immortal first, second;

        if (this.name.compareTo(i2.name) < 0) {
            first = this;
            second = i2;
        } else {
            first = i2;
            second = this;
        }

        synchronized (first) {
            synchronized (second) {
                if (i2.getHealth() > 0 && this.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.health += defaultDamageValue;

                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");

                    if (i2.getHealth() <= 0) {
                        updateCallback.processReport(this + " says: " + i2 + " is dead now!\n");
                    }
                } else {
                    updateCallback.processReport(this + " says: " + i2 + " is already dead!\n");
                }
            }
        }
    }


    public void pause() {
        synchronized (this) {
            paused = true;
        }
    }

    public void resumeImmortal() {
        synchronized (this) {
            paused = false;
            notify();
        }
    }

    private void checkPaused() {
        synchronized (this) {
            while (paused) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void changeHealth(int v) {
        synchronized (this) {
            health = v;
        }
    }

    public int getHealth() {
        synchronized (this) {
            return health;
        }
    }

    public void stopImmortal() {
        synchronized (this) {
            stopped = true;
        }
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }


}