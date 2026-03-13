/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.ritual;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.nmccoy.legendgear.block.TileEntityRitual;
import net.nmccoy.legendgear.ritual.RecipeComponent;
import net.nmccoy.legendgear.ritual.RitualRecipe;

public abstract class Ritual {
    public RitualRecipe components;
    public String unlocalizedName;

    public Ritual(String name, RitualRecipe parts) {
        this.components = parts;
        this.unlocalizedName = name;
    }

    public Ritual() {
        this.components = new RitualRecipe();
        this.unlocalizedName = "someRitual";
    }

    public boolean accepts(RitualRecipe ingredients) {
        return this.components.accepts(ingredients);
    }

    public abstract boolean invoke(RitualRecipe var1, TileEntityRitual var2, EntityPlayer var3);

    public List<Entity> filterFocus(Object filter, TileEntityRitual location) {
        ArrayList<Entity> entities = new ArrayList();
        if (filter instanceof Class && Entity.class.isAssignableFrom((Class)filter)) {
            Class c = (Class)filter;
            entities = new ArrayList<Entity>(location.targetsInRitual(c));
            return entities;
        }
        Block blockFocus = location.focusBlock();
        List<EntityItem> items = location.itemsInRitual();
        for (EntityItem item : items) {
            Item itemFilter;
            ItemStack stackFilter;
            ItemStack stack = item.getEntityItem();
            if (filter instanceof ItemStack && (stackFilter = (ItemStack)filter).getItem() == stack.getItem() && stackFilter.getItemDamage() == stack.getItemDamage()) {
                entities.add((Entity)item);
            }
            if (filter instanceof Item && (itemFilter = (Item)filter) == stack.getItem()) {
                entities.add((Entity)item);
            }
            if (!(filter instanceof Block)) continue;
            Block blockFilter = (Block)filter;
            if (!(stack.getItem() instanceof ItemBlock) || Block.getBlockFromItem((Item)stack.getItem()) != blockFilter) continue;
            entities.add((Entity)item);
        }
        if (filter instanceof Block) {
            if (entities.size() > 0 && blockFocus == Blocks.air) {
                return entities;
            }
            Block blockFilter = (Block)filter;
            if (blockFilter == blockFocus) {
                return entities;
            }
            return null;
        }
        return entities;
    }

    public static class Summoning
    extends Ritual {
        public Class<? extends EntityLiving> creatureClass;
        public Object focusFilter;

        public Summoning(String name, Class<? extends EntityLiving> creature, RecipeComponent identifiers, Object focusFilter) {
            this(name, creature, new RitualRecipe().add(identifiers).add(new RecipeComponent(Blocks.emerald_block)), focusFilter);
        }

        public Summoning(String name, Class<? extends EntityLiving> creature, RitualRecipe recipe, Object focusFilter) {
            this.components = recipe;
            this.creatureClass = creature;
            this.focusFilter = focusFilter;
            this.unlocalizedName = name;
        }

        @Override
        public boolean invoke(RitualRecipe ingredients, TileEntityRitual location, EntityPlayer caster) {
            List<Entity> targets = this.filterFocus(this.focusFilter, location);
            System.out.println(targets);
            if (targets == null) {
                return false;
            }
            if (!this.components.accepts(ingredients)) {
                return false;
            }
            if (targets.size() > 0) {
                targets.get(0).setDead();
            } else if (this.focusFilter instanceof Block) {
                location.clearFocusBlock();
            } else {
                return false;
            }
            EntityLiving summoned = null;
            try {
                summoned = this.creatureClass.getConstructor(World.class).newInstance(location.getWorldObj());
            }
            catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
            if (summoned == null) {
                return false;
            }
            summoned.onSpawnWithEgg(null);
            summoned.setPosition((double)location.xCoord + 0.5, (double)(location.yCoord + 1), (double)location.zCoord + 0.5);
            location.getWorldObj().spawnEntityInWorld((Entity)summoned);
            return true;
        }
    }
}
