using System.ComponentModel.DataAnnotations;

namespace DiaryApp.Application.DTOs.Auth;

public class VerifyOtpDto
{
    [Required(ErrorMessage = "Email không được để trống.")]
    [EmailAddress(ErrorMessage = "Định dạng Email không hợp lệ.")]
    public required string Email { get; set; }

    [Required(ErrorMessage = "Otp không được để trống.")]
    public required string OtpCode { get; set; }
}