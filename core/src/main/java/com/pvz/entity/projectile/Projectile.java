package com.pvz.entity.projectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pvz.data.ProjectileData;
import com.pvz.entity.AnimationComponent;
import com.pvz.entity.Entity;

/**
 * Projectile (vd: pea, snow pea): la ENTITY theo yeu cau.
 * Bay PIXEL-based sang phai tren 1 hang. Trung zombie dau tien cung row co
 * pixelX gan nhat (lop dieu phoi xu ly va cham).
 *
 * Co he thong anh: doc ten region tu ProjectileData.animation.
 * Co anh -> ve anh; khong co -> fallback khoi mau.
 */
public class Projectile extends Entity {

    private final ProjectileData data;
    private final int row;
    private final AnimationComponent anim = new AnimationComponent();

    public Projectile(ProjectileData data, int row, float startX, float centerY,
                      float size) {
        super(startX, centerY, size, size, 1f);
        this.data = data;
        this.row = row;
        // pea thuong la 1 frame tinh; neu nhieu frame thi tu chay animation
        anim.addState("fly", data.animation);
        anim.setState("fly");
    }

    @Override
    public void update(float delta) {
        x += data.speed * delta;
        anim.update(delta);
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion frame = anim.getFrame();
        if (frame != null) {
            batch.setColor(Color.WHITE);
            batch.draw(frame, x - width / 2f, y - height / 2f, width, height);
        }
    }

    public int getRow() { return row; }
    public ProjectileData getData() { return data; }
}
