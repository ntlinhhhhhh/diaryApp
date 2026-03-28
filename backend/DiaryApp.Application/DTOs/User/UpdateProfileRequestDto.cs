using System.ComponentModel.DataAnnotations;

namespace DiaryApp.Application.DTOs.User;

public class UpdateProfileRequestDto
{
    [Required(ErrorMessage = "Tên không được để trống")]
    public required string Name { get; set; }
    public string? AvatarUrl { get; set; }
    public string? Gender { get; set; }
    public string? Birthday { get; set; }
}