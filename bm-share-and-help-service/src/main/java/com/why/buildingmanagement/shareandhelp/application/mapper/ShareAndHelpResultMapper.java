package com.why.buildingmanagement.shareandhelp.application.mapper;

import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpCommentResult;
import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpComment;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPost;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShareAndHelpResultMapper {

    ShareAndHelpPostResult toResult(ShareAndHelpPost post);

    ShareAndHelpCommentResult toResult(ShareAndHelpComment comment);
}