using DiaryApp.Application.DTOs.Moment;
using DiaryApp.Application.Interfaces;
using DiaryApp.Application.Interfaces.Services;
using DiaryApp.Domain.Entities;

namespace DiaryApp.Application.Services;

public class MomentService(
    IMomentRepository momentRepository, 
    IUserRepository userRepository) : IMomentService
{
    private readonly IMomentRepository _momentRepository = momentRepository;
    private readonly IUserRepository _userRepository = userRepository;

    public async Task<MomentResponseDto> CreateMomentAsync(string userId, MomentRequestDto request)
    {
        var user = await _userRepository.GetByIdAsync(userId);
        if (user == null)
            throw new KeyNotFoundException("Không tìm thấy người dùng.");

        var newMoment = new Moment
        {
            Id = Guid.NewGuid().ToString(),
            UserId = userId,
            UserName = user.Name ?? "username",
            UserAvatarUrl = user.AvatarUrl ?? "",
            DailyLogId = request.DailyLogId,
            ImageUrl = request.ImageUrl,
            Caption = request.Caption,
            IsPublic = request.IsPublic,
            CapturedAt = request.CapturedAt.ToUniversalTime()
        };

        await _momentRepository.CreateAsync(newMoment);

        return MapToResponseDto(newMoment);
    }

    public async Task<MomentResponseDto?> GetByIdAsync(string momentId)
    {
        var moment = await _momentRepository.GetByIdAsync(momentId);
        return moment == null ? null : MapToResponseDto(moment);
    }

    public async Task<IEnumerable<MomentResponseDto>> GetMomentsByUserIdAsync(string userId)
    {
        var moments = await _momentRepository.GetMomentsByUserIdAsync(userId);
        return moments.Select(MapToResponseDto);
    }

    public async Task DeleteAsync(string userId, string momentId)
    {
        var moment = await _momentRepository.GetByIdAsync(momentId);
        if (moment == null) throw new KeyNotFoundException("Không tìm thấy khoảnh khắc này.");
        if (moment.UserId != userId) 
        {
            throw new UnauthorizedAccessException("Bạn không có quyền xóa khoảnh khắc của người khác.");
        }

        await _momentRepository.DeleteAsync(momentId);
    }

    private static MomentResponseDto MapToResponseDto(Moment moment)
    {
        return new MomentResponseDto
        {
            Id = moment.Id ?? string.Empty,
            UserId = moment.UserId,
            UserName = moment.UserName,
            UserAvatarUrl = moment.UserAvatarUrl,
            DailyLogId = moment.DailyLogId,
            ImageUrl = moment.ImageUrl,
            Caption = moment.Caption,
            IsPublic = moment.IsPublic,
            CapturedAt = moment.CapturedAt
        };
    }
}