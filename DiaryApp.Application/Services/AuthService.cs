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
namespace DiaryApp.Application.Services;

public class AuthService : IAuthService
{
    private readonly IUserRepository _userRepository;
    private readonly JwtSettings _jwtSettings;

    // Inject thêm IOptions<JwtSettings>
    public AuthService(IUserRepository userRepository, IOptions<JwtSettings> jwtSettings)
    {
        _userRepository = userRepository;
        _jwtSettings = jwtSettings.Value;
    }
    public async Task<AuthResponseDto> RegisterAsync(RegisterRequestDto request)
    {
        bool userExists = await _userRepository.ExistsByEmailAsync(request.Email);
        if (userExists) throw new Exception("Email này đã được sử dụng. Vui lòng sử dụng email khác!");

        string hashPassword = BCrypt.Net.BCrypt.HashPassword(request.Password);
        User newUser = new User
        {
            Id = Guid.NewGuid().ToString(),
            Email = request.Email,
            Name = request.Name,
            HashPassword = hashPassword,
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
        var user = await _userRepository.GetByEmailAsync(request.Email);

        if (user == null) throw new Exception("Email hoặc mật khẩu không chính xác.");

        bool isPasswordValid = BCrypt.Net.BCrypt.Verify(request.Password, user.HashPassword);
        if (!isPasswordValid)
        {
            throw new Exception("Email hoặc mật khẩu không chính xác.");
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

    private string GenerateJwtToken(User user)
    {
        var claims = new []
        {
            new Claim(JwtRegisteredClaimNames.Sub, user.Id),
            new Claim(JwtRegisteredClaimNames.Email, user.Email),
            new Claim(JwtRegisteredClaimNames.Name, user.Name ?? ""),
            new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),
        };

        // Get the secret key
        var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_jwtSettings.Secret));

        // hashing algorithm
        var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

        // Create the token descriptor
        var tokenDescriptor = new SecurityTokenDescriptor
        {
            Subject = new ClaimsIdentity(claims),
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