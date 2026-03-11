using DiaryApp.Application.Interfaces;
using DiaryApp.Domain.Entities;
using DiaryApp.Infrastructure.Data;
using Google.Cloud.Firestore;

namespace DiaryApp.Infrastructure.Repositories;

public class DailyLogRepository : IDailyLogRepository
{
    private readonly FirestoreDb _db;
    private readonly CollectionReference _dailyLodCollection;

    public DailyLogRepository(FirestoreProvider provider)
    {
        _db = provider.Database;
        _dailyLodCollection = _db.Collection("dailyLogs");
    }

    Task IDailyLogRepository.DeleteAsync(string userId, string dateId)
    {
        throw new NotImplementedException();
    }

    Task<DailyLog?> IDailyLogRepository.GetByDateAsync(string userId, string date)
    {
        throw new NotImplementedException();
    }

    Task<IEnumerable<DailyLog>> IDailyLogRepository.GetLogsByActivityAsync(string userId, string activityId)
    {
        throw new NotImplementedException();
    }

    Task<IEnumerable<DailyLog>> IDailyLogRepository.GetLogsByMenstruationAsync(string userId, bool isMenstruation)
    {
        throw new NotImplementedException();
    }

    Task<IEnumerable<DailyLog>> IDailyLogRepository.GetLogsByMonthAsync(string userId, string yearMonth)
    {
        throw new NotImplementedException();
    }

    Task<IEnumerable<DailyLog>> IDailyLogRepository.GetLogsByMoodAsync(string userId, int moodId)
    {
        throw new NotImplementedException();
    }

    Task<IEnumerable<DailyLog>> IDailyLogRepository.SearchByNoteAsync(string userId, string keyword)
    {
        throw new NotImplementedException();
    }

    Task IDailyLogRepository.UpsertAsync(string userId, DailyLog log)
    {
        throw new NotImplementedException();
    }
}