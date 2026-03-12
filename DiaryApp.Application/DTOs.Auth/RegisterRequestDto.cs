using System.ComponentModel.DataAnnotations;

namespace DiaryApp.Application.DTOs.Auth;

public class RegisterRequestDto
{
    [Required(ErrorMessage = "Email là bắt buộc")]
    public required string Email { get; set; }
    
    [Required(ErrorMessage = "Password là bắt buộc")]
    public required string Password { get; set; }
    public required string Name { get; set; }
}