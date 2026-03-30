
using DiaryApp.Application.DTOs.User;
using DiaryApp.Application.Interfaces;
using DiaryApp.Application.Interfaces.Services;
using DiaryApp.Domain.Entities;

namespace DiaryApp.Application.Services;

public class UserService(
    IUserRepository userRepository,
    IThemeRepository themeRepository,
    IMomentRepository momentRepository,
    IRedisCacheService cacheService
    ) : IUserService
{
    private readonly IUserRepository _userRepository = userRepository;
    private readonly IThemeRepository _themeRepository = themeRepository;
    private readonly IMomentRepository _momentRepository = momentRepository;
    private readonly IRedisCacheService _cacheService = cacheService;

    public async Task<UserProfileDto> GetProfileAsync(string userId)
    {
        string cacheKey = $"user_profile:{userId}";
        var cachedUser = await _cacheService.GetAsync<UserProfileDto>(cacheKey);

        if (cachedUser != null) return cachedUser;

        var user = await _userRepository.GetByIdAsync(userId);
        if (user == null)
        {
            throw new KeyNotFoundException("Không tìm thấy thông tin người dùng");
        }
        
        var profile = new UserProfileDto
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

        await _cacheService.SetAsync(cacheKey, profile, TimeSpan.FromMinutes(30));
        return profile;
    }

    public async Task UpdateProfileAsync(string userId, UpdateProfileRequestDto request)
    {
        var user = await _userRepository.GetByIdAsync(userId);
        
        if (user == null) throw new KeyNotFoundException("Người dùng không tồn tại");

        await _userRepository.UpdateProfileAsync(
            userId: userId,
            name: request.Name.Trim(),
            newPassword: null,
            avatarUrl: request.AvatarUrl,
            gender: request.Gender,
            birthday: request.Birthday
        );

        await _cacheService.RemoveAsync($"user_profile:{userId}");
        await _cacheService.RemoveAsync($"auth:email:{user.Email}");

        _ = Task.Run(async () => 
        {
            try
            {
                await _momentRepository.SyncUserMediaInMomentsAsync(userId, request.Name, request.AvatarUrl);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Lỗi khi đồng bộ Moment cho user {userId}: {ex.Message}");
            }
        });
    }

    public async Task<IEnumerable<UserSearchResponseDto>> SearchUsersAsync(string name, int limit)
    {
        if (string.IsNullOrWhiteSpace(name))
            throw new ArgumentNullException("Vui lòng nhập từ khóa để tìm kiếm.");

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
            throw new KeyNotFoundException("Giao diện này không tồn tại hoặc đã ngừng bán.");
        }

        var user = await _userRepository.GetByIdAsync(userId);
        if (user == null) throw new KeyNotFoundException("Không tìm thấy người dùng.");

        var ownedThemes = await _userRepository.GetOwnedThemeIdsAsync(userId);
        if (ownedThemes.Contains(request.ThemeId))
        {
            throw new InvalidOperationException("Bạn đã sở hữu giao diện này rồi.");
        }

        if (user.CoinBalance < request.Price)
        {
            throw new InvalidOperationException($"Bạn không đủ xu. Cần {request.Price} xu để mua giao diện này.");
        }

        await _userRepository.UpdateCoinBalanceAsync(userId, -request.Price);
        await _userRepository.AddOwnedThemeAsync(userId, request.ThemeId);

        await _cacheService.RemoveAsync($"user_profile:{userId}");
        await _cacheService.RemoveAsync($"owned_themes:{userId}");
    }

    public async Task ChangeActiveThemeAsync(string userId, UpdateThemeRequestDto request)
    {
        var theme = await _themeRepository.GetByIdAsync(request.ThemeId);
        if (theme == null || !theme.IsActive)
        {
            throw new KeyNotFoundException("Giao diện không hợp lệ hoặc đã bị gỡ bỏ khỏi hệ thống.");
        }
        
        var ownedThemes = await _userRepository.GetOwnedThemeIdsAsync(userId);
        if (!ownedThemes.Contains(request.ThemeId)) 
        {
            throw new Exception("Bạn chưa sở hữu giao diện này");
        }

        await _userRepository.SetActiveThemeAsync(userId, request.ThemeId);
        await _cacheService.RemoveAsync($"user_profile:{userId}");
    }

    public async Task<List<string>> GetMyThemeIdsAsync(string userId)
    {
        string cacheKey = $"owned_themes:{userId}";

        var cachedThemeIds = await _cacheService.GetAsync<List<string>>(cacheKey);
        if (cachedThemeIds != null) return cachedThemeIds;

        var themeIds = await _userRepository.GetOwnedThemeIdsAsync(userId);
        await _cacheService.SetAsync(cacheKey, themeIds, TimeSpan.FromHours(1));

        return themeIds;
    }

    public async Task DeleteUserAsync(string userId)
    {
        var user = await _userRepository.GetByIdAsync(userId);
        if (user == null)
        {
            throw new KeyNotFoundException("Không tìm thấy người dùng cần xóa");
        }
        await _userRepository.DeleteAsync(userId);
        await _cacheService.RemoveAsync($"user_profile:{userId}");
        await _cacheService.RemoveAsync($"auth:email:{user.Email}");
        await _cacheService.RemoveAsync($"owned_themes:{userId}");
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