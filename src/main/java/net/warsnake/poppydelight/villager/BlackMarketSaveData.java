package net.warsnake.poppydelight.villager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;

public class BlackMarketSaveData extends SavedData {

    private static final String TAG_DEMAND = "Demand";

    private final Map<String, Integer> demand = new HashMap<>();

    public static BlackMarketSaveData load(CompoundTag tag) {
        BlackMarketSaveData data = new BlackMarketSaveData();

        if (tag.contains(TAG_DEMAND)) {
            CompoundTag d = tag.getCompound(TAG_DEMAND);

            for (String key : d.getAllKeys()) {
                data.demand.put(key, d.getInt(key));
            }
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag d = new CompoundTag();

        for (Map.Entry<String, Integer> entry : demand.entrySet()) {
            d.putInt(entry.getKey(), entry.getValue());
        }

        tag.put(TAG_DEMAND, d);
        return tag;
    }

    public Map<String, Integer> getDemand() {
        return demand;
    }

    public void increaseDemand(String id) {
        demand.put(id, demand.getOrDefault(id, 0) + 1);
        setDirty();
    }

    public void decay() {

        demand.replaceAll((k, v) -> {

            if (v > 10) {
                return (int) Math.floor(v * 0.85);
            } else if (v > 1) {
                return v - 1;
            }

            return v;
        });

        setDirty();
    }


    public class BlackMarketDataManager {

        private static final String NAME = "poppydelight_blackmarket";

        public static BlackMarketSaveData get(ServerLevel level) {

            return level.getDataStorage().computeIfAbsent(
                    BlackMarketSaveData::load,
                    BlackMarketSaveData::new,
                    NAME
            );
        }
    }
}