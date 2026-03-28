using DiaryApp.Application.DTOs.DailyLog;
using DiaryApp.Application.Interfaces;
using DiaryApp.Application.Interfaces.Services;
using DiaryApp.Domain.Entities;

namespace DiaryApp.Application.Services;

public class DailyLogService(
    IDailyLogRepository logRepository,
    IUserRepository userRepository,
    IActivityRepository activityRepository
) : IDailyLogService
{
    private readonly IDailyLogRepository _logRepository = logRepository;
    private readonly IUserRepository _userRepository = userRepository;
    private readonly IActivityRepository _activityRepository = activityRepository;

    public async Task UpsertLogAsync(string userId, DailyLogRequestDto request)
    {
        await EnsureUserExistsAsync(userId);

        if (request.BaseMoodId < 1 || request.BaseMoodId > 5)
        {
            throw new ArgumentException("Mã cảm xúc (baseMoodId) không hợp lệ. Phải từ 1 đến 5.");
        }

        if (request.ActivityIds != null && request.ActivityIds.Any())
        {
            var exists = await _activityRepository.CheckAllActivitiesExistAsync(request.ActivityIds);
            if (!exists)
            {
                throw new KeyNotFoundException($"Hoạt động không tồn tại.");
            }
        }

        string extractedYearMonth = request.Date.Length >= 7 
            ? request.Date.Substring(0, 7) 
            : DateTime.UtcNow.ToString("yyyy-MM");

        var newLog = new DailyLog
        {
            Id = $"{userId}_{request.Date}",
            BaseMoodId = request.BaseMoodId,
            Date = request.Date,
            YearMonth = extractedYearMonth,
            Note = request.Note,
            SleepHours = request.SleepHours,
            IsMenstruation = request.IsMenstruation,
            MenstruationPhase = request.MenstruationPhase,
            DailyPhotos = request.DailyPhotos ?? new List<string>(),
            CreatedAt = DateTime.UtcNow,
            ActivityIds = request.ActivityIds,
        };

        await _logRepository.UpsertAsync(userId, newLog);
    }

    public async Task<DailyLogResponseDto?> GetLogByDateAsync(string userId, string date)
    {
        await EnsureUserExistsAsync(userId);

        var log = await _logRepository.GetByDateAsync(userId, date);
        if (log == null) return null;
        return MapToResponseDto(log);
    }

    public async Task<IEnumerable<DailyLogResponseDto>> GetLogsByMonthAsync(string userId, string yearMonth)
    {
        await EnsureUserExistsAsync(userId);

        var logs = await _logRepository.GetLogsByMonthAsync(userId, yearMonth);
        return logs.Select(MapToResponseDto);
    }

    public async Task<IEnumerable<DailyLogResponseDto>> GetLogsByActivityAsync(string userId, string activityId, string yearMonth)
    {
        await EnsureUserExistsAsync(userId);

        var logs = await _logRepository.GetLogsByActivityAsync(userId, activityId, yearMonth);
        return logs.Select(MapToResponseDto);
    }

    public async Task<IEnumerable<DailyLogResponseDto>> GetLogsByMoodAsync(string userId, int moodId)
    {
        await EnsureUserExistsAsync(userId);
        var logs = await _logRepository.GetLogsByMoodAsync(userId, moodId);
        return logs.Select(MapToResponseDto);
    }

    public async Task<IEnumerable<DailyLogResponseDto>> GetLogsByMenstruationAsync(string userId, bool isMenstruation)
    {
        await EnsureUserExistsAsync(userId);
        var logs = await _logRepository.GetLogsByMenstruationAsync(userId, isMenstruation);
        return logs.Select(MapToResponseDto);
    }

    public async Task<IEnumerable<DailyLogResponseDto>> SearchByNoteAsync(string userId, string keyword)
    {
        await EnsureUserExistsAsync(userId);
        var logs = await _logRepository.SearchByNoteAsync(userId, keyword);
        return logs.Select(MapToResponseDto);
    }

    public async Task DeleteLogAsync(string userId, string date)
    {
        await EnsureUserExistsAsync(userId);
        await _logRepository.DeleteAsync(userId, date);
    }

    private static DailyLogResponseDto MapToResponseDto(DailyLog log)
    {
        return new DailyLogResponseDto
        {
            Id = log.Id,
            BaseMoodId = log.BaseMoodId,
            Date = log.Date,
            Note = log.Note,
            SleepHours = log.SleepHours,
            IsMenstruation = log.IsMenstruation,
            MenstruationPhase = log.MenstruationPhase,
            DailyPhotos = log.DailyPhotos ?? new List<string>(),
            ActivityIds = log.ActivityIds ?? new List<string>(),
            CreatedAt = log.CreatedAt
        };
    }

    private async Task EnsureUserExistsAsync(string userId)
    {
        var user = await _userRepository.GetByIdAsync(userId);
        if (user == null)
        {
            throw new KeyNotFoundException($"Không tìm thấy người dùng id: {userId}"); 
        }
    }
}