using System.ComponentModel.DataAnnotations;

namespace DiaryApp.Application.DTOs.Auth;

public class RegisterRequestDto
{
    [Required(ErrorMessage = "Email không được để trống.")]
    [EmailAddress(ErrorMessage = "Định dạng Email không hợp lệ.")]
    public required string Email { get; set; }

    [Required(ErrorMessage = "Mật khẩu không được để trống.")]
    [MinLength(6, ErrorMessage = "Mật khẩu phải có ít nhất 6 ký tự.")]
    public required string Password { get; set; }

    [Required(ErrorMessage = "Tên không được để trống.")]
    [StringLength(50, MinimumLength = 4, ErrorMessage = "Tên phải từ 4 đến 50 ký tự.")]
    public required string Name { get; set; }
}