using System;
using DiaryApp.Application.DTOs.Theme;
using DiaryApp.Domain.Entities;
using DiaryApp.Domain.Enums;

namespace DiaryApp.Application.Interfaces.Services;

public class ThemeService(IThemeRepository themeRepository) : IThemeService
{
    private readonly IThemeRepository _themeRepository = themeRepository;

    public async Task<IEnumerable<ThemeResponseDto>> GetAllActiveThemesAsync()
    {
        var themes = await _themeRepository.GetAllActiveThemesAsync();

        return  themes.Select(theme => new ThemeResponseDto()
        {
            Id = theme.Id,
            Name = theme.Name,
            Price = theme.Price,
            ThumbnailUrl = theme.ThumbnailUrl,
            BackgroundUrl = theme.BackgroundUrl   
        });
    }

    public async Task<ThemeResponseDto?> GetThemeByIdAsync(string themeId)
    {
        var theme = await _themeRepository.GetByIdAsync(themeId);
        if (theme == null || !theme.IsActive)
        {
            return null;
        }
        return new ThemeResponseDto
        {
            Id = themeId,
            Name = theme.Name,
            Price = theme.Price,
            ThumbnailUrl = theme.ThumbnailUrl,
            BackgroundUrl = theme.BackgroundUrl   
        };
    }
    
    public async Task<ThemeMoodResponseDto?> GetMoodIconAsync(string themeId, BaseMood baseMoodId)
    {
        var moodIcon = await _themeRepository.GetMoodIconAsync(themeId, baseMoodId);

        if (moodIcon == null) return null;

        return new ThemeMoodResponseDto
        {
            BaseMoodId = baseMoodId.ToString(),
            IconUrl = moodIcon.IconUrl,
            CustomName = moodIcon.CustomName
        };
    }

    public async Task<IEnumerable<ThemeMoodResponseDto>> GetThemeMoodsAsync(string themeId)
    {
        var theme = await _themeRepository.GetByIdAsync(themeId);
        
        if (theme == null || theme.Moods == null) 
            return Enumerable.Empty<ThemeMoodResponseDto>();

        return theme.Moods.Select(m => new ThemeMoodResponseDto
        {
            BaseMoodId = m.BaseMoodId.ToString(),
            IconUrl = m.IconUrl,
            CustomName = m.CustomName
        });
    }

    public async Task CreateThemeAsync(CreateThemeRequestDto request)
    {
        var existingTheme = await _themeRepository.GetByIdAsync(request.Id);
        if (existingTheme != null)
        {
            throw new Exception($"Giao diện với mã '{request.Id}' đã tồn tại.");
        }

        var newTheme = new Theme
        {
            Id = request.Id,
            Name = request.Name,
            Price = request.Price,
            ThumbnailUrl = request.ThumbnailUrl ?? "",
            BackgroundUrl = request.BackgroundUrl ?? "",
            IsActive = request.IsActive,
            Moods = request.Moods.Select(m => new ThemeMoodIcon
            {
                BaseMoodId = m.BaseMoodId,
                IconUrl = m.IconUrl,
                CustomName = m.CustomName ?? ""
            }).ToList()
        };

        await _themeRepository.CreateThemeAsync(newTheme);
    }

    public async Task UpdateThemeAsync(string themeId, CreateThemeRequestDto request)
    {
        var existingTheme = await _themeRepository.GetByIdAsync(themeId);
        if (existingTheme == null)
        {
            throw new Exception($"Không tìm thấy giao diện với mã '{themeId}'.");
        }

        var updatedTheme = new Theme
        {
            Id = themeId,
            Name = request.Name,
            Price = request.Price,
            ThumbnailUrl = request.ThumbnailUrl ?? "",
            BackgroundUrl = request.BackgroundUrl ?? "",
            IsActive = request.IsActive,
            Moods = request.Moods.Select(m => new ThemeMoodIcon
            {
                BaseMoodId = m.BaseMoodId,
                IconUrl = m.IconUrl,
                CustomName = m.CustomName ?? ""
            }).ToList()
        };

        await _themeRepository.UpdateThemeAsync(updatedTheme);
    }
}
