package com.why.buildingmanagement.building.infrastructure.security;

import java.util.List;

public record AuthenticatedUser(String userId,
                                String email,
                                String username,
                                List<String> roles) {
}