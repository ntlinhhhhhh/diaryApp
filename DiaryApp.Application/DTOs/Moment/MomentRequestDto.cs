namespace DiaryApp.Application.DTOs.Moment;

public class MomentRequestDto
{
    public required string DailyLogId { get; set; }
    public required string ImageUrl { get; set; }
    public string? Caption { get; set; }
    public bool IsPublic { get; set; }
    public DateTime CapturedAt { get; set; } = DateTime.UtcNow;
}