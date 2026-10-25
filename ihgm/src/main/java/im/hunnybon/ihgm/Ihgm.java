package im.hunnybon.ihgm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.gamerules.GameRules;

public class Ihgm implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            // Check first for if the player count is equal to exactly 0.
            // If it is, also check to see if the gamerule advanceTime
            // is set to true, if it is, roll the overworld clock back by
            // 1 tick each server tick so time of day stands still while
            // the server is empty.
            if (server.getPlayerCount() == 0){
                if (server.getGameRules().get(GameRules.ADVANCE_TIME)){
                    Holder<WorldClock> clock = server.registryAccess().lookupOrThrow(Registries.WORLD_CLOCK).getOrThrow(WorldClocks.OVERWORLD);
                    server.clockManager().addTicks(clock, -1);
                }
            };
        });
    }
}
