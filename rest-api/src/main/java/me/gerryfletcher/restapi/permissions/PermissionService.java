package me.gerryfletcher.restapi.permissions;

import me.gerryfletcher.restapi.authentication.Role;

import java.util.*;
import java.util.function.Function;

/**
 * This class acts as the DB would.
 */
public class PermissionService {

    private static Map<String, List<UserPermission>> userPermissionsMap = new HashMap<>();
    private static Map<PermissionAction, Function<UserInfo, UserInfo>> permissionActionMap = new HashMap<>();

    public PermissionService() {
        constructActionMap();
    }

    public List<UserPermission> getUserPermissions(String username) {
        return userPermissionsMap.getOrDefault(username, new ArrayList<>());
    }

    public void addUserPermission(UserPermission userPermission) {
        List<UserPermission> userPermissionList = userPermissionsMap.getOrDefault(userPermission.getUsername(), new ArrayList<>());
        userPermissionList.add(userPermission);

        userPermissionsMap.put(userPermission.getUsername(), userPermissionList);
    }

    public UserInfo updateUserPermissions(UserInfo userInfo) {
        List<UserPermission> userPermissions = userPermissionsMap.getOrDefault(userInfo.getUsername(), new ArrayList<>());
        if (userPermissions.isEmpty()) return userInfo;

        Date tokenIssueDate = userInfo.getTokenIssueDate();

        for (UserPermission userPermission : userPermissions) {
            // If the userPermission was set on the account after the token was created, apply it to the user.
            if (tokenIssueDate.before(userPermission.getIssuedAt())) {
                PermissionAction action = userPermission.getAction();
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
