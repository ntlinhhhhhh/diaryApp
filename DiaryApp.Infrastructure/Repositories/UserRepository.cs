using DiaryApp.Application.Interfaces;
using DiaryApp.Domain.Entities;
using DiaryApp.Infrastructure.Data;
using Google.Cloud.Firestore;

namespace DiaryApp.Infrastructure.Repositories;

public class UserRepository : IUserRepository
{
    private readonly FirestoreDb _db;
    private readonly CollectionReference _usersCollection;

    public UserRepository(FirestoreProvider provider)
    {
        _db = provider.Database;
        _usersCollection = _db.Collection("users");
    }

    Task IUserRepository.AddOwnedThemeAsync(string userId, string themeId)
    {
        throw new NotImplementedException();
    }

    Task IUserRepository.CreateAsync(User user)
    {
        throw new NotImplementedException();
    }

    Task IUserRepository.DeleteAsync(string userId)
    {
        throw new NotImplementedException();
    }

    Task<bool> IUserRepository.ExistsByEmailAsync(string email)
    {
        throw new NotImplementedException();
    }

    Task<User?> IUserRepository.GetByEmailAsync(string email)
    {
        throw new NotImplementedException();
    }

    Task<User?> IUserRepository.GetByIdAsync(string id)
    {
        throw new NotImplementedException();
    }

    Task<List<string>> IUserRepository.GetOwnedThemeIdsAsync(string userId)
    {
        throw new NotImplementedException();
    }

    Task<IEnumerable<User>> IUserRepository.SearchByNameAsync(string name, int limit)
    {
        throw new NotImplementedException();
    }

    Task IUserRepository.SetActiveThemeAsync(string userId, string themeId)
    {
        throw new NotImplementedException();
    }

    Task IUserRepository.UpdateCoinBalanceAsync(string userId, int amount)
    {
        throw new NotImplementedException();
    }

    Task IUserRepository.UpdateProfileAsync(string userId, string name, string? avatarUrl, string? gender, string? birthday)
    {
        throw new NotImplementedException();
    }
}