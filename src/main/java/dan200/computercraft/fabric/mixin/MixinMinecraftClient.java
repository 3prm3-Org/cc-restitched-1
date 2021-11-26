/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2021. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.fabric.mixin;

import dan200.computercraft.client.FrameInfo;
import dan200.computercraft.fabric.events.ClientUnloadWorldEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( Minecraft.class )
public abstract class MixinMinecraftClient
{
    @Inject( method = "render", at = @At( "HEAD" ) )
    private void onRender( CallbackInfo info )
    {
        FrameInfo.onRenderFrame();
    }

    @Inject( method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At( "RETURN" ) )
    private void disconnectAfter( Screen screen, CallbackInfo info )
    {
        ClientUnloadWorldEvent.EVENT.invoker().onClientUnloadWorld();
    }

    @Inject( method = "joinWorld", at = @At( "RETURN" ) )
    private void joinWorldAfter( ClientLevel world, CallbackInfo info )
    {
        ClientUnloadWorldEvent.EVENT.invoker().onClientUnloadWorld();
    }
}
