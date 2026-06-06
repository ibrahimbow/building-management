package com.why.buildingmanagement.announcement.application.port.in;

import com.why.buildingmanagement.announcement.application.result.AnnouncementResult;

import java.util.List;

public interface AdminGetAnnouncementsUseCase {

    List<AnnouncementResult> getAllAnnouncements();
}