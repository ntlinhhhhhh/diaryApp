
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

    public async Task<IEnumerable<UserSearchResponseDto>> SearchUsersAsync(string name, int limit)
    {
        if (string.IsNullOrWhiteSpace(name))
            throw new Exception("Vui lòng nhập từ khóa để tìm kiếm.");

        var users = await _userRepository.SearchByNameAsync(name, limit);

        return users.Select(u => new UserSearchResponseDto
        {
            Id = u.Id,
            Name = u.Name,
            AvatarUrl = u.AvatarUrl,
            Email = u.Email
        });
    }

    public async Task BuyThemeAsync(string userId, BuyThemeRequestDto request)
    {
        var user = await _userRepository.GetByIdAsync(userId);
        if (user == null) throw new Exception("Không tìm thấy người dùng.");

        var ownedThemes = await _userRepository.GetOwnedThemeIdsAsync(userId);
        if (ownedThemes.Contains(request.ThemeId))
        {
            throw new Exception("Bạn đã sở hữu giao diện này rồi.");
        }

        if (user.CoinBalance < request.Price)
        {
            throw new Exception($"Bạn không đủ xu. Cần {request.Price} xu để mua giao diện này.");
        }

        await _userRepository.UpdateCoinBalanceAsync(userId, -request.Price);
        await _userRepository.AddOwnedThemeAsync(userId, request.ThemeId);
    }

    public async Task ChangeActiveThemeAsync(string userId, UpdateThemeRequestDto request)
    {
        var ownedThemes = await _userRepository.GetOwnedThemeIdsAsync(userId);
        if (!ownedThemes.Contains(request.ThemeId)) 
        {
            throw new Exception("Bạn chưa sở hữu giao diện này");
        }

        await _userRepository.SetActiveThemeAsync(userId, request.ThemeId);
    }

    public async Task<List<string>> GetMyThemeIdsAsync(string userId)
    {
        return await _userRepository.GetOwnedThemeIdsAsync(userId);
    }
}