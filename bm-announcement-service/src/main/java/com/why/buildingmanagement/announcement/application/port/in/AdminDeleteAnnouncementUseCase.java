package com.why.buildingmanagement.announcement.application.port.in;

import java.util.UUID;

public interface AdminDeleteAnnouncementUseCase {

    void deleteAnnouncementByAdmin(UUID announcementId);
}