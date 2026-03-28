using DiaryApp.Application.DTOs;
using DiaryApp.Application.DTOs.Notification; // Giả định namespace chứa DTO
using DiaryApp.Application.Interfaces;
using DiaryApp.Domain.Entities;

namespace DiaryApp.Application.Services;

public class AppNotificationService(IAppNotificationRepository notificationRepository) : IAppNotificationService
{
    private readonly IAppNotificationRepository _notificationRepository = notificationRepository;

    public async Task<AppNotificationResponseDto> CreateNotificationAsync(AppNotificationRequestDto request)
    {
        var newNotification = new AppNotification
        {
            Id = Guid.NewGuid().ToString(),
            UserId = request.UserId,
            Title = request.Title,
            Message = request.Message,
            Type = request.Type ?? "System",
            IsRead = false,
            CreatedAt = DateTime.UtcNow
        };

        await _notificationRepository.CreateAsync(newNotification);

        return MapToDto(newNotification);
    }

    public async Task<IEnumerable<AppNotificationResponseDto>> GetMyNotificationsAsync(string userId)
    {
        var notifications = await _notificationRepository.GetByUserIdAsync(userId);
        return notifications.Select(MapToDto);
    }

    public async Task MarkAsReadAsync(string notificationId, string currentUserId)
    {
        var notification = await _notificationRepository.GetByIdAsync(notificationId);
        
        if (notification == null)
        {
            throw new KeyNotFoundException("Không tìm thấy thông báo.");
        }

        if (notification.UserId != currentUserId)
        {
            throw new UnauthorizedAccessException("Bạn không có quyền thao tác trên thông báo này.");
        }

        if (!notification.IsRead)
        {
            await _notificationRepository.MarkAsReadAsync(notificationId);
        }
    }

    public async Task DeleteNotificationAsync(string notificationId, string currentUserId)
    {
        var notification = await _notificationRepository.GetByIdAsync(notificationId);
        
        if (notification == null)
        {
            throw new KeyNotFoundException("Không tìm thấy thông báo cần xóa.");
        }

        if (notification.UserId != currentUserId)
        {
            throw new UnauthorizedAccessException("Bạn không có quyền xóa thông báo này.");
        }

        await _notificationRepository.DeleteByIdAsync(notificationId);
    }

    public async Task DeleteAllMyNotificationsAsync(string userId)
    {
        await _notificationRepository.DeleteAllByUserIdAsync(userId);
    }

    private static AppNotificationResponseDto MapToDto(AppNotification notification)
    {
        return new AppNotificationResponseDto
        {
            Id = notification.Id,
            Title = notification.Title,
            Message = notification.Message,
            Type = notification.Type,
            IsRead = notification.IsRead,
            CreatedAt = notification.CreatedAt
        };
    }
}