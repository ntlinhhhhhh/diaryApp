using System.ComponentModel.DataAnnotations;

namespace DiaryApp.Application.DTOs.Activity;

public class ActivityRequestDto
{
    [Required(ErrorMessage = "Tên hoạt động không được để trống")]
    public string Name { get; set; } = null!;

    [Required(ErrorMessage = "IconUrl không được để trống")]
    public string IconUrl { get; set; } = null!;

    public string? Category { get; set; }
}