using System;
using System.Collections.Generic;

namespace DiaryApp.Domain.Entities;

public class User
{
    public required string Id { get; set; }
    public required string Name { get; set; }
    public required string Email { get; set; }
    public string? Gender { get; set; }
    public string? Birthday { get; set; }
    public string AvatarUrl { get; set; } = "avatar_default.png";
    public int CoinBalance { get; set; }
    public string ActiveThemeId { get; set; } = "theme_default_id";
    public List<string> OwnedThemeIds { get; set; } = new List<string>(); // Collection theme
    public DateTime CreatedAt { get; set; }
    public DateTime UpdatedAt { get; set; }
}