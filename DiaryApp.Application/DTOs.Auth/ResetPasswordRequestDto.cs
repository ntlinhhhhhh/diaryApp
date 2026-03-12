using System.ComponentModel.DataAnnotations;

namespace DiaryApp.Application.DTOs.Auth;

public class ResetPasswordRequestDto
{
    [Required(ErrorMessage = "Email không được để trống.")]
    [EmailAddress(ErrorMessage = "Định dạng Email không hợp lệ.")]
    public required string Email { get; set; }

    [Required(ErrorMessage = "OTP không được để trống.")]
    public required string Otp { get; set; }
    
    [Required(ErrorMessage = "Mật khẩu không được để trống.")]
    [MinLength(6, ErrorMessage = "Mật khẩu phải có ít nhất 6 ký tự.")]
    public required string NewPassword { get; set; }
}