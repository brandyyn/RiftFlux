package zairus.worldexplorer.archery.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import zairus.worldexplorer.archery.entity.monster.EntitySkeletonExplorer;

public class ItemSlingshotSkeletonSpawnEgg extends Item {
    private static final int PRIMARY_COLOR = 0xF0F0F0;
    private static final int SECONDARY_COLOR = 0x7CCB68;

    public ItemSlingshotSkeletonSpawnEgg() {
        this.setHasSubtypes(false);
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setTextureName("minecraft:spawn_egg");
        this.setUnlocalizedName("slingshot_skeleton_spawn_egg");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                             int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        ForgeDirection direction = ForgeDirection.getOrientation(side);
        EntitySkeletonExplorer skeleton = new EntitySkeletonExplorer(world);
        skeleton.setLocationAndAngles(
                x + direction.offsetX + 0.5D,
                y + direction.offsetY + 0.2D,
                z + direction.offsetZ + 0.5D,
                world.rand.nextFloat() * 360.0F,
                0.0F
        );
        skeleton.onSpawnWithEgg(null);
        if (stack.hasDisplayName()) {
            skeleton.setCustomNameTag(stack.getDisplayName());
        }
        world.spawnEntityInWorld(skeleton);

        if (player == null || !player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return Items.spawn_egg.getIconFromDamageForRenderPass(0, pass);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return pass == 0 ? PRIMARY_COLOR : SECONDARY_COLOR;
    }
}
