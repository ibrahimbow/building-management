package com.why.buildingmanagement.shareandhelp.infrastructure.api.mapper;

import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpCommentResult;
import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;
import com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.response.ShareAndHelpCommentResponse;
import com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.response.ShareAndHelpPostResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShareAndHelpApiMapper {

    ShareAndHelpPostResponse toResponse(ShareAndHelpPostResult result);

    ShareAndHelpCommentResponse toResponse(ShareAndHelpCommentResult result);
}