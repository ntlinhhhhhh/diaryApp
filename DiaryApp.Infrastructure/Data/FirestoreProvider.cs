using Google.Cloud.Firestore;
using Microsoft.Extensions.Configuration;

namespace DiaryApp.Infrastructure.Data;

public class FirestoreProvider
{
    public FirestoreDb Database { get; }

        public FirestoreProvider(IConfiguration configuration)
        {
            // path from appsettings.json
            string filepath = configuration["Firebase:ServiceAccountPath"] 
                              ?? throw new ArgumentNullException("Firebase path is missing");
            
            // projectId from appsettings.json
            string projectId = configuration["Firebase:ProjectId"] 
                               ?? throw new ArgumentNullException("Project ID is missing");

            // env valuable
            Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", filepath);

            Database = FirestoreDb.Create(projectId);
        }
}