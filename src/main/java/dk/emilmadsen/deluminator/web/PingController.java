package dk.emilmadsen.deluminator.web;

import dk.emilmadsen.deluminator.service.PingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ping")
public class PingController {

    private final PingService pingService;

    public PingController(PingService pingService) {
        this.pingService = pingService;
    }

    @GetMapping("/{ip}")
    public ResponseEntity<String> pingIp(@PathVariable String ip) {
        if (pingService.ping(ip)) {
            return ResponseEntity.ok(ip + " is reachable.");
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
