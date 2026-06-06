package com.why.buildingmanagement.auth.application.port.in;

import com.why.buildingmanagement.auth.application.result.AdminBuildingUserResult;

import java.util.List;

public interface GetAllBuildingUsersUseCase {

    List<AdminBuildingUserResult> getAllUsers();
}