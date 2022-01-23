package dk.emilmadsen.deluminator.scheduled;

import dk.emilmadsen.deluminator.model.PingHistory;
import dk.emilmadsen.deluminator.model.ReachabilityStatus;
import dk.emilmadsen.deluminator.service.PingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CheckIpsJob {
    private static final int PING_DELAY = 1000 * 15;
    private static final int CHECK_DELAY = 1000 * 60 * 3;

    private List<PingHistory> pingTargets = new ArrayList<>();

    private final PingService pingService;

    public CheckIpsJob(PingService pingService, @Value("${iplist:[]}") String[] ipList) {
        this.pingService = pingService;
        for (String ip : ipList) {
            pingTargets.add(new PingHistory(ip));
        }
    }

    @Scheduled(fixedDelay = PING_DELAY)
    public void pingTargets() {
        log.info("pinging ips");
        for (PingHistory target : pingTargets) {
            boolean reachable = pingService.ping(target.getIp());
            target.addHistory(reachable);
            log.info("{} reachable? {}", target.getIp(), reachable);
        }
    }

    @Scheduled(fixedDelay = CHECK_DELAY)
    public void checkTargets() {
        pingTargets.forEach(target -> {
            ReachabilityStatus status = target.isHistoricallyReachable();
            switch (status) {
                case INCONCLUSIVE -> log.warn("not enough history logged to determine status on ip: {}", target.getIp());
                case REACHABLE -> log.info("target: {} is reachable - all is good.", target.getIp());
                case UNREACHABLE -> log.warn("target: {} is not reachable!", target.getIp()); // TODO: sound the alarm
            }

        });
    }

}
