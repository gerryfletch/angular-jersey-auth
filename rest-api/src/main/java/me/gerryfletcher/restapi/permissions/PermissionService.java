package me.gerryfletcher.restapi.permissions;

import me.gerryfletcher.restapi.authentication.Role;
import me.gerryfletcher.restapi.exceptions.permissions.UserLoggedOutException;
import me.gerryfletcher.restapi.exceptions.permissions.UserRevokedException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PermissionService {

    // Lookup a users permission by username
    private static Map<String, UserPermissions> userPermissionsMap = new HashMap<>();

    // Get the function to convert a role based on an action.
    private static Map<PermissionAction, Function<String, String>> actionToRoleMap = new HashMap<>();

    public PermissionService() {
        constructActionMap();
    }

    public void putUserPermission(UserPermissions permissions) {
        userPermissionsMap.put(permissions.getUsername(), permissions);
    }

    public UserPermissions getUserPermissions(String username) {
        return userPermissionsMap.get(username);
    }

    /**
     * Get the permission altering function based on the action provided, and return the new role formed.
     * @return The new role of the user.
     */
    public String getRoleFromAction(String originalRole, PermissionAction action) {
        return actionToRoleMap.get(action).apply(originalRole);
    }

    /**
     * Removes any permission actions from a user.
     * @return the removed permissions.
     */
    public UserPermissions clearUserPermission(String username) {
        return userPermissionsMap.remove(username);
    }

    private void constructActionMap() {
        actionToRoleMap.put(PermissionAction.DEMOTE, role  -> Role.USER);
        actionToRoleMap.put(PermissionAction.PROMOTE, role -> Role.ADMIN);
        actionToRoleMap.put(PermissionAction.LOGOUT, role  -> { throw new UserLoggedOutException(); });
        actionToRoleMap.put(PermissionAction.REVOKE, role  -> { throw new UserRevokedException(); });
    }

}
