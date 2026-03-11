using DiaryApp.Domain.Entities;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace DiaryApp.Application.Interfaces;
public interface IActivityRepository
{
    Task<IEnumerable<Activity>> GetAllAsync();
    Task<IEnumerable<Activity>> GetByCategoryAsync(string category);

    // get a activity by id
    Task<Activity?> GetByIdAsync(string id);
}