package com.pvz.data;

/**
 * ProjectileData: anh xa 1-1 voi projectiles/<ten>.json (pea, snow_pea...).
 */
public class ProjectileData {
    public String id;              // "pea", "snow_pea"
    public float speed;            // pixel/giay bay sang phai
    public float damage;
    public boolean slows;          // snow pea = true (lam cham zombie)
    public float slowFactor;       // vd 0.5 = cham con 1 nua
    public float slowDuration;     // giay
    public String[] animation;     // region atlas
}
