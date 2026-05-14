package com.why.buildingmanagement.shareandhelp.application.port.out;

import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPost;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadShareAndHelpPostPort {

    List<ShareAndHelpPost> loadByBuildingId(final UUID buildingId);

    Optional<ShareAndHelpPost> loadById(final UUID postId);

    Optional<ShareAndHelpPost> loadByIdAndCreatedByUserId(final UUID postId,
                                                          final Long createdByUserId);
}