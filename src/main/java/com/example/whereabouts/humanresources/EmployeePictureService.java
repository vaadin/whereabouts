package com.example.whereabouts.humanresources;

import com.vaadin.flow.server.streams.DownloadHandler;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@PreAuthorize("isAuthenticated()")
@NullMarked
public class EmployeePictureService {

    public @Nullable DownloadHandler findPicture(EmployeeId employeeId) {
        return null; // TODO Implement me!
    }
}
