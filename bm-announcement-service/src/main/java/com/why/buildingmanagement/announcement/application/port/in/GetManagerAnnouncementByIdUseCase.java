package com.why.buildingmanagement.announcement.application.port.in;

import com.why.buildingmanagement.announcement.application.result.AnnouncementResult;

public interface GetManagerAnnouncementByIdUseCase {
    AnnouncementResult getManagerAnnouncementById(GetManagerAnnouncementByIdQuery query);

}
