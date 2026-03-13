using DiaryApp.Application.DTOs.Activity;
using DiaryApp.Application.Interfaces;
using DiaryApp.Application.Interfaces.Services;
using DiaryApp.Domain.Entities;

namespace DiaryApp.Application.Services;

public class ActivityService(IActivityRepository activityRepository) : IActivityService
{
    private readonly IActivityRepository _activityRepository = activityRepository;

    public async Task<ActivityResponseDto> CreateActivityAsync(ActivityRequestDto request)
    {
        var newActivity = new Activity
        {
            Id = Guid.NewGuid().ToString(),
            Name = request.Name,
            IconUrl = request.IconUrl,
            Category = request.Category ?? "Other"
        };

        await _activityRepository.CreateAsync(newActivity);
        
        return MapToDto(newActivity);
    }

    public async Task UpdateActivityAsync(string id, ActivityRequestDto request)
    {
        var existingActivity = await _activityRepository.GetByIdAsync(id);
        if (existingActivity == null)
        {
            throw new Exception("Không tìm thấy hoạt động cần cập nhật.");
        }

        existingActivity.Name = request.Name;
        existingActivity.IconUrl = request.IconUrl;
        existingActivity.Category = request.Category ?? "Other";

        await _activityRepository.UpdateAsync(existingActivity);
    }

    public async Task DeleteActivityAsync(string activityId)
    {
        var existingActivity = await _activityRepository.GetByIdAsync(activityId);
        if (existingActivity == null)
        {
            throw new Exception("Không tìm thấy hoạt động cần xóa.");
        }

        await _activityRepository.DeleteAsync(activityId);
    }

    public async Task<IEnumerable<ActivityResponseDto>> GetAllActivitiesAsync()
    {
        var activities = await _activityRepository.GetAllAsync();
        return activities.Select(MapToDto);
    }

    public async Task<IEnumerable<ActivityResponseDto>> GetActivitiesByCategoryAsync(string category)
    {
        var activities = await _activityRepository.GetByCategoryAsync(category);
        return activities.Select(MapToDto);
    }

    public async Task<ActivityResponseDto?> GetActivityByIdAsync(string activityId)
    {
        var activity = await _activityRepository.GetByIdAsync(activityId);
        
        if (activity == null) return null;
        
        return MapToDto(activity);
    }

    private static ActivityResponseDto MapToDto(Domain.Entities.Activity activity)
    {
        return new ActivityResponseDto
        {
            Id = activity.Id,
            Name = activity.Name,
            IconUrl = activity.IconUrl,
            Category = activity.Category ?? "Other"
        };
    }

}