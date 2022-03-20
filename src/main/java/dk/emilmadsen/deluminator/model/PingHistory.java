package dk.emilmadsen.deluminator.model;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class PingHistory {
    private static final int MAX_HISTORY_LENGTH = 15;

    private String ip;
    private List<Boolean> history = new LinkedList<>();

    public PingHistory(String ip) {
        this.ip = ip;
    }

    public void addHistory(boolean isReachable) {
        history.add(0, isReachable);
        ensureMaxLength();
    }

    public ReachabilityStatus getReachability() {
        if (history.size() == MAX_HISTORY_LENGTH) {
            if (history.stream().allMatch(item -> item == Boolean.FALSE)) {
                return ReachabilityStatus.UNREACHABLE;
            } else {
                return ReachabilityStatus.REACHABLE;
            }
        }
        return ReachabilityStatus.INCONCLUSIVE;
    }

    private void ensureMaxLength() {
        if (history.size() > MAX_HISTORY_LENGTH) {
            history = history.subList(0, MAX_HISTORY_LENGTH);
        }
    }

}
