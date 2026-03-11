using DiaryApp.Application.Interfaces;
using DiaryApp.Domain.Entities;
using DiaryApp.Infrastructure.Data;
using Google.Cloud.Firestore;
using Google.Cloud.Firestore.V1;

namespace DiaryApp.Infrastructure.Repositories;

public class ActivityRepository : IActivityRepository
{
    private readonly FirestoreDb _db;
    private readonly CollectionReference _activityCollection;

    public ActivityRepository(FirestoreProvider provider)
    {
        _db = provider.Database;
        _activityCollection = _db.Collection("activities");
    }
    
    Task<IEnumerable<Activity>> IActivityRepository.GetAllAsync()
    {
        throw new NotImplementedException();
    }

    Task<IEnumerable<Activity>> IActivityRepository.GetByCategoryAsync(string category)
    {
        throw new NotImplementedException();
    }

    Task<Activity?> IActivityRepository.GetByIdAsync(string id)
    {
        throw new NotImplementedException();
    }
}