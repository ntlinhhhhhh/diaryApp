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
    private string CurrentUserId => User.FindFirstValue(ClaimTypes.NameIdentifier)!;

    [HttpGet("me")]
    public async Task<IActionResult> GetMyProfile()
    {
        try
        {
            var profile = await _userService.GetProfileAsync(CurrentUserId);
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
            await _userService.UpdateProfileAsync(CurrentUserId, request);
            return Ok(new { message = "Cập nhật hồ sơ thành công!" });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    [HttpGet("Search")]
    public async Task<IActionResult> SearchUsers([FromQuery] string name, [FromQuery] int limit)
    {
        try
        {
            var users = await _userService.SearchUsersAsync(name, limit);
            return Ok(users);
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    [HttpGet("me/themes")]
    public async Task<IActionResult> GetMyThemes()
    {
        try
        {
            var themes = await _userService.GetMyThemeIdsAsync(CurrentUserId);
            return Ok(themes);
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    [HttpPost("me/theme/buy")]
    public async Task<IActionResult> BuyTheme([FromBody] BuyThemeRequestDto request)
    {
        try
        {
            await _userService.BuyThemeAsync(CurrentUserId, request);
            return Ok(new { message = "Mua giao diện thành công" });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    [HttpPut("me/themes/active")]
    public async Task<IActionResult> ChangeTheme([FromBody] UpdateThemeRequestDto request)
    {
        try
        {
            await _userService.ChangeActiveThemeAsync(CurrentUserId, request);
            return Ok(new { message = "Thay đổi giao diện thành công!" });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }
}