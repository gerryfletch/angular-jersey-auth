package me.gerryfletcher.restapi.permissions;

import java.util.*;

/**
 * This class acts as the DB would.
 */
public class PermissionService {

    private static Map<String, List<UserPermission>> userPermissionsMap = new HashMap<>();

    public PermissionService() {
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

        //noinspection ResultOfMethodCallIgnored
        userPermissions.stream()
                .filter(p -> p.getIssuedAt().before(tokenIssueDate))
                .map(p -> p.getAction().applyTo(userInfo));

        return userInfo;
    }

}
