package rs.edu.raf.si.bank2.users.requests;

import lombok.Data;
import rs.edu.raf.si.bank2.users.models.mariadb.PermissionName;

@Data
public class CheckPermissionRequest {
    PermissionName permissionName;
    String userEmail;
}
