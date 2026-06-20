package com.why.buildingmanagement.shareandhelp.infrastructure.persistence;

import com.why.buildingmanagement.shareandhelp.application.port.out.LoadShareAndHelpPostPort;
import com.why.buildingmanagement.shareandhelp.application.port.out.SaveShareAndHelpPostPort;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPost;
import com.why.buildingmanagement.shareandhelp.infrastructure.persistence.entity.ShareAndHelpPostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ShareAndHelpPersistenceAdapter implements SaveShareAndHelpPostPort, LoadShareAndHelpPostPort {

    private final ShareAndHelpRepository repository;
    private final ShareAndHelpPersistenceMapper mapper;

    @Override
    public ShareAndHelpPost save(final ShareAndHelpPost post) {

        final ShareAndHelpPostEntity entity = mapper.toEntity(post);
        final ShareAndHelpPostEntity savedEntity = repository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<ShareAndHelpPost> loadByBuildingId(final UUID buildingId) {

        return repository.findAllByBuildingIdAndDeletedAtIsNullOrderByCreatedAtDesc(buildingId)
                         .stream()
                         .map(mapper::toDomain)
                         .toList();
    }

    @Override
    public Optional<ShareAndHelpPost> loadById(final UUID postId) {

        return repository.findByIdAndDeletedAtIsNull(postId)
                         .map(mapper::toDomain);
    }

    @Override
    public Optional<ShareAndHelpPost> loadByIdAndCreatedByUserId(final UUID postId,
                                                                 final Long createdByUserId) {

        return repository.findByIdAndCreatedByUserIdAndDeletedAtIsNull(postId, createdByUserId)
                         .map(mapper::toDomain);
    }

    @Override
    public List<ShareAndHelpPost> loadAll() {

        return repository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()
                         .stream()
                         .map(mapper::toDomain)
                         .toList();
    }
}