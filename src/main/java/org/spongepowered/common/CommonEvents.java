package org.spongepowered.common;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerAboutToStartEvent;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.common.world.DimensionManager;
import org.spongepowered.common.world.SpongeDimensionType;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.UUID;

/**
 * TODO No idea where to put this class in common. Need to ask someone...
 * TODO Register this event
 */
public class CommonEvents {

    @Subscribe(order = Order.FIRST)
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        registerAllEnabledWorlds();
    }

    public void registerAllEnabledWorlds() {
        File[] directoryListing = DimensionManager.getCurrentSaveRootDirectory().listFiles();
        if (directoryListing == null) {
            return;
        }

        for (File child : directoryListing) {
            File levelData = new File(child, "level_sponge.dat");
            if (!child.isDirectory() || !levelData.exists()) {
                continue;
            }

            try {
                NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(levelData));
                if (nbt.hasKey(Sponge.ECOSYSTEM_NAME)) {
                    NBTTagCompound spongeData = nbt.getCompoundTag(Sponge.ECOSYSTEM_NAME);
                    boolean enabled = spongeData.getBoolean("enabled");
                    boolean loadOnStartup = spongeData.getBoolean("loadOnStartup");
                    int dimensionId = spongeData.getInteger("dimensionId");
                    if (!(dimensionId == -1) && !(dimensionId == 0) && !(dimensionId == 1)) {
                        if (!enabled) {
                            Sponge.getLogger().info("World {} is currently disabled. Skipping world load...", child.getName());
                            continue;
                        }
                        if (!loadOnStartup) {
                            Sponge.getLogger().info("World {} 'loadOnStartup' is disabled.. Skipping world load...", child.getName());
                            continue;
                        }
                    } else if (dimensionId == -1) {
                        if (!MinecraftServer.getServer().getAllowNether()) {
                            continue;
                        }
                    }
                    if (spongeData.hasKey("uuid_most") && spongeData.hasKey("uuid_least")) {
                        UUID uuid = new UUID(spongeData.getLong("uuid_most"), spongeData.getLong("uuid_least"));
                        Sponge.getSpongeRegistry().registerWorldUniqueId(uuid, child.getName());
                    }
                    if (spongeData.hasKey("dimensionId") && spongeData.getBoolean("enabled")) {
                        int dimension = spongeData.getInteger("dimensionId");
                        for (Map.Entry<Class<? extends Dimension>, DimensionType> mapEntry : Sponge.getSpongeRegistry().dimensionClassMappings
                                .entrySet()) {
                            if (mapEntry.getKey().getCanonicalName().equalsIgnoreCase(spongeData.getString("dimensionType"))) {
                                Sponge.getSpongeRegistry().registerWorldDimensionId(dimension, child.getName());
                                if (!DimensionManager.isDimensionRegistered(dimension)) {
                                    DimensionManager.registerDimension(dimension,
                                            ((SpongeDimensionType) mapEntry.getValue()).getDimensionTypeId());
                                }
                            }
                        }
                    } else {
                        Sponge.getLogger().info("World {} is disabled! Skipping world registration...", child.getName());
                    }
                }
            } catch (Throwable t) {
                Sponge.getLogger().error("Error during world registration.", t);
            }
        }
    }
}
