using DiaryApp.Application.Interfaces;
using DiaryApp.Domain.Entities;
using DiaryApp.Infrastructure.Data;
using Google.Cloud.Firestore;

namespace DiaryApp.Infrastructure.Repositories;

public class MomentRepository : IMomentRepository
{
    private readonly FirestoreDb _db;
    private readonly CollectionReference _momentCollection;

    public MomentRepository(FirestoreProvider provider)
    {
        _db = provider.Database;
        _momentCollection = _db.Collection("moments");
    }

    Task IMomentRepository.CreateAsync(Moment moment)
    {
        throw new NotImplementedException();
    }

    Task IMomentRepository.DeleteAsync(string momentId)
    {
        throw new NotImplementedException();
    }

    Task<Moment?> IMomentRepository.GetByIdAsync(string id)
    {
        throw new NotImplementedException();
    }

    Task<IEnumerable<Moment>> IMomentRepository.GetMomentsByUserIdAsync(string userId)
    {
        throw new NotImplementedException();
    }

    Task IMomentRepository.SyncUserMediaInMomentsAsync(string userId, string newName, string newAvatarUrl)
    {
        throw new NotImplementedException();
    }
}