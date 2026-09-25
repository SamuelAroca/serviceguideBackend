package proyecto.web.serviceguideBackend.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import proyecto.web.serviceguideBackend.exceptions.AppException;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

// Limitador de intentos en memoria, por clave (email, normalmente). Basta
// para un solo nodo como este; si el backend llega a correr en varias
// instancias detras de un balanceador, esto habria que moverlo a un store
// compartido (Redis) para que el limite aplique entre todas.
@Component
public class RateLimiter {

    private record Attempt(int count, Instant windowStart) {
    }

    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();

    public void checkAllowed(String key, int maxAttempts, Duration window) {
        Attempt attempt = attempts.get(key);
        if (attempt != null
                && Duration.between(attempt.windowStart(), Instant.now()).compareTo(window) < 0
                && attempt.count() >= maxAttempts) {
            throw new AppException("Too many attempts, please try again later", HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    public void recordFailure(String key, Duration window) {
        attempts.compute(key, (k, existing) -> {
            Instant now = Instant.now();
            if (existing == null || Duration.between(existing.windowStart(), now).compareTo(window) >= 0) {
                return new Attempt(1, now);
            }
            return new Attempt(existing.count() + 1, existing.windowStart());
        });
    }

    public void recordSuccess(String key) {
        attempts.remove(key);
    }
}
