package rs.edu.raf.si.bank2.main.services.interfaces;

import java.io.IOException;
import rs.edu.raf.si.bank2.main.models.mariadb.PermissionName;

public interface CommunicationInterface {

    /**
     * Used to check whether the user has the adequate permissions.
     * <p>
     * TODO Impl: this method should be reworked to use the Users service,
     *   not the current service!
     *
     * @param permissionName permission to check for
     * @param userEmail      user email
     * @return true if user has the requested permission, false otherwise
     */
    boolean isAuthorised(PermissionName permissionName, String userEmail);

    /**
     * TODO no idea what this does, but should be removed.
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @deprecated
     */
    public String testComs() throws IOException, InterruptedException;
}
