# PvZ — Hướng dẫn dự án (libGDX 1.14.0 + Java 17)

Tài liệu này giải thích bộ khung code mình đã dựng, và **chỉ rõ từng việc bạn cần làm** để hoàn thành game.

---

## 1. Hiện trạng: bạn đang có gì

Bộ khung **chạy được ngay** với placeholder khối màu (chưa cần ảnh/nhạc):

- 6 màn hình điều hướng đầy đủ: Startup → ChoosePlant → Game → Win/Lose, và Complete.
- "Van khóa thời gian" `GameClock` hoạt động: pause, speed 2x, clamp delta.
- Level 1-1 đã có **dữ liệu mẫu** (peashooter, sunflower, basic zombie, pea) để bạn chơi thử: đặt cây bằng chuột, sun rơi, zombie tới, lawnmower cứu, win/lose.
- Toàn bộ 23 file JSON template đúng tên field, chờ bạn đè data thật.

### Điều khiển khi test
- **Click chuột**: nhặt sun / đặt cây (cây đầu tiên đã chọn).
- **P** hoặc **ESC**: pause / resume.
- **S**: bật/tắt speed 2x (chỉ từ level 4 trở đi).

---

## 2. Kiến trúc & Design Patterns

### Cấu trúc package
```
com.pvz
├── core/         GameClock (van khóa thời gian), GameConfig (hằng số)
├── data/         POJO khớp JSON: PlantData, ZombieData, ProjectileData, LevelData
├── entity/       Entity (gốc) → Plant, Zombie, Projectile, LawnMower, Sun
├── factory/      PlantFactory, ZombieFactory, ProjectileFactory (Factory Method)
├── manager/      SaveManager, DataManager, AudioManager, ScreenManager (Singleton)
├── screen/       6 màn hình + BaseScreen
└── system/       GridSystem, WaveSystem, PlantUnlockSystem
```

### Các pattern dùng ở đâu
| Pattern | Lớp | Vai trò |
|---|---|---|
| **Singleton** | SaveManager, DataManager, AudioManager, ScreenManager | Một thể hiện duy nhất, truy cập toàn cục qua `.get()` |
| **Factory Method** | PlantFactory, ZombieFactory, ProjectileFactory | Tạo entity từ id + data, ẩn chi tiết khởi tạo |
| **Inheritance (OOP)** | Entity → Plant/Zombie/Projectile/LawnMower/Sun | Mọi thực thể chung vòng đời `update/draw` |
| **State** (sẽ thêm) | Zombie.State, Plant trạng thái | shooting/eating/walking |
| **Strategy** (sẽ thêm) | Hành vi zombie đặc biệt | pole vault, flag... |

### Van khóa thời gian — điểm quan trọng nhất
Trong `GameScreen.update(rawDelta)`:
- `gameDelta = clock.tick(rawDelta)` → cho **world** (zombie, sun, cooldown, progress, wave). Bằng 0 khi pause, ×2 khi speed.
- `realDelta = clock.realDelta(rawDelta)` → cho **UI/menu**, không bao giờ đóng băng.
- **Mọi** `update(float delta)` của entity/system đều nhận delta từ một nguồn này. Không lớp nào tự gọi `Gdx.graphics.getDeltaTime()`.

→ Muốn thêm slow-motion sau này? Chỉ cần đổi `timeScale` trong GameClock. Một con số điều khiển toàn bộ dòng thời gian.

---

## 3. Cách build & chạy (BẠN làm)

Mình **không build được trong môi trường này** vì nó chặn tải thư viện libGDX từ Maven. Bạn build trên máy mình:

```bash
# Trong thư mục pvz/
./gradlew lwjgl3:run        # Linux/macOS
gradlew.bat lwjgl3:run      # Windows
```

Lần đầu Gradle sẽ tải libGDX (cần mạng). Nếu báo lỗi biên dịch, **copy nguyên văn lỗi gửi mình**, mình sửa ngay.

> Khuyên dùng IntelliJ IDEA: mở thư mục `pvz`, nó tự nhận Gradle. Chạy `Lwjgl3Launcher`.

---

## 4. VIỆC BẠN CẦN LÀM (theo thứ tự ưu tiên)

### Bước 1 — Build thử khung (ngay bây giờ)
Chạy lệnh trên, vào Start New, chơi thử level 1-1 bằng khối màu. Mục tiêu: xác nhận khung sống. Báo mình mọi lỗi.

### Bước 2 — Điền data JSON thật
Mở `assets/data/` và đè số vào. **Giữ nguyên tên field**, chỉ đổi giá trị.
- `plants/*.json` — hp, cost, cooldown, damage, attackInterval, sun production...
- `zombies/*.json` — hp, speed, damage, eatInterval. Conehead/Buckethead chỉ cần hp cao hơn (không cần "armor").
- `projectiles/*.json` — speed, damage; snow_pea có slows=true.
- `levels/level_1_X.json` — wave timeline, huge wave, sun interval, maxPlants.

**Tên id phải khớp tên file.** Ví dụ `plants/snowpea.json` thì id="snowpea", và `PlantUnlockSystem` cũng dùng "snowpea".

### Bước 3 — Asset đồ họa & âm thanh
- Sprite cây/zombie: gộp thành **TextureAtlas** (dùng tool `gdx-texture-packer`). Điền tên region vào field `animations` trong JSON.
- Âm thanh: đặt vào `assets/audio/` (ví dụ `theme.ogg`, `click.ogg`). AudioManager đã sẵn sàng đọc; bạn chỉ cần gọi đúng đường dẫn.

### Bước 4 — Cùng mình code entity đặc biệt
Sau khi khung chạy ổn, mình viết tiếp (mỗi cái là một lớp con):
- Potato Mine (arm time, nổ khi zombie chạm, bị ăn khi đang arm)
- Cherry Bomb (nổ 3×3 sau 1.2s)
- Chomper (nhai, block, có thể chết khi đang nhai)
- Wall-nut (blocksZombie=true, hp cao)
- Snow Pea / Repeater (slow / bắn 2 pea)
- Pole Vaulting Zombie (nhảy cây đầu), Flag, Conehead, Buckethead
- Shovel (gỡ cây, unlock lv3), nút speed UI (unlock lv4)

---

## 5. Những chỗ mình để TODO sẵn cho bạn/chúng ta
- `PlantFactory/ZombieFactory`: chỗ `switch` để trả về lớp con đặc biệt.
- `Plant/Zombie/Entity.draw()`: vẽ animation từ atlas (hiện chỉ `drawDebug` khối màu).
- `GameScreen`: nút pause/speed/shovel bằng UI (hiện dùng phím tắt để test).
- Sun rơi từ trời theo interval (hiện chỉ có sun từ Sunflower; thêm timer đọc `sunFallInterval`).

---

## 6. Quy ước toạ độ (sửa khi có ảnh nền)
Trong `GameConfig`: `LAWN_X, LAWN_Y, CELL_WIDTH, CELL_HEIGHT` là **giá trị mẫu**. Khi có ảnh nền sân cỏ thật, chỉnh 4 số này cho khớp lưới 5×9. `GridSystem` tự tính phần còn lại.

---

Có lỗi build hay muốn mình code phần tiếp theo, cứ gửi mình. Thứ tự đề xuất: **build khung → báo lỗi (nếu có) → mình thêm sun-từ-trời + UI nút → rồi tới entity đặc biệt.**
