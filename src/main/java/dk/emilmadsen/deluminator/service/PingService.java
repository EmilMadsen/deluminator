package dk.emilmadsen.deluminator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

@Service
@Slf4j
public class PingService {
    private static final int PING_TIMEOUT = 8000;
    private static final int PING_DELAY = 10000;

    @Value("${iplist}")
    private String[] ipList;

    @Scheduled(fixedDelay = PING_DELAY)
    public void checkIps() {
        log.info("pinging ips");
        if (null != ipList) {
            for (String ip : ipList) {
                log.info("{} reachable? {}", ip, ping(ip));
            }
        }
    }

    public boolean ping(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.isReachable(PING_TIMEOUT);
        } catch (IOException e) {
            log.error("IOException whilst trying to establish connection to host: {}", ip, e);
        }
        return false;
    }
}
