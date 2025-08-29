package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT = 5;
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());

    public List<Integer> checkHost(String ipaddress, int N) {
        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
        int totalServers = skds.getRegisteredServersCount();

        SharedSearchStatus status = new SharedSearchStatus(BLACK_LIST_ALARM_COUNT);

        int segmentSize = totalServers / N;
        int remainder = totalServers % N;

        HostBlackListSearchThread[] threads = new HostBlackListSearchThread[N];

        int start = 0;
        for (int i = 0; i < N; i++) {
            int end = start + segmentSize + (i < remainder ? 1 : 0);
            threads[i] = new HostBlackListSearchThread(start, end, ipaddress, skds, status);
            threads[i].start();
            start = end;
        }

        int totalChecked = 0;
        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
                totalChecked += threads[i].getCheckedLists();
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, "Thread interrupted", e);
            }
        }

        if (status.getOccurrencesCount() >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipaddress);
        } else {
            skds.reportAsTrustworthy(ipaddress);
        }

        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{totalChecked, totalServers});

        return status.getBlackListOccurrences();
    }
}
