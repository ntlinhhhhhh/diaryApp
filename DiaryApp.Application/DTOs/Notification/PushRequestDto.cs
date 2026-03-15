namespace DiaryApp.Application.DTOs.Notification;    
public class PushRequestDto
{
    public string Token { get; set; } = string.Empty;
    public string Title { get; set; } = string.Empty;
    public string Body { get; set; } = string.Empty;
}
