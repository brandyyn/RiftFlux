package gravestone.entity.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityWitherSkullCrawler extends EntitySkullCrawler {
   public EntityWitherSkullCrawler(World world) {
      super(world);
      this.isImmuneToFire = true;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2.0D);
   }

   protected void dropRareDrop(int par1) {
      this.entityDropItem(new ItemStack(Items.skull, 1, 1), 0.0F);
   }

   protected PotionEffect getPotionEffect() {
      return new PotionEffect(Potion.wither.id, 100);
   }

   protected void silverfishLikeBehaviour() {
   }
}
