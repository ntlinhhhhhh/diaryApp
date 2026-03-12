using System.Security.Claims;
using DiaryApp.Application.DTOs.User;
using DiaryApp.Application.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace DiaryApp.API.Controllers;

[Authorize]
[ApiController]
[Route("api/[controller]")]
public class UserController(IUserService userService) : ControllerBase
{
    private readonly IUserService _userService = userService;

    [HttpGet("me")]
    public async Task<IActionResult> GetMyProfile()
    {
        try
        {
            string userId = User.FindFirstValue(ClaimTypes.NameIdentifier)!;
            
            var profile = await _userService.GetProfileAsync(userId);
            return Ok(profile);
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    [HttpPut("me")]
    public async Task<IActionResult> UpdateMyProfile([FromBody] UpdateProfileRequestDto request)
    {
        try
        {
            string userId = User.FindFirstValue(ClaimTypes.NameIdentifier)!;
            
            await _userService.UpdateProfileAsync(userId, request);
            return Ok(new { message = "Cập nhật hồ sơ thành công!" });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }
}