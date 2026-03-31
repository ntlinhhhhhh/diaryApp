using DiaryApp.Application.Interfaces;
using DiaryApp.Application.Services;
using DiaryApp.Infrastructure.Repositories;
using DiaryApp.Infrastructure.Data;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using System.Text;
using DiaryApp.Infrastructure.Services;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using DiaryApp.Application.Interfaces.Services;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using DiaryApp.Infrastructure.Configurations;
using DiaryApp.Api.Extensions;
using DiaryApp.Infrastructure.Providers;
using Google.Cloud.Firestore;
using DiaryApp.Infrastructure.Messaging;
using DiaryApp.Infrastructure.Workers;

JwtSecurityTokenHandler.DefaultInboundClaimTypeMap.Clear();

var builder = WebApplication.CreateBuilder(args);
builder.Logging.ClearProviders();
builder.Logging.AddConsole();

// CONFIGURATIONS
builder.Services.Configure<JwtSettings>(builder.Configuration.GetSection("JwtSettings"));
builder.Services.Configure<GoogleSettings>(builder.Configuration.GetSection("GoogleSettings"));
builder.Services.Configure<EmailSettings>(builder.Configuration.GetSection("EmailSettings"));
builder.Services.Configure<CloudinarySettings>(builder.Configuration.GetSection("CloudinarySettings"));
builder.Services.Configure<RabbitMQSettings>(builder.Configuration.GetSection("RabbitMQSettings"));

// builder.Services.AddFirebaseAdminConfig(builder.Configuration);

var jwtSettings = builder.Configuration.GetSection("JwtSettings");
var secretKey = Encoding.UTF8.GetBytes(jwtSettings["Secret"]!);
var redisConnectionString = builder.Configuration["Redis:ConnectionString"];

// AUTHENTICATION & AUTHORIZATION
builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = true,
        ValidateAudience = true,
        ValidateLifetime = true,
        ValidateIssuerSigningKey = true,
        ValidIssuer = jwtSettings["Issuer"],
        ValidAudience = jwtSettings["Audience"],
        IssuerSigningKey = new SymmetricSecurityKey(secretKey),
        RoleClaimType = ClaimTypes.Role 
    };
});

builder.Services.AddAuthorization(options =>
{
    options.AddPolicy("AdminOnly", policy => policy.RequireRole("Admin"));
});

string firebaseBase64 = Environment.GetEnvironmentVariable("FIREBASE_KEY_BASE64");
string projectId = builder.Configuration["Firebase:ProjectId"] ?? "diaryapp-36c8f";

if (!string.IsNullOrEmpty(firebaseBase64))
{
    byte[] decodedBytes = Convert.FromBase64String(firebaseBase64);
    string decodedJson = System.Text.Encoding.UTF8.GetString(decodedBytes);

    FirebaseAdmin.FirebaseApp.Create(new FirebaseAdmin.AppOptions()
    {
        Credential = Google.Apis.Auth.OAuth2.GoogleCredential.FromJson(decodedJson)
    });

    builder.Services.AddSingleton<FirestoreDb>(provider =>
    {
        return new FirestoreDbBuilder { ProjectId = projectId, JsonCredentials = decodedJson }.Build();
    });
}
else
{
    string serviceAccountPath = builder.Configuration["Firebase:ServiceAccountPath"];
    
    if (!string.IsNullOrEmpty(serviceAccountPath))
    {
        Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", serviceAccountPath);
        
        FirebaseAdmin.FirebaseApp.Create(new FirebaseAdmin.AppOptions()
        {
            Credential = Google.Apis.Auth.OAuth2.GoogleCredential.FromFile(serviceAccountPath)
        });

        builder.Services.AddSingleton<FirestoreDb>(provider =>
        {
            return FirestoreDb.Create(projectId);
        });
    }
    else
    {
        throw new Exception("Lỗi: Không tìm thấy chìa khóa Firebase! Hãy kiểm tra lại Base64 hoặc ServiceAccountPath.");
    }
}

builder.Services.AddStackExchangeRedisCache(options =>
{
    options.Configuration = redisConnectionString;
    options.InstanceName = "DiaryApp_";
});

// DEPENDENCY INJECTION
// Infrastructure
builder.Services.AddSingleton<FirestoreProvider>();
// builder.Services.AddScoped<ActivitySeeder>();
builder.Services.AddScoped<IUserRepository, UserRepository>();
builder.Services.AddScoped<IEmailService, EmailService>();
builder.Services.AddScoped<IThemeRepository, ThemeRepository>();
builder.Services.AddScoped<IActivityRepository, ActivityRepository>();
builder.Services.AddScoped<IDailyLogRepository, DailyLogRepository>();
builder.Services.AddScoped<IMomentRepository, MomentRepository>();
builder.Services.AddScoped<IAppNotificationRepository, AppNotificationRepository>();
builder.Services.AddScoped<IRedisCacheService, RedisCacheService>();
builder.Services.AddScoped<IMessageProducer, RabbitMQProducer>();
builder.Services.AddHostedService<ImageUploadWorker>();
// Application
builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddScoped<IUserService, UserService>();
builder.Services.AddScoped<IThemeService, ThemeService>();
builder.Services.AddScoped<IActivityService, ActivityService>();
builder.Services.AddScoped<IDailyLogService, DailyLogService>();
builder.Services.AddScoped<IMomentService, MomentService>();
builder.Services.AddScoped<IJwtProvider, JwtProvider>();
builder.Services.AddScoped<IGoogleAuthProvider, GoogleAuthProvider>();
builder.Services.AddScoped<IFirebaseNotificationService, FirebaseNotificationService>();
builder.Services.AddScoped<IAppNotificationService, AppNotificationService>();
builder.Services.AddScoped<ICloudinaryService, CloudinaryService>();

// CONTROLLERS & SWAGGER 
builder.Services.AddControllers()
    .ConfigureApiBehaviorOptions(options =>
        {
            options.SuppressMapClientErrors = false; 
        });
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Description = "Bearer {your_token}",
        Name = "Authorization",
        In = ParameterLocation.Header,
        Type = SecuritySchemeType.ApiKey,
        Scheme = "Bearer"
    });

    c.AddSecurityRequirement(new OpenApiSecurityRequirement()
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference { Type = ReferenceType.SecurityScheme, Id = "Bearer" },
                Scheme = "oauth2",
                Name = "Bearer",
                In = ParameterLocation.Header,
            },
            new List<string>()
        }
    });
});

var app = builder.Build();
app.UseDeveloperExceptionPage();


// MIDDLEWARE PIPELINE
app.UseSwagger();
app.UseSwaggerUI();

// app.UseHttpsRedirection();

app.UseAuthentication(); 
app.UseAuthorization();

app.MapControllers();
// using (var scope = app.Services.CreateScope())
// {
//     var db = scope.ServiceProvider.GetRequiredService<FirestoreDb>();
//     Console.WriteLine("Đang khởi tạo kết nối Firestore...");
//     await db.Collection("themes").Limit(1).GetSnapshotAsync(); 
//     Console.WriteLine("Firestore đã sẵn sàng!");
// }

app.MapGet("/", () => Results.Ok("I am alive!"));
app.Run();