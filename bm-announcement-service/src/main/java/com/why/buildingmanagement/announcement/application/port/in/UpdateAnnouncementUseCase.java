package com.why.buildingmanagement.announcement.application.port.in;

import com.why.buildingmanagement.announcement.application.result.AnnouncementResult;

public interface UpdateAnnouncementUseCase {

    AnnouncementResult updateAnnouncement(UpdateAnnouncementCommand command);
}