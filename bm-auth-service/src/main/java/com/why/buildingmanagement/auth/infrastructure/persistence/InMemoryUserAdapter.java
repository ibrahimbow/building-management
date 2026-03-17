package com.why.buildingmanagement.auth.infrastructure.persistence;

import com.why.buildingmanagement.auth.application.port.out.LoadBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.SaveBuildingUserPort;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryUserAdapter implements LoadBuildingUserPort, SaveBuildingUserPort {
    private final Map<Long, BuildingUser> storage = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public Optional<BuildingUser> loadByUsernameOrEmail(String usernameOrEmail) {
        return storage.values().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(usernameOrEmail) || u.getEmail().equalsIgnoreCase(usernameOrEmail))
                .findFirst();
    }

    @Override
    public boolean existsByUsername(String username) {
        return storage.values().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return storage.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public BuildingUser save(BuildingUser user) {
        Long id = seq.getAndIncrement();
        BuildingUser saved = new BuildingUser(id, user.getUsername(), user.getEmail(), user.getPasswordHash(),
                user.getRole(), user.getCreatedAt(), user.isEnabled());
        storage.put(id, saved);
        return saved;
    }
}
