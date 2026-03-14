# Giai đoạn 1: Build code
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copy TOÀN BỘ thư mục code (bao gồm cả API, Domain, App, Infra) vào Docker
COPY . .

# Chỉ định rõ cho Docker biết project chính nằm ở đâu để Restore và Publish
# (Lưu ý: Thay chữ 'diaryapp.api' cho đúng với tên thư mục thật của bạn)
RUN dotnet restore "diaryapp.api/diaryapp.api.csproj"
RUN dotnet publish "diaryapp.api/diaryapp.api.csproj" -c Release -o /app/publish

# Giai đoạn 2: Chạy app
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS final
WORKDIR /app
EXPOSE 8080
ENV ASPNETCORE_URLS=http://+:8080

COPY --from=build /app/publish .

# Chạy file dll được sinh ra (Tên file dll thường giống tên project API)
ENTRYPOINT ["dotnet", "diaryapp.api.dll"]