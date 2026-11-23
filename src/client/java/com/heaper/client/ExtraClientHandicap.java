package com.heaper.client;

import com.handicap.client.ClientState;
import com.heaper.HandicapBehaviour;
import net.minecraft.client.Minecraft;

public class ExtraClientHandicap extends HandicapBehaviour {
    protected ExtraClientHandicap(String id) {
        super(id);
    }

    public void onGive(Minecraft mc) {}
    public void onRemove(Minecraft mc) {}
    public void onTick(Minecraft mc) {}

    protected boolean has() {
        return ClientState.has(handicap());
    }
}
