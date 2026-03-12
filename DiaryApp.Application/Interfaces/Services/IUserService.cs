using DiaryApp.Application.DTOs.User;

namespace DiaryApp.Application.Interfaces;

public interface IUserService
{
    Task<UserProfileDto> GetProfileAsync(string userId);
    Task UpdateProfileAsync(string userId, UpdateProfileRequestDto request);
}