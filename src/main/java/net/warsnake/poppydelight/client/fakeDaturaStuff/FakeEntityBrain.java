package net.warsnake.poppydelight.client.fakeDaturaStuff;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;


public class FakeEntityBrain {

    public double targetX, targetZ;

    public int wanderCooldown = 0;

    public double velX = 0, velZ = 0;

    public final double speed;

    public boolean grounded = false;

    public FakeEntityBrain(double speed) {
        this.speed = speed;
    }

    public static double findGroundY(Level level, double x, double currentY, double z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int startY = (int) (currentY + 4);
        int endY   = (int) (currentY - 8);

        for (int y = startY; y >= endY; y--) {
            pos.set((int) x, y, (int) z);

            if (level.getBlockState(pos).isSolid()) {
                return y + 1.0;
            }
        }
        return -1;
    }
}