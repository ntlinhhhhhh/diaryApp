using DiaryApp.Domain.Entities;

namespace DiaryApp.Application.Interfaces;

public interface IDailyLogRepository
{
    // get daily_log, date "YYYY-MM-DD"
    Task<DailyLog?> GetByDateAsync(string userId, string date);
    
    // get log of a month
    Task<IEnumerable<DailyLog>> GetLogsByMonthAsync(string userId, string yearMonth);

    // get by mood or activity
    Task<IEnumerable<DailyLog>> GetLogsByMoodAsync(string userId, int moodId); 
    Task<IEnumerable<DailyLog>> GetLogsByActivityAsync(string userId, string activityId);
    Task<IEnumerable<DailyLog>> GetLogsByMenstruationAsync(string userId, bool isMenstruation); // isMenstruation = true
    Task<IEnumerable<DailyLog>> SearchByNoteAsync(string userId, string keyword); // search by keyword of note
    
    // create or update (Upsert)
    Task UpsertAsync(string userId, DailyLog log);
    
    // delete daily_log
    Task DeleteAsync(string userId, string dateId);
}