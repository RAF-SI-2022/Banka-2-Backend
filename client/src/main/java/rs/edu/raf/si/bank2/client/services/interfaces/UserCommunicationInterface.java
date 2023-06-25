package rs.edu.raf.si.bank2.client.services.interfaces;

import java.io.IOException;
import rs.edu.raf.si.bank2.client.dto.CommunicationDto;
import rs.edu.raf.si.bank2.client.models.mariadb.PermissionName;

public interface UserCommunicationInterface {

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
    public CommunicationDto sendGet(String senderEmail, String urlExtension);

    public CommunicationDto sendPostLike(String urlExtension, String postObjectBody, String senderEmail, String method);

    public CommunicationDto sendDelete(String urlExtension);
}
