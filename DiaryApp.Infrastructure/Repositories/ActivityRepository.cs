using DiaryApp.Application.Interfaces;
using DiaryApp.Domain.Entities;
using DiaryApp.Infrastructure.Data;
using Google.Cloud.Firestore;

namespace DiaryApp.Infrastructure.Repositories;

public class ActivityRepository : IActivityRepository
{
    private readonly FirestoreDb _db;
    private readonly CollectionReference _activitiesCollection;

    public ActivityRepository(FirestoreProvider provider)
    {
        _db = provider.Database;
        _activitiesCollection = _db.Collection("activities");
    }
    
    async Task<IEnumerable<Activity>> IActivityRepository.GetAllAsync()
    {
        Query query = _activitiesCollection.OrderBy("Name");
        QuerySnapshot snapshot = await query.GetSnapshotAsync();
        return snapshot.Documents.Select(MapSnapshotToActivity);
    }

    async Task<IEnumerable<Activity>> IActivityRepository.GetByCategoryAsync(string category)
    {
        Query query = _activitiesCollection
        .WhereEqualTo("Category", category)
        .OrderBy("Name");

        QuerySnapshot snapshot = await query.GetSnapshotAsync();
        return snapshot.Documents.Select(MapSnapshotToActivity);
    }

    async Task<Activity?> IActivityRepository.GetByIdAsync(string id)
    {
        DocumentReference docRef = _activitiesCollection.Document(id);
        DocumentSnapshot snapshot = await docRef.GetSnapshotAsync();

        if (!snapshot.Exists) return null;

        return MapSnapshotToActivity(snapshot);
    }

    private Activity MapSnapshotToActivity(DocumentSnapshot snapshot)
    {
        return new Activity
        {
            Id = snapshot.Id,
            Name = snapshot.GetValue<string>("Name"),
            IconUrl = snapshot.GetValue<string>("IconUrl"),
            Category = snapshot.ContainsField("Category") ? snapshot.GetValue<string>("Category") : "Other"
        };
    }
}