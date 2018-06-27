package me.gerryfletcher.restapi.permissions;

import java.util.HashMap;
import java.util.Map;

public class PermissionService {

    private static Map<String, UserPermissions> userPermissionsMap = new HashMap<>();

    public PermissionService() {
    }

    public void putUserPermission(UserPermissions permissions) {
        userPermissionsMap.put(permissions.getUsername(), permissions);
    }

    public UserPermissions getUserPermissions(String username) {
        return userPermissionsMap.get(username);
    }

}
