using DiaryApp.Application.Interfaces;
using MailKit.Net.Smtp;
using MimeKit;

namespace DiaryApp.Infrastructure.Service;

public class EmailService : IEmailService
{
    async Task IEmailService.SendEmailAsync(string to, string subject, string body)
    {
        throw new NotImplementedException();
    }
}