package com.why.buildingmanagement.building.infrastructure.security;

import com.why.buildingmanagement.building.infrastructure.security.exception.InvalidUserHeaderException;
import com.why.buildingmanagement.building.infrastructure.security.exception.MissingUserHeaderException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
public class CurrentUserService {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String USER_DISPLAY_NAME_HEADER = "X-User-Display-Name";
    private static final String USER_AVATAR_URL_HEADER = "X-User-Avatar-Url";
    private static final String USER_PHONE_HEADER = "X-User-Phone";

    private final HttpServletRequest request;

    public CurrentUserService(final HttpServletRequest request) {
        this.request = request;
    }

    public CurrentUser getCurrentUser() {

        return new CurrentUser(requiredUserId(),
                               requiredHeader(USER_EMAIL_HEADER),
                               requiredHeader(USER_ROLE_HEADER),
                               requiredHeader(USER_DISPLAY_NAME_HEADER),
                               optionalHeader(USER_AVATAR_URL_HEADER),
                               optionalHeader(USER_PHONE_HEADER));
    }

    private String requiredHeader(final String name) {

        final String value = request.getHeader(name);

        if (value == null || value.isBlank()) {
            throw new MissingUserHeaderException("Missing required user header: " + name);
        }

        return value;
    }

    private String optionalHeader(final String name) {

        return request.getHeader(name);
    }

    private Long requiredUserId() {
        try {
            return Long.valueOf(requiredHeader(USER_ID_HEADER));
        } catch (final NumberFormatException ex) {
            throw new InvalidUserHeaderException("Invalid user id header: " + USER_ID_HEADER);
        }
    }
}