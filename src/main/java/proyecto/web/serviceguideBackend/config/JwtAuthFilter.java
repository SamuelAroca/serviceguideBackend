package proyecto.web.serviceguideBackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import proyecto.web.serviceguideBackend.token.TokenRepository;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserAuthenticationProvider userAuthenticationProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().contains("/api/users/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null) {
            String[] authElements = header.split(" ");
            if (authElements.length == 2 && "Bearer".equals(authElements[0])) {
                var isTokenValid = userAuthenticationProvider.isTokenValid(authElements[1]);
                if (isTokenValid) {
                    try {
                        SecurityContextHolder.getContext().setAuthentication(
                                userAuthenticationProvider.validateToken(authElements[1]));
                    } catch (RuntimeException e) {
                        SecurityContextHolder.clearContext();
                        throw e;
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
