package dk.emilmadsen.deluminator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

@Service
@Slf4j
public class PingService {
    private static final int PING_TIMEOUT = 8000;

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
