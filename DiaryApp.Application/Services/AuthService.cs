using DiaryApp.Application.DTOs.Auth;
using DiaryApp.Application.Interfaces;
using DiaryApp.Domain.Entities;
using DiaryApp.Domain.Configurations;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using BCrypt.Net;
using DiaryApp.Application.DTOs;
using Google.Apis.Auth;
namespace DiaryApp.Application.Services;

public class AuthService(
    IUserRepository userRepository,
    IEmailService emailService,
    IOptions<JwtSettings> jwtSettings,
    IOptions<GoogleSettings> googleSettings
) : IAuthService
{
    private readonly IUserRepository _userRepository = userRepository;
    private readonly IEmailService _emailService = emailService;
    private readonly JwtSettings _jwtSettings = jwtSettings.Value;
    private readonly GoogleSettings _googleSettings = googleSettings.Value;

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

        string token = GenerateJwtToken(newUser);

        return new AuthResponseDto
        {
            Token = token,
            UserId = newUser.Id,
            Name = newUser.Name,
            AvatarUrl = newUser.AvatarUrl
        };
    }

    public async Task<AuthResponseDto> LoginAsync(LoginRequestDto request)
    {
        var email = request.Email.Trim().ToLower();
        var user = await _userRepository.GetByEmailAsync(email);

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

        string token = GenerateJwtToken(user);
        return new AuthResponseDto
        {
            Token = token,
            UserId = user.Id,
            Name = user.Name ?? "username",
            AvatarUrl = user.AvatarUrl
        };
    }

    public async Task<AuthResponseDto> LoginWithGoogleAsync(GoogleLoginRequestDto request)
    {
        try
        {
            var validationSettings = new GoogleJsonWebSignature.ValidationSettings
            {
                Audience = new List<string> { _googleSettings.ClientId }
            };

            var payload = await GoogleJsonWebSignature.ValidateAsync(request.IdToken, validationSettings);
            
            return await HandleSocialLogin(payload.Email, payload.Name, payload.Picture);
        } catch (InvalidJwtException)
        {
            throw new UnauthorizedAccessException("Lỗi xác thực Google: Token không hợp lệ.");
        } catch (Exception ex)
        {
            throw new Exception("Đã xảy ra lỗi khi đăng nhập bằng Google: " + ex.Message);
        }
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

            string token = GenerateJwtToken(user);
            return new AuthResponseDto
            {
                Token = token,
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

        string otp = new Random().Next(100000, 999999).ToString();
        user.ResetOtp = otp;
        user.OtpExpiry = DateTime.UtcNow.AddMinutes(5); // limit 5 minute
        await _userRepository.UpdateAsync(user);
        string subject = $"[{otp}] Mã xác nhận khôi phục mật khẩu";
        string emailBody = $@"
            <h2>Yêu cầu khôi phục mật khẩu</h2>
            <p>Mã OTP của bạn là: <b style='font-size: 24px; color: #4CAF50;'>{otp}</b></p>
            <p>Mã này sẽ hết hạn sau 5 phút.</p>";
        await _emailService.SendEmailAsync(user.Email, subject, emailBody);    
    }

    public async Task ResetPasswordAsync(ResetPasswordRequestDto request)
    {
        var email = request.Email.ToLower();
        var user = await _userRepository.GetByEmailAsync(email);

        if (user == null || user.AuthProvider != "Local")
        {
            throw new KeyNotFoundException("Email không tồn tại");
        }
        if (string.IsNullOrEmpty(user.ResetOtp) || user.ResetOtp != request.Otp)
        {
            throw new UnauthorizedAccessException("Mã OTP không chính xác hoặc đã hết hạn.");
        }

        if (user.OtpExpiry < DateTime.UtcNow) throw new Exception("Mã OTP đã hết hạn.");

        user.HashPassword = BCrypt.Net.BCrypt.HashPassword(request.NewPassword);
        user.ResetOtp = null;
        user.OtpExpiry = null;
        await _userRepository.UpdateAsync(user);
    }

    private string GenerateJwtToken(User user)
    {
        var claims = new[]
        {
            new Claim(JwtRegisteredClaimNames.Sub, user.Id),
            new Claim(JwtRegisteredClaimNames.Email, user.Email),
            new Claim(JwtRegisteredClaimNames.Name, user.Name ?? ""),
            new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),
            new Claim(ClaimTypes.Role, user.Role.ToString()),
        };

        var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_jwtSettings.Secret));
        var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);
        
        var claimsIdentity = new ClaimsIdentity(claims, "Jwt", JwtRegisteredClaimNames.Name, "role");

        var tokenDescriptor = new SecurityTokenDescriptor
        {
            Subject = claimsIdentity,
            Expires = DateTime.UtcNow.AddMinutes(_jwtSettings.ExpiryMinutes),
            Issuer = _jwtSettings.Issuer,
            Audience = _jwtSettings.Audience,
            SigningCredentials = creds
        };

        var tokenHandler = new JwtSecurityTokenHandler();
        var token = tokenHandler.CreateToken(tokenDescriptor);
        
        return tokenHandler.WriteToken(token);
    }
}