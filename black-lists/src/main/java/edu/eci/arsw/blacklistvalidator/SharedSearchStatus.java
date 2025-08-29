package edu.eci.arsw.blacklistvalidator;

import java.util.LinkedList;
import java.util.List;

public class SharedSearchStatus {
    private int occurrencesCount = 0;
    private final List<Integer> blackListOccurrences = new LinkedList<>();
    private boolean stop = false;

    private final int alarmCount;

    public SharedSearchStatus(int alarmCount) {
        this.alarmCount = alarmCount;
    }

    public synchronized boolean addOccurrence(int index) {
        if (!stop) {
            blackListOccurrences.add(index);
            occurrencesCount++;
            if (occurrencesCount >= alarmCount) {
                stop = true; 
            }
        }
        return stop;
    }

    public synchronized boolean shouldStop() {
        return stop;
    }

    public synchronized List<Integer> getBlackListOccurrences() {
        return new LinkedList<>(blackListOccurrences);
    }

    public synchronized int getOccurrencesCount() {
        return occurrencesCount;
    }
}

