package net.warsnake.poppydelight.client.fakeDaturaStuff;

import net.minecraft.world.entity.animal.goat.Goat;
import net.warsnake.poppydelight.PoppyDelight;
import net.warsnake.poppydelight.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.animal.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = PoppyDelight.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class FakeEntityHandler {

    private static int nextFakeId = -1;
    private static final List<Integer> fakeEntityIds = new ArrayList<>();

    private static final Map<Integer, FakeEntityBrain> brains = new HashMap<>();

    private static final int MAX_FAKE_ENTITIES = 8;
    private static final double SPAWN_RADIUS = 16.0;
    private static final int SPAWN_CHANCE = 40;
    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;

        boolean hasEffect = player.hasEffect(ModEffects.DATURA.get());

        if (hasEffect) {
            if (fakeEntityIds.size() < MAX_FAKE_ENTITIES && RANDOM.nextInt(SPAWN_CHANCE) == 0) {
                spawnFakeEntity(level, player);
            }

            fakeEntityIds.removeIf(id -> {
                Entity e = level.getEntity(id);
                if (e == null || e.isRemoved()) {
                    brains.remove(id);
                    return true;
                }
                if (e.distanceTo(player) > SPAWN_RADIUS * 2) {
                    level.removeEntity(id, Entity.RemovalReason.DISCARDED);
                    brains.remove(id);
                    return true;
                }

                FakeEntityBrain brain = brains.get(id);
                if (brain != null) {
                    tickFakeAI(e, brain, level);
                }
                return false;
            });

        } else {
            if (!fakeEntityIds.isEmpty()) {
                removeAllFakeEntities(level);
            }
        }
    }

    private static void tickFakeAI(Entity entity, FakeEntityBrain brain, ClientLevel level) {

        if (brain.wanderCooldown <= 0) {
            double angle    = RANDOM.nextDouble() * Math.PI * 2;
            double distance = 3.0 + RANDOM.nextDouble() * 8.0;
            brain.targetX      = entity.getX() + Math.cos(angle) * distance;
            brain.targetZ      = entity.getZ() + Math.sin(angle) * distance;
            brain.wanderCooldown = 40 + RANDOM.nextInt(80); // 2–6 seconds
        }
        brain.wanderCooldown--;

        double dx = brain.targetX - entity.getX();
        double dz = brain.targetZ - entity.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);

        if (dist > 0.5) {
            double accel = 0.08;
            brain.velX += (dx / dist) * accel;
            brain.velZ += (dz / dist) * accel;
        }

        double currentSpeed = Math.sqrt(brain.velX * brain.velX + brain.velZ * brain.velZ);
        if (currentSpeed > brain.speed) {
            brain.velX = (brain.velX / currentSpeed) * brain.speed;
            brain.velZ = (brain.velZ / currentSpeed) * brain.speed;
        }
        brain.velX *= 0.85;
        brain.velZ *= 0.85;

        double newX = entity.getX() + brain.velX;
        double newZ = entity.getZ() + brain.velZ;
        double groundY = FakeEntityBrain.findGroundY(level, newX, entity.getY(), newZ);
        double newY = (groundY >= 0) ? groundY : entity.getY();

        entity.setPos(newX, newY, newZ);

        if (currentSpeed > 0.01) {
            float yaw = (float) (Math.toDegrees(Math.atan2(-brain.velX, brain.velZ)));
            entity.setYRot(yaw);
            entity.yRotO = yaw;
        }
    }

    private static void spawnFakeEntity(ClientLevel level, LocalPlayer player) {
        double angle    = RANDOM.nextDouble() * Math.PI * 2;
        double distance = 5.0 + RANDOM.nextDouble() * (SPAWN_RADIUS - 5.0);
        double x = player.getX() + Math.cos(angle) * distance;
        double z = player.getZ() + Math.sin(angle) * distance;

        double groundY = FakeEntityBrain.findGroundY(level, x, player.getY(), z);
        double y = (groundY >= 0) ? groundY : player.getY();

        Entity fakeEntity = createRandomFakeEntity(level);
        if (fakeEntity == null) return;

        fakeEntity.setPos(x, y, z);

        int fakeId = nextFakeId--;
        fakeEntity.setId(fakeId);

        level.putNonPlayerEntity(fakeId, fakeEntity);
        fakeEntityIds.add(fakeId);

        double speed = 0.08 + RANDOM.nextDouble() * 0.08;
        brains.put(fakeId, new FakeEntityBrain(speed));
    }

    private static Entity createRandomFakeEntity(ClientLevel level) {
        int choice = RANDOM.nextInt(8);
        return switch (choice) {
            case 0 -> new Zombie(EntityType.ZOMBIE, level);
            case 1 -> new Skeleton(EntityType.SKELETON, level);
            case 2 -> new Creeper(EntityType.CREEPER, level);
            case 3 -> new Spider(EntityType.SPIDER, level);
            case 4 -> new Cow(EntityType.COW, level);
            case 5 -> new Pig(EntityType.PIG, level);
            case 6 -> new Goat(EntityType.GOAT, level);
            case 7 -> new Witch(EntityType.WITCH, level);
            default -> null;
        };
    }

    public static void removeAllFakeEntities(ClientLevel level) {
        for (int id : fakeEntityIds) {
            Entity e = level.getEntity(id);
            if (e != null && !e.isRemoved()) {
                level.removeEntity(id, Entity.RemovalReason.DISCARDED);
            }
        }
        fakeEntityIds.clear();
        brains.clear();
    }

    public static int getFakeEntityCount() {
        return fakeEntityIds.size();
    }
}