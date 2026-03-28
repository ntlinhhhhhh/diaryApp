using System.Security.Claims;
using DiaryApp.Application.DTOs.Moment;
using DiaryApp.Application.Interfaces.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace DiaryApp.API.Controllers;

[Authorize]
[ApiController]
[Route("api/moments")]
public class MomentController(IMomentService momentService) : ControllerBase
{
    private readonly IMomentService _momentService = momentService;

    private string CurrentUserId => User.FindFirstValue(ClaimTypes.NameIdentifier)!;

    // POST: api/moments
    [HttpPost]
    public async Task<IActionResult> CreateMoment([FromBody] MomentRequestDto request)
    {
        try
        {
            var response = await _momentService.CreateMomentAsync(CurrentUserId, request);
            
            return CreatedAtAction(nameof(GetMomentById), new { id = response.Id }, response);
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    // GET: api/moments/{id}
    [HttpGet("{id}")]
    public async Task<IActionResult> GetMomentById(string id)
    {
        try
        {
            var moment = await _momentService.GetByIdAsync(id);
            if (moment == null) return NotFound(new { message = "Không tìm thấy khoảnh khắc này." });
            
            return Ok(moment);
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    // GET: api/moments/me
    [HttpGet("me")]
    public async Task<IActionResult> GetMyMoments()
    {
        try
        {
            var moments = await _momentService.GetMomentsByUserIdAsync(CurrentUserId);
            return Ok(moments);
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    // GET: api/moments/user/{userId}
    [HttpGet("user/{userId}")]
    public async Task<IActionResult> GetMomentsByUser(string userId)
    {
        try
        {
            var moments = await _momentService.GetMomentsByUserIdAsync(userId);
            return Ok(moments);
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    // DELETE: api/moments/{id}
    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteMoment(string id)
    {
        try
        {
            await _momentService.DeleteAsync(CurrentUserId, id);
            return Ok(new { message = "Đã xóa khoảnh khắc thành công." });
        }
        catch (UnauthorizedAccessException ex)
        {
            return StatusCode(StatusCodes.Status403Forbidden, new { message = ex.Message });
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }
}