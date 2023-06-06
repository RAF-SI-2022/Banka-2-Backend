package rs.edu.raf.si.bank2.main.filters;

import io.jsonwebtoken.MalformedJwtException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rs.edu.raf.si.bank2.main.services.UserService;
import rs.edu.raf.si.bank2.main.utils.JwtUtil;

/**
 * Filter for requests that need authentication (via JWT).
 * <p>
 * This currently works by contacting the users microservice to check
 * whether the JWT token is valid. This makes little sense, because both
 * services use the same database. Should be refactored in the future to use
 * separate databases; all dependencies on the user model should be
 * reconfigured to use the model directly from the users service. The user
 * model for this microservice (if any), should contain nothing but an ID,
 * which should be the same ID for that user in the users service.
 * <p>
 * This requires a pub-sub communication model (event-driven microservices),
 * wherein when a user is added into the users microservice, the event is
 * propagated to all other microservices which can update their reference,
 * and so on.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Value("${services.users.host}")
    private String usersServiceHost;

    public JwtFilter(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (MalformedJwtException e) {
                username = null;
            }
        }

        if (username != null) {

            // send request to users service
            // TODO should this be http or https?
            String urlValidate = "http://" + usersServiceHost + "/api" + "/serviceAuth" + "/validate";

            // TODO this needs more requires troubleshooting + exception
            //  handling!!!
            HttpURLConnection con = null;
            boolean successfullyConnected = false;
            boolean authenticated = false;
            try {
                URL url = new URL(urlValidate);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", "Bearer " + jwt);
                con.setRequestProperty("Content-Type", "application/json");
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setConnectTimeout(3000);
                con.setReadTimeout(3000);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes("{}");
                out.flush();
                out.close();

                int code = con.getResponseCode();
                successfullyConnected = true;
                authenticated = code == 200;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }

            if (!successfullyConnected) {
                httpServletResponse.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
                return;
            }

            UserDetails userDetails = this.userService.loadUserByUsername(username);

            // if (jwtUtil.validateToken(jwt, userDetails)) {
            if (authenticated) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
