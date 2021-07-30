package com.alkemy.ong.service.Interface;

import com.alkemy.ong.dto.request.ActivityRequestDto;
import com.alkemy.ong.dto.response.ActivityResponseDto;
import com.alkemy.ong.model.Activity;

import java.util.List;

public interface IActivity {

    ActivityResponseDto createActivity(ActivityRequestDto activitiesDto);

    ActivityResponseDto updateActivity(Long id, ActivityRequestDto activitiesDto);

    List<ActivityResponseDto> getAllActivities();

    void deleteActivity(Long id);

    Activity getActivityById(Long id);

}
