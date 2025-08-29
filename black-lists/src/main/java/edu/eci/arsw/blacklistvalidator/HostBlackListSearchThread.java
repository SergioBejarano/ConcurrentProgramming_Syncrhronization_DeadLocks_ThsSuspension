package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;


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
    private final SharedSearchStatus status;

    private int checkedLists = 0;

    public HostBlackListSearchThread(int startIndex, int endIndex, String ipaddress,
                                     HostBlacklistsDataSourceFacade skds, SharedSearchStatus status) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.ipaddress = ipaddress;
        this.skds = skds;
        this.status = status;
    }

    @Override
    public void run() {
        for (int i = startIndex; i < endIndex && !status.shouldStop(); i++) {
            checkedLists++;
            if (skds.isInBlackListServer(i, ipaddress)) {
                boolean reachedLimit = status.addOccurrence(i);
                if (reachedLimit) break;
            }
        }
    }

    public int getCheckedLists() {
        return checkedLists;
    }
}
