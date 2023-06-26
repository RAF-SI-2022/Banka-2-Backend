package rs.edu.raf.si.bank2.users.controllers;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for validating tokens from other services.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/serviceAuth")
@Timed
public class ServiceAuthController {

    @Autowired
    public ServiceAuthController() {}

    /**
     * API endpoint for validating tokens by other services. Services
     * should send a POST request to this endpoint with the bearer token from
     * the original user request in order to validate the token.
     * <p>
     * See
     * {@link rs.edu.raf.si.bank2.users.configuration.SpringSecurityConfig}
     * class where this route is configured to fail of not authenticated (if
     * JWT token is bad.)
     *
     * @return 200 if valid, error otherwise.
     */
    @Timed("controllers.serviceAuth.validate")
    @PostMapping("/validate")
    public ResponseEntity<?> validate() {
        return ResponseEntity.ok().body("Token valid");
    }
}
