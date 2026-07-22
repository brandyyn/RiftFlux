package com.voidsrift.riftflux.mixin.late.xaero.minimap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.ScreenBase;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointSet;
import xaero.common.minimap.waypoints.WaypointWorld;
import xaero.common.minimap.waypoints.WaypointsManager;

@Mixin(targets = "xaero.common.gui.GuiWaypoints", remap = false)
public abstract class MixinGuiWaypoints_ImmediateDelete extends ScreenBase {
    @Shadow
    private WaypointWorld displayedWorld;

    @Shadow
    private ConcurrentSkipListSet<Integer> selectedListSet;

    @Shadow
    private ArrayList<Waypoint> waypointsSorted;

    @Shadow
    private WaypointsManager waypointsManager;

    @Shadow
    private boolean buttonClicked;

    protected MixinGuiWaypoints_ImmediateDelete(IXaeroMinimap modMain, GuiScreen parent, GuiScreen escape) {
        super(modMain, parent, escape);
    }

    @Inject(
            method = {
                    "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V",
                    "func_146284_a(Lnet/minecraft/client/gui/GuiButton;)V"
            },
            at = @At("HEAD"),
            cancellable = true,
            require = 1
    )
    private void riftflux$deleteWaypointImmediately(GuiButton button, CallbackInfo callbackInfo) {
        if (button == null || !button.enabled || button.id != 5 || this.displayedWorld == null
                || this.selectedListSet.isEmpty()) {
            return;
        }

        ArrayList<Waypoint> selectedWaypoints = new ArrayList<Waypoint>();
        for (Integer selectedIndex : this.selectedListSet) {
            if (selectedIndex == null || selectedIndex < 0 || selectedIndex >= this.waypointsSorted.size()) {
                continue;
            }

            Waypoint waypoint = this.waypointsSorted.get(selectedIndex);
            if (waypoint != null && !waypoint.isServerWaypoint()) {
                selectedWaypoints.add(waypoint);
            }
        }
        if (selectedWaypoints.isEmpty()) {
            return;
        }

        WaypointSet currentSet = this.displayedWorld.getCurrentSet();
        if (currentSet == null) {
            return;
        }

        currentSet.getList().removeAll(selectedWaypoints);
        if (this.waypointsSorted != currentSet.getList()) {
            this.waypointsSorted.removeAll(selectedWaypoints);
        }
        this.selectedListSet.clear();
        this.buttonClicked = true;
        this.waypointsManager.updateWaypoints();

        try {
            this.modMain.getSettings().saveWaypoints(this.displayedWorld);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        callbackInfo.cancel();
    }
}
