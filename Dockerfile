# Giai đoạn 1: Build code
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copy file csproj và restore thư viện
COPY ["DiaryApp.csproj", "./"]
RUN dotnet restore "DiaryApp.csproj"

# Copy toàn bộ code và build bản Release
COPY . .
RUN dotnet publish "DiaryApp.csproj" -c Release -o /app/publish

# Giai đoạn 2: Chạy app
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS final
WORKDIR /app
# Render sử dụng cổng 8080 làm mặc định cho HTTP
EXPOSE 8080
ENV ASPNETCORE_URLS=http://+:8080

COPY --from=build /app/publish .
ENTRYPOINT ["dotnet", "DiaryApp.dll"]