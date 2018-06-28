package me.gerryfletcher.restapi.permissions;

import me.gerryfletcher.restapi.authentication.Role;

import java.util.*;
import java.util.function.Function;

/**
 * This class acts as the DB would.
 */
public class PermissionService {

    private static Map<String, List<Permission>> userPermissionsMap = new HashMap<>();
    private static Map<PermissionAction, Function<UserInfo, UserInfo>> permissionActionMap = new HashMap<>();

    public PermissionService() {
        constructActionMap();
    }

    public List<Permission> getUserPermissions(String username) {
        return userPermissionsMap.getOrDefault(username, new ArrayList<>());
    }

    public void addUserPermission(String username, PermissionAction action) {
        Permission permission = new Permission(action, new Date());
        List<Permission> permissionList = userPermissionsMap.getOrDefault(username, new ArrayList<>());
        permissionList.add(permission);

        userPermissionsMap.put(username, permissionList);
    }

    public UserInfo updateUserPermissions(UserInfo userInfo) {
        List<Permission> permissions = userPermissionsMap.getOrDefault(userInfo.getUsername(), new ArrayList<>());
        if (permissions.isEmpty()) return userInfo;

        Date tokenIssueDate = userInfo.getTokenIssueDate();

        for (Permission permission : permissions) {
            // If the permission was set on the account after the token was created, apply it to the user.
            if (tokenIssueDate.before(permission.getIssuedAt())) {
                PermissionAction action = permission.getAction();
                userInfo = permissionActionMap.get(action).apply(userInfo);
            }
        }

        return userInfo;
    }

    private void constructActionMap() {
        permissionActionMap.put(PermissionAction.PROMOTE, u -> u.setRole(Role.ADMIN));
        permissionActionMap.put(PermissionAction.DEMOTE, u -> u.setRole(Role.USER));
        permissionActionMap.put(PermissionAction.LOGOUT, u -> u.setLoggedIn(false));
        permissionActionMap.put(PermissionAction.REVOKE, u -> u.setRevoked(true));
        permissionActionMap.put(PermissionAction.CLEAR, u -> u.clear());
    }

}
