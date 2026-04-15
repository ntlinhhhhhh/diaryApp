using DiaryApp.Application.DTOs.Auth;
using DiaryApp.Application.Interfaces;
using DiaryApp.Domain.Entities;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using BCrypt.Net;
using DiaryApp.Application.DTOs;
using Google.Apis.Auth;
using DiaryApp.Application.Interfaces.Services;
namespace DiaryApp.Application.Services;

public class AuthService(
    IUserRepository userRepository,
    IEmailService emailService,
    IJwtProvider jwtProvider,
    IGoogleAuthProvider googleAuthProvider,
    IRedisCacheService cacheService
) : IAuthService
{
    private readonly IUserRepository _userRepository = userRepository;
    private readonly IEmailService _emailService = emailService;
    private readonly IJwtProvider _jwtProvider = jwtProvider;
    private readonly IGoogleAuthProvider _googleAuthProvider = googleAuthProvider;
    private readonly IRedisCacheService _cacheService = cacheService;

    private string GetOtpKey(string email) 
        => $"auth:otp:{email.ToLower().Trim()}";

    private string GetResetTokenKey(string email) 
        => $"auth:reset_token:{email.ToLower().Trim()}";

    public async Task<AuthResponseDto> RegisterAsync(RegisterRequestDto request)
    {
        var email = request.Email.Trim().ToLower();
        var name = request.Name.Trim();

        if (string.IsNullOrWhiteSpace(name))
        {
            throw new ArgumentNullException("Tên người dùng không được để trống.");
        } 

        bool userExists = await _userRepository.ExistsByEmailAsync(request.Email);
        if (userExists)
        {
            throw new InvalidOperationException("Email này đã được sử dụng. Vui lòng sử dụng email khác!");
        }

        string hashPassword = BCrypt.Net.BCrypt.HashPassword(request.Password);
        User newUser = new User
        {
            Id = Guid.NewGuid().ToString(),
            Email = email,
            Name = name,
            HashPassword = hashPassword,
            AuthProvider = "Local",
            CreatedAt = DateTime.UtcNow
        };

        await _userRepository.CreateAsync(newUser);

        return new AuthResponseDto
        {
            Token = _jwtProvider.GenerateToken(newUser),
            UserId = newUser.Id,
            Name = newUser.Name,
            AvatarUrl = newUser.AvatarUrl
        };
    }

    public async Task<AuthResponseDto> LoginAsync(LoginRequestDto request)
    {
        var email = request.Email.Trim().ToLower();

        string cacheKey = $"auth:email:{email}";
        var user = await _cacheService.GetAsync<User>(cacheKey);

        if (user == null)
        {
            user = await _userRepository.GetByEmailAsync(email);
            
            if (user != null)
            {
                await _cacheService.SetAsync(cacheKey, user, TimeSpan.FromMinutes(15));
            }
        }

        if (user == null) throw new UnauthorizedAccessException("Email hoặc mật khẩu không chính xác.");

        if (user.AuthProvider != "Local") 
        {
            throw new Exception("Tài khoản này được đăng ký qua Google. Vui lòng đăng nhập bằng Google.");
        }

        bool isPasswordValid = BCrypt.Net.BCrypt.Verify(request.Password, user.HashPassword);
        if (!isPasswordValid)
        {
            throw new UnauthorizedAccessException("Email hoặc mật khẩu không chính xác.");
        }

        return new AuthResponseDto
        {
            Token = _jwtProvider.GenerateToken(user),
            UserId = user.Id,
            Name = user.Name ?? "username",
            AvatarUrl = user.AvatarUrl
        };
    }

    public async Task<AuthResponseDto> LoginWithGoogleAsync(GoogleLoginRequestDto request)
    {
        var payload = await _googleAuthProvider.ValidateTokenAsync(request.IdToken);
        return await HandleSocialLogin(payload.Email, payload.Name, payload.Picture);
    }

    private async Task<AuthResponseDto> HandleSocialLogin(string email, string name, string picture)
    {
        var user = await _userRepository.GetByEmailAsync(email);

        if (user == null)
        {
            user = new User
            {
                Id = Guid.NewGuid().ToString(),
                Email = email,
                Name = name,
                HashPassword = "",
                AvatarUrl = picture,
                AuthProvider = "Google",
                CreatedAt = DateTime.UtcNow
            };
            await _userRepository.CreateAsync(user);
        }

        return new AuthResponseDto
        {
            Token = _jwtProvider.GenerateToken(user),
            UserId = user.Id,
            Name = user.Name ?? "username",
            AvatarUrl = user.AvatarUrl
        };
    }

    public async Task ForgotPasswordAsync(ForgotPasswordRequestDto request)
    {
        var email = request.Email.Trim().ToLower();
        var user = await _userRepository.GetByEmailAsync(email);

        if (user == null || user.AuthProvider != "Local") throw new KeyNotFoundException("Email không tồn tại");

        string otp = Random.Shared.Next(100000, 999999).ToString();
        user.ResetOtp = otp;
        string otpCacheKey = $"auth:otp:{email}";
        await _cacheService.SetAsync(otpCacheKey, otp, TimeSpan.FromMinutes(10));

        string subject = $"[{otp}] Mã xác nhận khôi phục mật khẩu";
        string emailBody = $@"
            <h2>Yêu cầu khôi phục mật khẩu</h2>
            <p>Mã OTP của bạn là: <b style='font-size: 24px; color: #4CAF50;'>{otp}</b></p>
            <p>Mã này sẽ hết hạn sau 10 phút.</p>";

        _ = Task.Run(async () => 
        {
            try 
            {
                await _emailService.SendEmailAsync(user.Email, subject, emailBody);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Lỗi gửi email: {ex.Message}");
            }
        }); 
    }

    public async Task<string> VerifyOtpAndGenerateTokenAsync(VerifyOtpDto request)
    {
        var otpCacheKey = GetOtpKey(request.Email);
        var savedOtp = await _cacheService.GetAsync<string>(otpCacheKey);

        if (string.IsNullOrEmpty(savedOtp) || savedOtp != request.OtpCode)
        {
            throw new UnauthorizedAccessException("Mã OTP không chính xác hoặc đã hết hạn.");
        }

        await _cacheService.RemoveAsync(otpCacheKey);

        var resetToken = Guid.NewGuid().ToString("N");
        var tokenCacheKey = GetResetTokenKey(request.Email);
        
        await _cacheService.SetAsync(tokenCacheKey, resetToken, TimeSpan.FromMinutes(10));

        return resetToken;
    }

    public async Task ResetPasswordAsync(ResetPasswordRequestDto request)
    {
        var tokenCacheKey = GetResetTokenKey(request.Email);
        var savedToken = await _cacheService.GetAsync<string>(tokenCacheKey);

        if (string.IsNullOrEmpty(savedToken) || savedToken != request.ResetToken)
        {
            throw new UnauthorizedAccessException("Phiên làm việc không hợp lệ hoặc đã hết hạn. Vui lòng thử lại.");
        }

        var email = request.Email.Trim().ToLower();
        var user = await _userRepository.GetByEmailAsync(email);

        if (user == null || user.AuthProvider != "Local")
        {
            throw new KeyNotFoundException("Email không tồn tại trong hệ thống.");
        }

        user.HashPassword = BCrypt.Net.BCrypt.HashPassword(request.NewPassword);
        await _userRepository.UpdateAsync(user);

        await _cacheService.RemoveAsync(tokenCacheKey);
        await _cacheService.RemoveAsync($"auth:email:{email}");
    }
}