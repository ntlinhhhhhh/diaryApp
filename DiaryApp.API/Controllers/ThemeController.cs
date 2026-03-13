using System.Security.Claims;
using DiaryApp.Application.DTOs.Theme;
using DiaryApp.Application.Interfaces.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace DiaryApp.API.Controllers;

[Authorize]
[Route("api/themes")]
[ApiController]
public class ThemeController : ControllerBase
{
    private readonly IThemeService _themeService;

    public ThemeController(IThemeService themeService)
    {
        _themeService = themeService;
    }

    // GET: /api/themes
    [HttpGet]
    public async Task<IActionResult> GetAllActiveThemes()
    {
        try
        {
            var themes = await _themeService.GetAllActiveThemesAsync();
            return Ok(themes);
        }
        catch (Exception ex)
        {
            return StatusCode(500, new { message = "Lỗi máy chủ: " + ex.Message });
        }
    }

    // GET: /api/themes/{id}
    [HttpGet("{id}")]
    public async Task<IActionResult> GetThemeById(string id)
    {
        try
        {
            var theme = await _themeService.GetThemeByIdAsync(id);
            
            if (theme == null) 
            {
                return NotFound(new { message = "Giao diện không tồn tại hoặc đã ngừng bán." });
            }

            return Ok(theme);
        }
        catch (Exception ex)
        {
            return StatusCode(500, new { message = "Lỗi máy chủ: " + ex.Message });
        }
    }

    // GET: /api/themes/{id}/moods
    [HttpGet("{id}/moods")]
    public async Task<IActionResult> GetThemeMoods(string id)
    {
        try
        {
            var moods = await _themeService.GetThemeMoodsAsync(id);
            
            if (!moods.Any()) return NotFound(new { message = "Không tìm thấy icon cho giao diện này." });

            return Ok(moods);
        }
        catch (Exception ex)
        {
            return StatusCode(500, new { message = "Lỗi: " + ex.Message });
        }
    }
    // POST: /api/themes
    [HttpPost]
    [Authorize(Roles = "Admin")]
    public async Task<IActionResult> CreateTheme([FromBody] CreateThemeRequestDto request)
    {
        if (!ModelState.IsValid) return BadRequest(ModelState); 

        try
        {
            await _themeService.CreateThemeAsync(request);
            
            return CreatedAtAction(nameof(GetThemeById), new { id = request.Id }, new { message = "Tạo giao diện thành công!" });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    // PUT: /api/themes/{id}
    [HttpPut("{id}")]
    [Authorize(Roles = "Admin")]
    public async Task<IActionResult> UpdateTheme(string id, [FromBody] CreateThemeRequestDto request)
    {
        if (!ModelState.IsValid) return BadRequest(ModelState);

        try
        {
            await _themeService.UpdateThemeAsync(id, request);
            return Ok(new { message = "Cập nhật giao diện thành công!" });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    // DELETE: /api/themes/{id}
    [HttpPost("id")]
    [Authorize(Roles = "Admin")]
    public async Task<IActionResult> DeleteTheme(string id)
    {
        try
        {
            await _themeService.DeleteThemeAsync(id);
            return Ok(new {message = "Đã xóa giao diện thành công"});
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }
}
