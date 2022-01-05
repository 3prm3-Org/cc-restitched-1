/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.turtle.upgrades;

import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;

public class TurtleCraftingTable extends AbstractTurtleUpgrade
{
    @Environment( EnvType.CLIENT )
    private ModelIdentifier leftModel;

    @Environment( EnvType.CLIENT )
    private ModelIdentifier rightModel;

    public TurtleCraftingTable( Identifier id )
    {
        super( id, TurtleUpgradeType.PERIPHERAL, Blocks.CRAFTING_TABLE );
    }

    @Override
    public IPeripheral createPeripheral( @Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side )
    {
        return new CraftingTablePeripheral( turtle );
    }

    @Nonnull
    @Override
    @Environment( EnvType.CLIENT )
    public TransformedModel getModel( ITurtleAccess turtle, @Nonnull TurtleSide side )
    {
        loadModelLocations();
        return TransformedModel.of( side == TurtleSide.LEFT ? leftModel : rightModel );
    }

    @Environment( EnvType.CLIENT )
    private void loadModelLocations()
    {
        if( leftModel == null )
        {
            leftModel = new ModelIdentifier( "computercraft:turtle_crafting_table_left", "inventory" );
            rightModel = new ModelIdentifier( "computercraft:turtle_crafting_table_right", "inventory" );
        }
    }
}
