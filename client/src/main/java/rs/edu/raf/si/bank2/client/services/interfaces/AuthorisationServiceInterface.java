package rs.edu.raf.si.bank2.client.services.interfaces;

import rs.edu.raf.si.bank2.client.models.mariadb.PermissionName;

import java.util.Optional;

/**
 * Service for dealing with authentication-related jobs, where there is no
 * currently logged-in user.
 */
public interface AuthorisationServiceInterface {


    boolean isAuthorised(PermissionName permissionRequired, String userEmail);

}
