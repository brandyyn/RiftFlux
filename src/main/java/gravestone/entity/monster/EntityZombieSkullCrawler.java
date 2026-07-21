package gravestone.entity.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityZombieSkullCrawler extends EntitySkullCrawler {
   public EntityZombieSkullCrawler(World world) {
      super(world);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2.0D);
   }

   protected String getLivingSound() {
      return "mob.zombie.say";
   }

   protected String getHurtSound() {
      return "mob.zombie.hurt";
   }

   protected String getDeathSound() {
      return "mob.zombie.death";
   }

   protected Item getDropItem() {
      return Items.rotten_flesh;
   }

   protected void dropRareDrop(int par1) {
      this.entityDropItem(new ItemStack(Items.skull, 1, 2), 0.0F);
   }

   protected PotionEffect getPotionEffect() {
      return new PotionEffect(Potion.hunger.id, 200);
   }

   protected void silverfishLikeBehaviour() {
   }
}
