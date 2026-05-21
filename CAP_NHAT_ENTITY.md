# CẬP NHẬT — Entity đặc biệt + Data hoàn chỉnh

File này ghi lại những gì vừa hoàn thành ở bước này (đọc kèm HUONG_DAN.md).

## Đã compile sạch
Toàn bộ 37 file Java (core) biên dịch thành công, 0 lỗi, 0 cảnh báo. 23/23 JSON hợp lệ.

## Data đã điền (từ datapvz.docx)

### Cây — quy đổi đơn vị
- `attackInterval = AttackDamage / DPS`. Ví dụ Peashooter 20 / 13.333 = 1.5s.
- `recharge`: Fast = 7.5s, Slow = 15s, Very slow = 50s.
- Sunflower `sunInterval` = 24.25s (theo cột Speed của data).
- Cherry Bomb hp = 999999 (gần bất tử trong 1.2s rồi tự nổ), nổ 1800 vùng 3×3.
- Wall-nut hp = 4000. Potato Mine arm 15s, nổ 1800. Chomper nhai 42s, nuốt chửng (chompDamage rất lớn).
- Snow Pea cost 175 (mình lấy giá Normal; data có 175/150/100 theo chế độ — chỉnh trong JSON nếu muốn).
- Repeater cost 200, bắn 2 pea.

### Zombie — quy đổi tốc độ
- `speed (pixel/giây) = CELL_WIDTH(100) / (giây mỗi ô)`.
- Basic 4.7s/ô → 21.3 px/s. Flag 3.7 → 27. Conehead hp 560. Buckethead hp 1290.
- Pole Vault: 2.5s/ô trước nhảy → 40 px/s, sau nhảy 4.7s/ô → 21.3 px/s (field `speedAfterVault`).
- Conehead/Buckethead KHÔNG dùng armor riêng — chỉ là hp lớn hơn (đúng yêu cầu của bạn).

### Bảng unlock (đã đổi theo data mới, GIỮ shovel/speed)
| Level | Cây thưởng | Zombie mới | Feature |
|---|---|---|---|
| 1-1 | Sunflower | Basic | — |
| 1-2 | Cherry Bomb | Flag | — |
| 1-3 | Wall-nut | Conehead | **shovel** |
| 1-4 | — | — | **speed 2x** |
| 1-5 | **Potato Mine** | — | — |
| 1-6 | Snow Pea | Pole Vaulting | — |
| 1-7 | Chomper | — | — |
| 1-8 | Repeater | Buckethead | — |

## Entity đặc biệt — code mới

Tất cả dùng OOP inheritance + interface (SOLID):

- **CherryBomb** (`entity/plant/CherryBomb.java`): đếm `explodeDelay` (1.2s) rồi nổ vùng 3×3, gọi `ctx.damageArea`, tự biến mất.
- **PotatoMine** (`PotatoMine.java`): có `armTime`; chưa arm thì block (zombie ăn được → biến mất không nổ); arm xong thì nổ ngay khi zombie vào ô.
- **Chomper** (`Chomper.java`): nuốt 1 zombie → nhai (`chewTime`); khi nhai vẫn block, mất máu dần, có thể chết trước khi nhai xong.
- **PoleVaultZombie** (`entity/zombie/PoleVaultZombie.java`): nhảy qua cây ĐẦU TIÊN (nội suy vị trí, có hiệu ứng bay lên), sau đó đi thường; gặp cây thứ 2 thì ăn.
- **PlantContext** (`entity/plant/PlantContext.java`): interface để cây tác động lên thế giới (zombiesInCell, zombiesInArea, damageArea, removePlant). GameScreen implement nó → Dependency Inversion, không để cây phụ thuộc trực tiếp GameScreen.

Snow Pea và Repeater KHÔNG cần lớp con riêng: chúng chỉ khác ở data (`projectileType=snow_pea`, `projectilePerShot=2`), GameScreen xử lý đủ. Flag/Conehead/Buckethead cũng vậy (chỉ khác hp/speed trong JSON).

## Bạn vẫn cần làm
1. Build thử: `./gradlew lwjgl3:run`. Báo mình nếu có lỗi runtime.
2. Asset: sprite atlas + 3 file nhạc (xem HUONG_DAN.md mục 3-4).
3. Tinh chỉnh số liệu trong JSON nếu muốn cân bằng lại.

## Test nhanh entity đặc biệt
Vì cây unlock theo tiến độ, muốn test nhanh Cherry/Potato/Chomper... bạn có thể tạm sửa file save `pvz_save.json` (ở thư mục chạy game) cho `lastUnlockedLevel` cao, hoặc thắng dần. Mỗi level chơi bằng placeholder khối màu:
- Cherry Bomb: khối đỏ, đặt cạnh cụm zombie → 1.2s sau nổ sạch vùng 3×3.
- Potato Mine: khối nâu (chưa arm) → đỏ (arm xong) → nổ khi zombie chạm.
- Chomper: khối tím sáng (rảnh) → tím đậm (đang nhai 42s).
- Pole Vault: khối xanh, gặp cây đầu → chuyển cam và "bay" qua → xám đi thường.
