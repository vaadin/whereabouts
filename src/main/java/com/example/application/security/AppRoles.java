package com.example.application.security;

public final class AppRoles {

    private AppRoles() {
    }

    @Deprecated
    public static final String ADMIN = "ADMIN";

    @Deprecated
    public static final String USER = "USER";

    public static final String LOCATION_READ = "LOCATION:READ";
    public static final String LOCATION_WRITE = "LOCATION:WRITE";

    public static final String EMPLOYEE_READ = "EMPLOYEE:READ";
    public static final String EMPLOYEE_WRITE = "EMPLOYEE:WRITE";

    public static final String PROJECT_READ = "PROJECT:READ";
    public static final String PROJECT_CREATE = "PROJECT:CREATE";
    public static final String TASK_CREATE = "TASK:CREATE";
    public static final String TASK_UPDATE = "TASK:UPDATE";
    public static final String TASK_DELETE = "TASK:DELETE";
}
