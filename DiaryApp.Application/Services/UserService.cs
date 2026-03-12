
using DiaryApp.Application.DTOs.User;
using DiaryApp.Application.Interfaces;
using DiaryApp.Domain.Entities;

namespace DiaryApp.Application.Services;

public class UserService(IUserRepository userRepository) : IUserService
{
    private readonly IUserRepository _userRepository = userRepository;

    public async Task<UserProfileDto> GetProfileAsync(string userId)
    {
        var user = await _userRepository.GetByIdAsync(userId);
        if (user == null)
        {
            throw new Exception("Không tìm thấy thông tin người dùng");
        }
        return new UserProfileDto
        {
            Id = user.Id,
            Email = user.Email,
            Name = user.Name,
            AvatarUrl = user.AvatarUrl,
            Gender = user.Gender,
            Birthday = user.Birthday,
            CoinBalance = user.CoinBalance,
            AuthProvider = user.AuthProvider,
            ActiveThemeId = user.ActiveThemeId,
            CreatedAt = user.CreatedAt
        };
    }

    public async Task UpdateProfileAsync(string userId, UpdateProfileRequestDto request)
    {
        var user = await _userRepository.GetByIdAsync(userId);
        
        if (user == null) throw new Exception("Người dùng không tồn tại");

        await _userRepository.UpdateProfileAsync(
            userId: userId,
            name: request.Name.Trim(),
            newPassword: null,
            avatarUrl: request.AvatarUrl,
            gender: request.Gender,
            birthday: request.Birthday
        );
    }
}