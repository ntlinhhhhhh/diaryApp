
using DiaryApp.Application.DTOs.User;
using DiaryApp.Application.Interfaces;
using DiaryApp.Domain.Entities;

namespace DiaryApp.Application.Services;

public class UserService(
    IUserRepository userRepository,
    IThemeRepository themeRepository
    ) : IUserService
{
    private readonly IUserRepository _userRepository = userRepository;
    private readonly IThemeRepository _themeRepository = themeRepository;

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
            Role = user.Role.ToString(),
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
        var theme = await _themeRepository.GetByIdAsync(request.ThemeId);
    
        if (theme == null || !theme.IsActive) 
        {
            throw new Exception("Giao diện này không tồn tại hoặc đã ngừng bán.");
        }

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
        var theme = await _themeRepository.GetByIdAsync(request.ThemeId);
        if (theme == null || !theme.IsActive)
        {
            throw new Exception("Giao diện không hợp lệ hoặc đã bị gỡ bỏ khỏi hệ thống.");
        }
        
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

    public async Task DeleteUserAsync(string userId)
    {
        var user = await _userRepository.GetByIdAsync(userId);
        if (user == null)
        {
            throw new Exception("Không tìm thấy người dùng cần xóa");
        }
        await _userRepository.DeleteAsync(userId);
    }

    public async Task<IEnumerable<UserSearchResponseDto>> GetAllUsersAsync()
    {
        var users = await _userRepository.GetAllUsersAsync();
        return users.Select(u => new UserSearchResponseDto
        {
            Id = u.Id,
            Name = u.Name,
            AvatarUrl = u.AvatarUrl,
            Email = u.Email
        });
    }
}