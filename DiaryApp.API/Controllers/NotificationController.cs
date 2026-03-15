using Microsoft.AspNetCore.Mvc;
using DiaryApp.Application.Interfaces;
using DiaryApp.Application.DTOs.Notification;

namespace DiaryApp.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class NotificationController : ControllerBase
{
    private readonly IFirebaseNotificationService _notificationService;

    public NotificationController(IFirebaseNotificationService notificationService)
    {
        _notificationService = notificationService;
    }

    [HttpPost("send")]
    public async Task<IActionResult> SendPushNotification([FromBody] PushRequestDto request)
    {
        try
        {
            var messageId = await _notificationService.SendPushNotificationAsync(
                request.Token, 
                request.Title, 
                request.Body);
                
            return Ok(new { Success = true, MessageId = messageId });
        }
        catch (Exception ex)
        {
            return StatusCode(500, new { Success = false, Error = ex.Message });
        }
    }
}