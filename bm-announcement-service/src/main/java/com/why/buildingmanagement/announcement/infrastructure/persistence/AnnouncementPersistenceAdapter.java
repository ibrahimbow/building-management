package com.why.buildingmanagement.announcement.infrastructure.persistence;

import com.why.buildingmanagement.announcement.application.port.out.AnnouncementRepositoryPort;
import com.why.buildingmanagement.announcement.domain.model.Announcement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class AnnouncementPersistenceAdapter implements AnnouncementRepositoryPort {

    private final AnnouncementRepository repository;
    private final AnnouncementMapper mapper;

    @Override
    public Announcement save(final Announcement announcement) {
        return mapper.toDomain(repository.save(mapper.toEntity(announcement)));
    }

    @Override
    public Optional<Announcement> findById(final UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Announcement> findByBuildingId(final UUID buildingId) {
        return repository.findByBuildingIdOrderByCreatedAtDesc(
                        buildingId,
                        PageRequest.of(0, 20))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(final Announcement announcement) {
        repository.delete(mapper.toEntity(announcement));
    }
}