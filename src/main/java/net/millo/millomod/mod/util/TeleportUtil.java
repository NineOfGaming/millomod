package net.millo.millomod.mod.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.CompletableFuture;

public class TeleportUtil {

    public static CompletableFuture<Void> teleportToPosition(PlayerEntity player, Vec3d targetPos) {
        double checkDist = 7d;
        Vec3d initialPos = player.getPos();

        return CompletableFuture.runAsync(() -> {
            Vec3d pos = initialPos;
            try {
                Thread.sleep(100);
                for (int i = 0; i < 30; i++) {
                    if (player.getPos().isInRange(targetPos, checkDist)) break;

                    Vec3d dir = new Vec3d(targetPos.x, targetPos.y, targetPos.z).subtract(pos).normalize().multiply(checkDist);
                    pos = pos.add(dir);
                    player.setPosition(pos);

                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            player.setPosition(targetPos);
        });

    }

}
