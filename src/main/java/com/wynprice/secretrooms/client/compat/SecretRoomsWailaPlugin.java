package com.wynprice.secretrooms.client.compat;

import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaPlugin;

@WailaPlugin
public class SecretRoomsWailaPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addOverride(new SecretRoomsWailaOverride(), SecretBaseBlock.class);
    }
}
