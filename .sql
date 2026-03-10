-- ========================================================
-- PHẦN 1: HỆ THỐNG CỬA HÀNG VÀ DANH MỤC (STORE & MASTER DATA)
-- ========================================================

-- 1. Bảng 5 mức độ cảm xúc cốt lõi (Cố định, dùng để thống kê) // cái này là nói về icon mà mình chọn trong bộ 5 icon
-- với 1 bộ icon chọn mood thì sẽ có 5 mức đố 1 2 3 4 5 -- rất tệ tệ bình thường tuyệt rất tuyệt
CREATE TABLE base_moods (
    id INT PRIMARY KEY,     -- Cố định: 1, 2, 3, 4, 5
    level INT NOT NULL,     -- 1: Rất tệ, 3: Bình thường, 5: Rất tuyệt
    default_name VARCHAR(50) 
);

-- 2. Bảng Themes (Các bộ Icon bán trong Store)
CREATE TABLE themes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,       -- Ví dụ: "Mèo ú", "Thời tiết mặc định"
    price INT DEFAULT 0,              -- Giá tiền (ví dụ: xu/coin). 0 là miễn phí
    thumbnail_url TEXT,               -- Ảnh bìa bộ icon hiển thị ngoài Store -- ảnh 
    background_url TEXT,              -- ảnh nền khi cho ui
    is_active BOOLEAN DEFAULT TRUE,   -- Cờ bật/tắt bán bộ theme này -- người dùng có đang chọn thêm này hay không
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Bảng Chi tiết Icon của từng Theme
CREATE TABLE theme_moods (
    id INT PRIMARY KEY AUTO_INCREMENT,
    theme_id INT NOT NULL,
    base_mood_id INT NOT NULL,
    icon_url TEXT NOT NULL,           -- Link ảnh icon lưu trên Cloudinary
    custom_name VARCHAR(50),          -- Tên riêng (VD: Theme Mèo thì tên là "Mèo vui vẻ") -- tên từng con icon biểu cảm -- 1 bộ có 5 con thì mỗi con có 1 tên
    CONSTRAINT fk_tm_theme FOREIGN KEY (theme_id) REFERENCES themes(id) ON DELETE CASCADE,
    CONSTRAINT fk_tm_base FOREIGN KEY (base_mood_id) REFERENCES base_moods(id)
);

-- 4. Bảng Danh mục hoạt động (Activities)
CREATE TABLE activities (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    icon_url TEXT NOT NULL,           -- Link ảnh icon của hoạt động (trên Cloudinary)
    category VARCHAR(50)              -- Phân loại: 'sport', 'work', 'relax'...
);

-- ========================================================
-- PHẦN 2: HỆ THỐNG NGƯỜI DÙNG & TỦ ĐỒ (USER & INVENTORY)
-- ========================================================

-- 5. Bảng Người dùng
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    gender VARCHAR(20),
    birthday DATE,
    avatar_url TEXT,
    coin_balance INT DEFAULT 0,       -- Số dư tiền/xu để mua theme trong Store
    active_theme_id INT DEFAULT 1,    -- Bộ theme đang được sử dụng hiện tại
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_theme FOREIGN KEY (active_theme_id) REFERENCES themes(id)
);

-- 6. Bảng Tủ đồ (Những bộ Theme mà User đã mua/sở hữu)
CREATE TABLE user_themes (
    user_id BIGINT NOT NULL,
    theme_id INT NOT NULL,
    purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, theme_id),  -- Đảm bảo 1 user chỉ mua 1 theme 1 lần
    CONSTRAINT fk_ut_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ut_theme FOREIGN KEY (theme_id) REFERENCES themes(id) ON DELETE CASCADE
);

-- ========================================================
-- PHẦN 3: NHẬT KÝ CÁ NHÂN (DAILY LOGS - NHƯ DAILY BEAN)
-- ========================================================

-- 7. Bảng Nhật ký chính của mỗi ngày
CREATE TABLE daily_moods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    date DATE NOT NULL,
    base_mood_id INT,                 -- Dùng base_mood_id để thống kê xuyên suốt các theme
    sleep_hours DECIMAL(3,1),
    menstruation_phase VARCHAR(50),   -- Theo dõi chu kỳ
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_daily_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_daily_base_mood FOREIGN KEY (base_mood_id) REFERENCES base_moods(id),
    UNIQUE (user_id, date)            -- Đảm bảo 1 ngày chỉ có 1 bản ghi chính
);

-- 8. Bảng Hoạt động chi tiết trong ngày
CREATE TABLE daily_mood_activities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    daily_mood_id BIGINT NOT NULL,
    activity_id INT NOT NULL,
    image_url TEXT,                   -- Ảnh minh họa phụ trợ cho hoạt động này (nếu có)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dma_daily FOREIGN KEY (daily_mood_id) REFERENCES daily_moods(id) ON DELETE CASCADE,
    CONSTRAINT fk_dma_activity FOREIGN KEY (activity_id) REFERENCES activities(id) ON DELETE CASCADE
);

-- ========================================================
-- PHẦN 4: MẠNG XÃ HỘI & THỜI GIAN THỰC (REAL-TIME MOMENTS)
-- ========================================================

-- 9. Bảng Khoảnh khắc (Ảnh chụp cam trước/sau)
CREATE TABLE moments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    daily_mood_id BIGINT,             -- Link về nhật ký ngày hôm đó (có thể NULL lúc mới chụp)
    image_url TEXT NOT NULL,          -- Link ảnh chính trên Cloudinary
    caption VARCHAR(255),
    is_public BOOLEAN DEFAULT FALSE,  -- Cờ chia sẻ cho bạn bè (dùng cho Phase 2)
    captured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_moments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_moments_daily FOREIGN KEY (daily_mood_id) REFERENCES daily_moods(id) ON DELETE SET NULL
);

