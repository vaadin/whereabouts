package com.example.whereabouts.security;

import java.util.Set;

public final class AppRoles {

    private AppRoles() {
    }

    public static final String LOCATION_READ = "LOCATION:READ";
    public static final String LOCATION_CREATE = "LOCATION:CREATE";
    public static final String LOCATION_UPDATE = "LOCATION:UPDATE";
    public static final Set<String> LOCATION_WRITE = Set.of(LOCATION_READ, LOCATION_CREATE, LOCATION_UPDATE);


    public static final String EMPLOYEE_READ = "EMPLOYEE:READ";
    public static final String EMPLOYEE_CREATE = "EMPLOYEE:CREATE";
    public static final String EMPLOYEE_UPDATE = "EMPLOYEE:UPDATE";
    public static final Set<String> EMPLOYEE_WRITE = Set.of(EMPLOYEE_READ, EMPLOYEE_CREATE, EMPLOYEE_UPDATE);

    public static final String PROJECT_READ = "PROJECT:READ";
    public static final String PROJECT_CREATE = "PROJECT:CREATE";
    public static final String TASK_CREATE = "TASK:CREATE";
    public static final String TASK_UPDATE = "TASK:UPDATE";
    public static final String TASK_DELETE = "TASK:DELETE";
    public static final Set<String> PROJECT_WRITE = Set.of(PROJECT_READ, PROJECT_CREATE, TASK_CREATE, TASK_UPDATE, TASK_DELETE);
    public static final Set<String> TASK_WRITE = Set.of(PROJECT_READ, TASK_CREATE, TASK_UPDATE, TASK_DELETE);

    public static final Set<String> READ_ONlY = Set.of(LOCATION_READ, EMPLOYEE_READ, PROJECT_READ);
    public static final Set<String> ALL = Set.of(
            LOCATION_READ, LOCATION_CREATE, LOCATION_UPDATE,
            EMPLOYEE_READ, EMPLOYEE_CREATE, EMPLOYEE_UPDATE,
            PROJECT_READ, PROJECT_CREATE, TASK_CREATE, TASK_UPDATE, TASK_DELETE
    );
}
