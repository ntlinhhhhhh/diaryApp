using DiaryApp.Application.Interfaces;
using DiaryApp.Domain.Entities;
using DiaryApp.Infrastructure.Data;
using Google.Cloud.Firestore;

namespace DiaryApp.Infrastructure.Repositories;

public class ThemeRepository : IThemeRepository
{
    private readonly FirestoreDb _db;
    private readonly CollectionReference _themeCollection;

    public ThemeRepository(FirestoreProvider provider)
    {
        _db = provider.Database;
        _themeCollection = _db.Collection("themes");
    }


    Task IThemeRepository.CreateThemeAsync(Theme theme)
    {
        throw new NotImplementedException();
    }

    Task<IEnumerable<Theme>> IThemeRepository.GetAllActiveThemesAsync()
    {
        throw new NotImplementedException();
    }

    Task<Theme?> IThemeRepository.GetByIdAsync(string themeId)
    {
        throw new NotImplementedException();
    }

    Task<ThemeMoodIcon?> IThemeRepository.GetMoodIconAsync(string themeId, int baseMoodId)
    {
        throw new NotImplementedException();
    }

    Task IThemeRepository.UpdateThemeAsync(Theme theme)
    {
        throw new NotImplementedException();
    }
}