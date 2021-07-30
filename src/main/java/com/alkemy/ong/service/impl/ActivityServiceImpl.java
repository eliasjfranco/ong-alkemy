package com.alkemy.ong.service.impl;

import com.alkemy.ong.dto.request.ActivityRequestDto;
import com.alkemy.ong.dto.response.ActivityResponseDto;
import com.alkemy.ong.model.Activity;
import com.alkemy.ong.repository.ActivityRepository;
import com.alkemy.ong.service.Interface.IActivity;
import com.alkemy.ong.service.Interface.IFileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
public class ActivityServiceImpl implements IActivity {

    private final ActivityRepository activityRepository;
    private final IFileStore fileStore;
    private final ProjectionFactory projectionFactory;
    private final MessageSource messageSource;

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository, IFileStore fileStore, ProjectionFactory projectionFactory, MessageSource messageSource) {
        this.activityRepository = activityRepository;
        this.fileStore = fileStore;
        this.projectionFactory = projectionFactory;
        this.messageSource = messageSource;
    }


    @Override
    public ActivityResponseDto createActivity(ActivityRequestDto activityRequestDto) {

        Activity activity = Activity.builder()
                .name(activityRequestDto.getName())
                .content(activityRequestDto.getContent())
                .created(new Date())
                .build();
        Activity activityCreated = activityRepository.save(activity);
        
        if(!activityRequestDto.getImage().isEmpty())
        	activityCreated.setImage(fileStore.save(activityCreated, activityRequestDto.getImage()));
        
        return projectionFactory.createProjection(ActivityResponseDto.class, activityRepository.save(activityCreated));
    }

    @Override
    public ActivityResponseDto updateActivity(Long id, ActivityRequestDto dto) {
        Activity activity = getActivityById(id);

        if(!dto.getName().isBlank())
        	activity.setName(dto.getName());
        if(!dto.getContent().isBlank())
        	activity.setContent(dto.getContent());
        
        if(!dto.getImage().isEmpty())
        	activity.setImage(fileStore.save(activity, dto.getImage()));
        
        activity.setEdited(new Date());
        return projectionFactory.createProjection(ActivityResponseDto.class, activityRepository.save(activity));
    }

    @Override
    public List<ActivityResponseDto> getAllActivities() {
        return activityRepository.findAllProjectedBy();
    }

    @Override
    public void deleteActivity(Long id) {
        Activity activity = getActivityById(id);
        activity.setDeletedAt(new Date());
        fileStore.deleteFilesFromS3Bucket(activity);
        activityRepository.save(activity);
        activityRepository.delete(activity);
    }

    @Override
    public Activity getActivityById(Long id) {
        return activityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        messageSource.getMessage("activity.error.not.found", null, Locale.getDefault())
                )
        );
    }


}
