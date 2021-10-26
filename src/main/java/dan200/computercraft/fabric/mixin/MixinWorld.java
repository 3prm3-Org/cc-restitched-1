/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2021. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.fabric.mixin;

import dan200.computercraft.shared.common.TileGeneric;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Horrible bodge to ensure a {@link BlockEntity}'s world is always present when setting a TE during another TE's tick.
 *
 * Forge does this, this is just a bodge to get Fabric in line with that behaviour.
 */
@Mixin( World.class )
public class MixinWorld
{
    @Shadow
    private boolean iteratingTickingBlockEntities;

    @Shadow
    public boolean isInBuildLimit( BlockPos pos )
    {
        return false;
    }

    @Inject( method = "addBlockEntity", at = @At( "HEAD" ) )
    public void addBlockEntity( @Nullable BlockEntity entity, CallbackInfo info )
    {
        if( entity != null && !entity.isRemoved() && this.isInBuildLimit( entity.getPos() ) && iteratingTickingBlockEntities )
        {
            setWorld( entity, this );
        }
    }

    private static void setWorld( BlockEntity entity, Object world )
    {
        if( entity.getWorld() != world && entity instanceof TileGeneric )
        {
            entity.setWorld( (World) world );
        }
    }

    //    @Inject( method = "addBlockEntities", at = @At( "HEAD" ) )
    public void addBlockEntities( Collection<BlockEntity> entities, CallbackInfo info )
    {
        if( iteratingTickingBlockEntities )
        {
            for( BlockEntity entity : entities )
            {
                setWorld( entity, this );
            }
        }
    }
}
