package com.pvz.manager;

/**
 * SoundKeys - Tất cả đường dẫn file âm thanh trong game PvZ.
 *
 * Cách dùng:
 *   AudioManager.get().playGameSound(SoundKeys.PEASHOOTER_THROW);
 *   AudioManager.get().playTheme(SoundKeys.MUSIC_PHONOGRAPH);
 *   AudioManager.get().playClick(SoundKeys.UI_BUTTONCLICK);
 */
public class SoundKeys {

    // =========================================================
    // PEASHOOTER
    // =========================================================
    public static final String PEASHOOTER_THROW     = "sounds/peashooter/throw(phát mỗi lần bắn đậu).wav";
    public static final String PEASHOOTER_THROW2    = "sounds/peashooter/throw2.wav";
    public static final String PEASHOOTER_SPLAT     = "sounds/peashooter/splat(phát khi đạn trúng zombie).wav";
    public static final String PEASHOOTER_SPLAT2    = "sounds/peashooter/splat2.wav";
    public static final String PEASHOOTER_PLANT     = "sounds/peashooter/plant(phát khi đặt cây xuống ô).wav";
    public static final String PEASHOOTER_SEEDLIFT  = "sounds/peashooter/seedlift(phát khi kéothả seed).wav";
    public static final String PEASHOOTER_SHOVEL    = "sounds/peashooter/shovel(xoa).wav";

    // =========================================================
    // SNOW PEA
    // =========================================================
    public static final String SNOWPEA_THROW        = "sounds/snow pea/throw(phát mỗi lần bắn đậu).wav";
    public static final String SNOWPEA_THROW2       = "sounds/snow pea/throw2.wav";
    public static final String SNOWPEA_SPLAT        = "sounds/snow pea/splat(phát khi đạn trúng zombie).wav";
    public static final String SNOWPEA_SPLAT2       = "sounds/snow pea/splat2.wav";
    public static final String SNOWPEA_FROZEN       = "sounds/snow pea/frozen( dong bang).wav";
    public static final String SNOWPEA_SPARKLES     = "sounds/snow pea/snow_pea_sparkles(hiệu ứng băng).wav";
    public static final String SNOWPEA_PLANT        = "sounds/snow pea/plant(phát khi đặt cây xuống ô).wav";
    public static final String SNOWPEA_SEEDLIFT     = "sounds/snow pea/seedlift(phát khi kéothả seed).wav";
    public static final String SNOWPEA_SHOVEL       = "sounds/snow pea/shovel(xoa).wav";

    // =========================================================
    // SUNFLOWER
    // =========================================================
    public static final String SUNFLOWER_PLANT      = "sounds/sunflower/plant(phát khi đặt cây xuống ô).wav";
    public static final String SUNFLOWER_SEEDLIFT   = "sounds/sunflower/seedlift(phát khi kéothả seed).wav";
    public static final String SUNFLOWER_SHOVEL     = "sounds/sunflower/shovel(xoa).wav";
    public static final String SUNFLOWER_POINTS     = "sounds/sunflower/points(tao sun).wav";
    public static final String SUNFLOWER_CHIME      = "sounds/sunflower/chime( sun suat hien).wav";
    public static final String SUNFLOWER_COIN       = "sounds/sunflower/coin(người chơi nhặt sun).wav";

    // =========================================================
    // WALL-NUT
    // =========================================================
    public static final String WALLNUT_PLANT        = "sounds/wall nut/plant(phát khi đặt cây xuống ô).wav";
    public static final String WALLNUT_SEEDLIFT     = "sounds/wall nut/seedlift(phát khi kéothả seed).wav";
    public static final String WALLNUT_SHOVEL       = "sounds/wall nut/shovel(xoa).wav";
    public static final String WALLNUT_BONK         = "sounds/wall nut/bonk( va cham lớn).wav";
    public static final String WALLNUT_CERAMIC      = "sounds/wall nut/ceramic( trang thai vỡ.wav";
    public static final String WALLNUT_PLASTICHIT   = "sounds/wall nut/plastichit( zombie cắn ).wav";
    public static final String WALLNUT_PLASTICHIT2  = "sounds/wall nut/plastichit2.wav";

    // =========================================================
    // CHERRY BOMB
    // =========================================================
    public static final String CHERRY_ACTIVATE      = "sounds/cherrybomb/cherrybomb( kích hoạt).wav";
    public static final String CHERRY_EXPLOSION     = "sounds/cherrybomb/explosion( vụ nổ chính).wav";
    public static final String CHERRY_REVERSE_EXP   = "sounds/cherrybomb/reverse_explosion(dư âm ).wav";
    public static final String CHERRY_SPLAT         = "sounds/cherrybomb/splat( zombie chêt nổ).wav";
    public static final String CHERRY_PLANT         = "sounds/cherrybomb/plant(phát khi đặt cây xuống ô).wav";
    public static final String CHERRY_SEEDLIFT      = "sounds/cherrybomb/seedlift(phát khi kéothả seed).wav";

    // =========================================================
    // ZOMBIE THƯỜNG
    // =========================================================
    public static final String ZOMBIE_CHOMP         = "sounds/chọn cây/Z thường/chomp.wav";
    public static final String ZOMBIE_CHOMP2        = "sounds/chọn cây/Z thường/chomp2.wav";
    public static final String ZOMBIE_BIGCHOMP      = "sounds/chọn cây/Z thường/bigchomp(cắn mạnh ).wav";
    public static final String ZOMBIE_GROAN         = "sounds/chọn cây/Z thường/groan( tiếng kêu ).wav";
    public static final String ZOMBIE_GROAN2        = "sounds/chọn cây/Z thường/groan2.wav";
    public static final String ZOMBIE_GROAN3        = "sounds/chọn cây/Z thường/groan3.wav";
    public static final String ZOMBIE_GROAN4        = "sounds/chọn cây/Z thường/groan4.wav";
    public static final String ZOMBIE_LOWGROAN      = "sounds/chọn cây/Z thường/lowgroan( zombie ở xa).wav";
    public static final String ZOMBIE_GRASSSTEP     = "sounds/chọn cây/Z thường/grassstep( bước trên cỏ ).wav";
    public static final String ZOMBIE_GULP          = "sounds/chọn cây/Z thường/gulp( nuốt cây ).wav";
    public static final String ZOMBIE_SPLAT         = "sounds/chọn cây/Z thường/splat( trúng đạn ).wav";
    public static final String ZOMBIE_LIMBS_POP     = "sounds/chọn cây/Z thường/limbs_pop( chân bật ra khi chết ).wav";
    public static final String ZOMBIE_FALLING1      = "sounds/chọn cây/Z thường/zombie_falling_1(chết ).wav";
    public static final String ZOMBIE_FALLING2      = "sounds/chọn cây/Z thường/zombie_falling_2.wav";

    // =========================================================
    // CONEHEAD ZOMBIE
    // =========================================================
    public static final String CONEHEAD_PLASTICHIT  = "sounds/coneheadZ/plastichit.wav";
    public static final String CONEHEAD_PLASTICHIT2 = "sounds/coneheadZ/plastichit2.wav";
    public static final String CONEHEAD_SHIELDHIT   = "sounds/coneheadZ/shieldhit.wav";
    public static final String CONEHEAD_SHIELDHIT2  = "sounds/coneheadZ/shieldhit2.wav";

    // =========================================================
    // UI - CHỌN CÂY
    // =========================================================
    public static final String UI_BLEEP             = "sounds/chọn cây/bleep.wav";
    public static final String UI_BUTTER            = "sounds/chọn cây/butter.wav";
    public static final String UI_BUTTONCLICK       = "sounds/chọn cây/buttonclick.wav";
    public static final String UI_COIN              = "sounds/chọn cây/coin.wav";
    public static final String UI_SEEDLIFT          = "sounds/chọn cây/seedlift.wav";
    public static final String UI_SHOVEL            = "sounds/chọn cây/shovel.wav";
    public static final String UI_TAP               = "sounds/chọn cây/tap.wav";
    public static final String UI_TAP2              = "sounds/chọn cây/tap2.wav";

    // =========================================================
    // GAMEPLAY
    // =========================================================
    public static final String GAMEPLAY_BUTTONCLICK = "sounds/trong gameplay/buttonclick.wav";
    public static final String GAMEPLAY_COIN        = "sounds/trong gameplay/coin.wav";
    public static final String GAMEPLAY_PAUSE       = "sounds/trong gameplay/pause.wav";
    public static final String GAMEPLAY_POINTS      = "sounds/trong gameplay/points.wav";
    public static final String GAMEPLAY_FINALWAVE   = "sounds/trong gameplay/finalwave.wav";
    public static final String GAMEPLAY_HUGEWAVE    = "sounds/trong gameplay/hugewave.wav";

    // =========================================================
    // THẮNG
    // =========================================================
    public static final String WIN_ACHIEVEMENT      = "sounds/thắng/achievement.wav";
    public static final String WIN_COIN             = "sounds/thắng/coin.wav";
    public static final String WIN_FINALFANFARE     = "sounds/thắng/finalfanfare.wav";
    public static final String WIN_PRIZE            = "sounds/thắng/prize.wav";
    public static final String WIN_MUSIC            = "sounds/thắng/winmusic.wav";

    // =========================================================
    // THUA
    // =========================================================
    public static final String LOSE_BUZZER          = "sounds/thua/buzzer.wav";
    public static final String LOSE_EVILLAUGH       = "sounds/thua/evillaugh.wav";
    public static final String LOSE_SCREAM          = "sounds/thua/scream.wav";
    public static final String LOSE_MUSIC           = "sounds/thua/losemusic.wav";

    // =========================================================
    // GIỚI THIỆU GAME / INTRO
    // =========================================================
    public static final String INTRO_CRAZYCRAZY     = "sounds/giới thiệu game/crazydavecrazy.wav";
    public static final String INTRO_DAVE_LONG1     = "sounds/giới thiệu game/crazydavelong1( hội thoại intro ).wav";
    public static final String INTRO_DAVE_LONG2     = "sounds/giới thiệu game/crazydavelong2.wav";
    public static final String INTRO_DAVE_LONG3     = "sounds/giới thiệu game/crazydavelong3.wav";
    public static final String INTRO_DAVE_SHORT1    = "sounds/giới thiệu game/crazydaveshort1.wav";
    public static final String INTRO_DAVE_SHORT2    = "sounds/giới thiệu game/crazydaveshort2( câu ngắn ).wav";
    public static final String INTRO_DAVE_SHORT3    = "sounds/giới thiệu game/crazydaveshort3.wav";
    public static final String INTRO_LOADING_FLOWER = "sounds/giới thiệu game/loadingbar_flower.wav";
    public static final String INTRO_LOADING_ZOMBIE = "sounds/giới thiệu game/loadingbar_zombie.wav";
    public static final String INTRO_ROLLIN         = "sounds/giới thiệu game/roll_in( cuộn map).wav";
    public static final String INTRO_PHONOGRAPH     = "sounds/giới thiệu game/phonograph( nhac intro cổ điển).wav";

    // =========================================================
    // BẮT ĐẦU BÀN CHƠI
    // =========================================================
    public static final String START_BUTTONCLICK    = "sounds/wall nut/bắt đầu bàn chơi/buttonclick.wav";
    public static final String START_CHIME          = "sounds/wall nut/bắt đầu bàn chơi/chime.wav";
    public static final String START_PLANT          = "sounds/wall nut/bắt đầu bàn chơi/plant.wav";
    public static final String START_SEEDLIFT       = "sounds/wall nut/bắt đầu bàn chơi/seedlift.wav";
    public static final String START_READYSETPLANT  = "sounds/wall nut/bắt đầu bàn chơi/readysetplant.wav";
}
