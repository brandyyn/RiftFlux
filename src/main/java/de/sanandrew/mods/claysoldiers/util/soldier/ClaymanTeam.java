/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  cpw.mods.fml.common.FMLLog
 *  org.apache.logging.log4j.Level
 */
package de.sanandrew.mods.claysoldiers.util.soldier;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.FMLLog;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

public final class ClaymanTeam {
    public static final ClaymanTeam NULL_TEAM = new ClaymanTeam();
    private static final Map<String, ClaymanTeam> TEAMS_ = Maps.newHashMap();
    private static final List<String> TEAM_NAMES_FOR_DOLLS_ = new ArrayList<String>();
    private static boolean isInitialized = false;
    private String p_name;
    private ResourceLocation[] p_texturesDefault;
    private ResourceLocation[] p_texturesRare;
    private ResourceLocation[] p_texturesUnique;
    private String p_icon;
    private int p_iconColor;
    private int p_teamColor;
    private ItemStack p_teamItem;
    private IIcon p_iconInstance;

    private ClaymanTeam() {
        this.p_name = "nullTeam";
        this.p_teamColor = 0xFFFFFF;
        this.p_iconColor = 0xFFFFFF;
        this.p_teamItem = new ItemStack(Blocks.command_block);
    }

    private ClaymanTeam(String teamName, int teamColor, ItemStack teamItem, String iconTexture, String[] defTextures, String[] rareTextures, String[] uniqueTextures) throws ClaymanTeamRegistrationException {
        int i;
        if (teamName == null) {
            throw new ClaymanTeamRegistrationException("teamName cannot be null!");
        }
        if (teamName.isEmpty()) {
            throw new ClaymanTeamRegistrationException("teamName cannot be empty!");
        }
        if (iconTexture == null) {
            throw new ClaymanTeamRegistrationException("iconTexture cannot be null!");
        }
        if (iconTexture.isEmpty()) {
            throw new ClaymanTeamRegistrationException("iconTexture cannot be empty!");
        }
        if (teamItem == null) {
            throw new ClaymanTeamRegistrationException("teamItem cannot be null!");
        }
        this.p_name = teamName;
        this.p_icon = iconTexture;
        this.p_teamColor = teamColor;
        this.p_iconColor = 0xFFFFFF;
        this.p_teamItem = teamItem;
        if (defTextures != null) {
            if (defTextures.length == 0) {
                throw new ClaymanTeamRegistrationException("defTextures cannot be empty!");
            }
            this.p_texturesDefault = new ResourceLocation[defTextures.length];
            for (i = 0; i < defTextures.length; ++i) {
                this.p_texturesDefault[i] = new ResourceLocation(defTextures[i]);
            }
        } else {
            throw new ClaymanTeamRegistrationException("defTextures cannot be null!");
        }
        if (rareTextures != null && rareTextures.length > 0) {
            this.p_texturesRare = new ResourceLocation[rareTextures.length];
            for (i = 0; i < rareTextures.length; ++i) {
                this.p_texturesRare[i] = new ResourceLocation(rareTextures[i]);
            }
        } else {
            this.p_texturesRare = new ResourceLocation[0];
        }
        if (uniqueTextures != null && uniqueTextures.length > 0) {
            this.p_texturesUnique = new ResourceLocation[uniqueTextures.length];
            for (i = 0; i < uniqueTextures.length; ++i) {
                this.p_texturesUnique[i] = new ResourceLocation(uniqueTextures[i]);
            }
        } else {
            this.p_texturesUnique = new ResourceLocation[0];
        }
    }

    public static void initialize() {
        if (isInitialized) {
            FMLLog.log((String)"ClaySoldiers", (Level)Level.WARN, (String)"Something tried to re-initialize the clayman teams! This is not allowed and should be checked!", (Object[])new Object[0]);
            return;
        }
        isInitialized = true;
        TEAMS_.put(ClaymanTeam.NULL_TEAM.p_name, NULL_TEAM);
        ClaymanTeam.registerTeam("clay", 0x999999, new ItemStack(Items.dye, 1, 7), new String[]{"claysoldiers:textures/entity/soldiers/lightgray.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/lightgray.png"}, null).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("black", 0x191919, new ItemStack(Items.dye, 1, 0), new String[]{"claysoldiers:textures/entity/soldiers/black.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/black.png"}, new String[]{"claysoldiers:textures/entity/soldiers_unique/black.png"}).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("red", 0xCC4646, new ItemStack(Items.dye, 1, 1), new String[]{"claysoldiers:textures/entity/soldiers/red.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/red.png"}, new String[]{"claysoldiers:textures/entity/soldiers_unique/red.png"}).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("green", 6717235, new ItemStack(Items.dye, 1, 2), new String[]{"claysoldiers:textures/entity/soldiers/green.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/green.png"}, new String[]{"claysoldiers:textures/entity/soldiers_unique/green.png", "claysoldiers:textures/entity/soldiers_unique/green2.png"}).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("brown", 6704179, new ItemStack(Items.dye, 1, 3), new String[]{"claysoldiers:textures/entity/soldiers/brown.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/brown.png", "claysoldiers:textures/entity/soldiers_rare/brown2.png"}, new String[]{"claysoldiers:textures/entity/soldiers_unique/brown.png", "claysoldiers:textures/entity/soldiers_unique/brown2.png"}).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("blue", 3361970, new ItemStack(Items.dye, 1, 4), new String[]{"claysoldiers:textures/entity/soldiers/blue.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/blue.png"}, new String[]{"claysoldiers:textures/entity/soldiers_unique/blue.png"}).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("purple", 8339378, new ItemStack(Items.dye, 1, 5), new String[]{"claysoldiers:textures/entity/soldiers/purple.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/purple.png", "claysoldiers:textures/entity/soldiers_rare/purple2.png"}, new String[]{"claysoldiers:textures/entity/soldiers_unique/purple.png", "claysoldiers:textures/entity/soldiers_unique/purple2.png"}).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("cyan", 5013401, new ItemStack(Items.dye, 1, 6), new String[]{"claysoldiers:textures/entity/soldiers/cyan.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/cyan.png"}, null).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("gray", 0x4C4C4C, new ItemStack(Items.dye, 1, 8), new String[]{"claysoldiers:textures/entity/soldiers/gray.png"}, null, null).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("pink", 15892389, new ItemStack(Items.dye, 1, 9), new String[]{"claysoldiers:textures/entity/soldiers/pink.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/pink.png"}, null).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("lime", 8375321, new ItemStack(Items.dye, 1, 10), new String[]{"claysoldiers:textures/entity/soldiers/lime.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/lime.png"}, null).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("yellow", 0xE5E533, new ItemStack(Items.dye, 1, 11), new String[]{"claysoldiers:textures/entity/soldiers/yellow.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/yellow.png", "claysoldiers:textures/entity/soldiers_rare/yellow2.png", "claysoldiers:textures/entity/soldiers_rare/yellow3.png"}, null).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("lightblue", 6724056, new ItemStack(Items.dye, 1, 12), new String[]{"claysoldiers:textures/entity/soldiers/lightblue.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/lightblue.png"}, null).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("magenta", 0xE000FF, new ItemStack(Items.dye, 1, 13), new String[]{"claysoldiers:textures/entity/soldiers/magenta.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/magenta.png"}, new String[]{"claysoldiers:textures/entity/soldiers_unique/magenta.png"}).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("orange", 14188339, new ItemStack(Items.dye, 1, 14), new String[]{"claysoldiers:textures/entity/soldiers/orange.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/orange.png"}, null).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("white", 0xFFFFFF, new ItemStack(Items.dye, 1, 15), new String[]{"claysoldiers:textures/entity/soldiers/white.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/white.png"}, new String[]{"claysoldiers:textures/entity/soldiers_unique/white.png", "claysoldiers:textures/entity/soldiers_unique/white2.png"}).useTeamColorAsItemColor();
        ClaymanTeam.registerTeam("melon", 9556992, new ItemStack(Blocks.melon_block), "claysoldiers:doll_melon", new String[]{"claysoldiers:textures/entity/soldiers/melon.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/melon.png"}, null);
        ClaymanTeam.registerTeam("pumpkin", 14583049, new ItemStack(Blocks.pumpkin), "claysoldiers:doll_pumpkin", new String[]{"claysoldiers:textures/entity/soldiers/pumpkin.png", "claysoldiers:textures/entity/soldiers/pumpkin2.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/pumpkin.png", "claysoldiers:textures/entity/soldiers_rare/pumpkin2.png"}, new String[]{"claysoldiers:textures/entity/soldiers_unique/pumpkin.png", "claysoldiers:textures/entity/soldiers_unique/pumpkin2.png"});
        ClaymanTeam.registerTeam("coal", 0x252525, new ItemStack(Blocks.torch), "claysoldiers:doll_coal", new String[]{"claysoldiers:textures/entity/soldiers/coal.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/coal.png"}, new String[]{"claysoldiers:textures/entity/soldiers_unique/coal.png"});
        ClaymanTeam.registerTeam("redstone", 14223111, new ItemStack(Blocks.redstone_torch), "claysoldiers:doll_redstone", new String[]{"claysoldiers:textures/entity/soldiers/redstone.png", "claysoldiers:textures/entity/soldiers/redstone2.png"}, new String[]{"claysoldiers:textures/entity/soldiers_rare/redstone.png"}, new String[]{"claysoldiers:textures/entity/soldiers/redstone.png", "claysoldiers:textures/entity/soldiers/redstone2.png"});
    }

    public static ClaymanTeam registerTeam(String teamName, int teamColor, ItemStack teamItem, String[] defTextures, String[] rareTextures, String[] uniqueTextures) {
        return ClaymanTeam.registerTeam(teamName, teamColor, teamItem, "claysoldiers:doll_clay", defTextures, rareTextures, uniqueTextures);
    }

    public static ClaymanTeam registerTeam(String teamName, int teamColor, ItemStack teamItem, String iconTexture, String[] defTextures, String[] rareTextures, String[] uniqueTextures) {
        try {
            ClaymanTeam inst = new ClaymanTeam(teamName, teamColor, teamItem, iconTexture, defTextures, rareTextures, uniqueTextures);
            if (TEAMS_.containsKey(teamName)) {
                FMLLog.log((String)"ClaySoldiers", (Level)Level.WARN, (String)"A mod has overridden the soldier team \"%s\"!", (Object[])new Object[]{teamName});
            } else {
                TEAM_NAMES_FOR_DOLLS_.add(teamName);
            }
            TEAMS_.put(teamName, inst);
            return inst;
        }
        catch (ClaymanTeamRegistrationException ex) {
            FMLLog.log((String)"ClaySoldiers", (Level)Level.ERROR, (String)"There was an error while trying to register the soldier team %s:", (Object[])new Object[]{teamName});
            ex.printStackTrace();
            FMLLog.log((String)"ClaySoldiers", (Level)Level.ERROR, (String)"This team will not be registered!", (Object[])new Object[0]);
            return null;
        }
    }

    public static ClaymanTeam getTeam(String name) {
        return TEAMS_.get(name);
    }

    public static ClaymanTeam getTeam(ItemStack stack) {
        if (stack == null) {
            return NULL_TEAM;
        }
        for (ClaymanTeam team : TEAMS_.values()) {
            if (!SAPUtils.areStacksEqualWithWCV(stack, team.getTeamItem())) continue;
            return team;
        }
        return NULL_TEAM;
    }

    public static List<String> getTeamNamesForDolls() {
        return new ArrayList<String>(TEAM_NAMES_FOR_DOLLS_);
    }

    public static void registerIcons(IIconRegister iconRegister) {
        HashMap registeredIcons = Maps.newHashMap();
        for (ClaymanTeam team : TEAMS_.values()) {
            if (!TEAM_NAMES_FOR_DOLLS_.contains(team.p_name)) continue;
            if (registeredIcons.containsKey(team.p_icon)) {
                team.p_iconInstance = (IIcon)registeredIcons.get(team.p_icon);
                continue;
            }
            team.p_iconInstance = iconRegister.registerIcon(team.p_icon);
            registeredIcons.put(team.p_icon, team.p_iconInstance);
        }
    }

    public ClaymanTeam useTeamColorAsItemColor() {
        this.p_iconColor = this.p_teamColor;
        return this;
    }

    public String getTeamName() {
        return this.p_name;
    }

    public ResourceLocation[] getDefaultTextures() {
        return this.p_texturesDefault;
    }

    public ResourceLocation[] getRareTextures() {
        return this.p_texturesRare;
    }

    public ResourceLocation[] getUniqueTextures() {
        return this.p_texturesUnique;
    }

    public String getIconTexture() {
        return this.p_icon;
    }

    public int getTeamColor() {
        return this.p_teamColor;
    }

    public int getIconColor() {
        return this.p_iconColor;
    }

    public ItemStack getTeamItem() {
        return this.p_teamItem.copy();
    }

    public IIcon getIconInstance() {
        return this.p_iconInstance;
    }

    private static class ClaymanTeamRegistrationException
    extends Exception {
        public ClaymanTeamRegistrationException(String message) {
            super(message);
        }
    }
}

