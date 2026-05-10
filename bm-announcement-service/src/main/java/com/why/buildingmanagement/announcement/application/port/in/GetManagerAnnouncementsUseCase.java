package com.why.buildingmanagement.announcement.application.port.in;

import com.why.buildingmanagement.announcement.application.result.AnnouncementResult;

import java.util.List;

public interface GetManagerAnnouncementsUseCase {

    List<AnnouncementResult> getManagerAnnouncements(GetManagerAnnouncementsQuery query);
}