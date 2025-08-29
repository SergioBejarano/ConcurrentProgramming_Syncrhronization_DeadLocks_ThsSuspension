package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author sergio.bejarano-r
 * @author laura.rsanchez
 */
public class HostBlackListSearchThread extends Thread {

    private final int startIndex;
    private final int endIndex;
    private final String ipaddress;
    private final HostBlacklistsDataSourceFacade skds;

    private final List<Integer> blackListOccurrences = new LinkedList<>();
    private int checkedLists = 0;

    public HostBlackListSearchThread(int startIndex, int endIndex, String ipaddress, HostBlacklistsDataSourceFacade skds) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.ipaddress = ipaddress;
        this.skds = skds;
    }

    @Override
    public void run() {
        for (int i = startIndex; i < endIndex; i++) {
            checkedLists++;
            if (skds.isInBlackListServer(i, ipaddress)) {
                blackListOccurrences.add(i);
            }
        }
    }

    public int getCheckedLists() {
        return checkedLists;
    }

    public List<Integer> getBlackListOccurrences() {
        return blackListOccurrences;
    }
}
