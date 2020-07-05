package io.github.moulberry.rpg.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Inject(method = "run", at=@At(value = "HEAD"))
    private void onRun() {
        System.out.println("RUN HOOKED!!!");
        System.out.println("RUN HOOKED!!!");
        System.out.println("RUN HOOKED!!!");
        System.out.println("RUN HOOKED!!!");
    }

}
