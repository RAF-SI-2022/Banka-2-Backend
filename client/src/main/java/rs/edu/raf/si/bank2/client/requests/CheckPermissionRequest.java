package rs.edu.raf.si.bank2.client.requests;

import lombok.Data;
import rs.edu.raf.si.bank2.client.models.mariadb.PermissionName;

@Data
public class CheckPermissionRequest {
    PermissionName permissionName;
    String userEmail;
}
