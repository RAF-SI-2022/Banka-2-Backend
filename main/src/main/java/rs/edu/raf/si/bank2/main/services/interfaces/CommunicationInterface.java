package rs.edu.raf.si.bank2.main.services.interfaces;

import rs.edu.raf.si.bank2.main.models.mariadb.PermissionName;

import java.io.IOException;

public interface CommunicationInterface {

    String isAuthorised(PermissionName permissionName, String userEmail) ;
    public String testComs() throws IOException, InterruptedException;
}

