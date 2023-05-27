package rs.edu.raf.si.bank2.main.requests;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import rs.edu.raf.si.bank2.main.models.mariadb.PermissionName;

@Data
public class CheckPermissionRequest {
    PermissionName permissionName;
    String userEmail;

    public CheckPermissionRequest(PermissionName permissionName, String userEmail) {
        this.permissionName = permissionName;
        this.userEmail = userEmail;
    }
}
