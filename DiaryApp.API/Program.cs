using DiaryApp.Application.Interfaces;
using DiaryApp.Application.Services;
using DiaryApp.Domain.Configurations;
using DiaryApp.Infrastructure.Repositories;
using DiaryApp.Infrastructure.Data;
using DiaryApp.Infrastructure.Service;

var builder = WebApplication.CreateBuilder(args);

// configure
builder.Services.Configure<JwtSettings>(builder.Configuration.GetSection("JwtSettings"));
builder.Services.Configure<GoogleSettings>(builder.Configuration.GetSection("GoogleSettings"));
builder.Services.Configure<EmailSettings>(builder.Configuration.GetSection("EmailSettings"));

// dependency injection
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// add infrastructure
builder.Services.AddSingleton<FirestoreProvider>();
builder.Services.AddScoped<IUserRepository, UserRepository>();
builder.Services.AddScoped<IEmailService, EmailService>();

// add Application
builder.Services.AddScoped<IAuthService, AuthService>();

var app = builder.Build();

// swagger
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthentication(); 
app.UseAuthorization();

// map controller
app.MapControllers();

app.Run();