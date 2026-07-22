/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  org.apache.logging.log4j.Level
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.core.manpack.mod.client.render;

import com.google.gson.Gson;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.init.ManPackLoadingPlugin;
import de.sanandrew.core.manpack.mod.client.model.ModelSanPlayer;
import de.sanandrew.core.manpack.util.client.helpers.AverageColorHelper;
import de.sanandrew.core.manpack.util.client.helpers.ItemRenderHelper;
import de.sanandrew.core.manpack.util.client.helpers.ModelBoxBuilder;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderSanPlayer
extends RenderPlayer
implements IResourceManagerReloadListener {
    public static final ResourceLocation TEXTURE = new ResourceLocation("sapmanpack", "textures/entity/player/SanPlayer.png");
    public static final ResourceLocation TEXTURE_SLEEP = new ResourceLocation("sapmanpack", "textures/entity/player/SanPlayer_sleeping.png");
    private ModelSanPlayer myModel = new ModelSanPlayer(0.0f, false);
    private ModelSanPlayer myModelArmor = new ModelSanPlayer(0.05f, true);
    private Map<String, SAPUtils.RGBAValues> unknownTextureColorMap = new HashMap<String, SAPUtils.RGBAValues>();
    private Map<String, CubeLoader> hatRenderList = new HashMap<String, CubeLoader>();

    public RenderSanPlayer() {
        this.mainModel = this.myModel;
        this.modelBipedMain = this.myModel;
        this.modelArmorChestplate = new ModelBiped();
        this.modelArmor = new ModelBiped();
    }

    @Override
    protected int shouldRenderPass(AbstractClientPlayer player, int renderPass, float partTicks) {
        this.myModel.skirt1.showModel = player.inventory.armorItemInSlot(1) == null || !(player.inventory.armorItemInSlot(1).getItem() instanceof ItemArmor);
        this.myModel.skirt2.showModel = this.myModel.skirt1.showModel;
        this.myModel.armRight2.showModel = player.inventory.armorItemInSlot(2) == null || !(player.inventory.armorItemInSlot(2).getItem() instanceof ItemArmor);
        this.myModel.armLeft2.showModel = this.myModel.armRight2.showModel;
        ItemStack stack = player.inventory.armorItemInSlot(3 - renderPass);
        if (renderPass == 0) {
            this.myModel.hideTails = false;
        }
        if (stack != null) {
            Item item = stack.getItem();
            this.myModelArmor.hatBase = null;
            if (item instanceof ItemArmor) {
                ItemArmor armorItem = (ItemArmor)item;
                String unlocName = stack.getUnlocalizedName().replace(':', '_');
                switch (renderPass) {
                    case 0: {
                        this.bindTexture(this.tryLoadArmorPiece("Hat", unlocName, player, stack, renderPass));
                        if (!this.hatRenderList.containsKey(unlocName)) {
                            CubeLoader cubes = CubeLoader.loadFromResource(unlocName);
                            cubes.initCubeInstances(this.myModelArmor);
                            this.hatRenderList.put(unlocName, cubes);
                        }
                        this.myModelArmor.hatBase = this.hatRenderList.get(unlocName).getCubeParent();
                        this.myModel.hideTails = this.hatRenderList.get((Object)unlocName).hideTails;
                        break;
                    }
                    case 1: {
                        this.bindTexture(this.tryLoadArmorPiece("Chest", unlocName, player, stack, renderPass));
                        break;
                    }
                    case 2: {
                        this.bindTexture(this.tryLoadArmorPiece("Leggings", unlocName, player, stack, renderPass));
                        break;
                    }
                    case 3: {
                        this.bindTexture(this.tryLoadArmorPiece("Boots", unlocName, player, stack, renderPass));
                    }
                }
                this.myModelArmor.body.showModel = renderPass == 1 || renderPass == 2;
                this.myModelArmor.bipedLeftArm.showModel = renderPass == 1;
                this.myModelArmor.armLeft2.showModel = renderPass == 1;
                this.myModelArmor.bipedRightArm.showModel = renderPass == 1;
                this.myModelArmor.armRight2.showModel = renderPass == 1;
                this.myModelArmor.skirt1.showModel = renderPass == 2;
                this.myModelArmor.skirt2.showModel = renderPass == 2;
                this.myModelArmor.legLeft.showModel = renderPass == 2 || renderPass == 3;
                this.myModelArmor.legRight.showModel = renderPass == 2 || renderPass == 3;
                this.setRenderPassModel(this.myModelArmor);
                this.myModelArmor.onGround = this.myModel.onGround;
                this.myModelArmor.isRiding = this.myModel.isRiding;
                this.myModelArmor.isChild = this.myModel.isChild;
                this.myModelArmor.isSneak = this.myModel.isSneak;
                this.myModelArmor.aimedBow = this.myModel.aimedBow;
                this.myModelArmor.heldItemLeft = this.myModel.heldItemLeft;
                this.myModelArmor.heldItemRight = this.myModel.heldItemRight;
                int armorColor = armorItem.getColor(stack);
                if (armorColor != -1) {
                    float red = (float)(armorColor >> 16 & 0xFF) / 255.0f;
                    float green = (float)(armorColor >> 8 & 0xFF) / 255.0f;
                    float blue = (float)(armorColor & 0xFF) / 255.0f;
                    GL11.glColor3f((float)red, (float)green, (float)blue);
                } else if (this.unknownTextureColorMap.containsKey(unlocName)) {
                    SAPUtils.RGBAValues rgba = this.unknownTextureColorMap.get(unlocName);
                    GL11.glColor3f((float)((float)rgba.getRed() / 255.0f), (float)((float)rgba.getGreen() / 255.0f), (float)((float)rgba.getBlue() / 255.0f));
                } else {
                    GL11.glColor3f((float)1.0f, (float)1.0f, (float)1.0f);
                }
                if (stack.isItemEnchanted()) {
                    return 15;
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    protected void renderEquippedItems(AbstractClientPlayer player, float partTicks) {
        super.renderEquippedItems(player, partTicks);
        GL11.glPushMatrix();
        this.myModel.body.postRender(0.0625f);
        ItemStack slot = player.inventory.getStackInSlot(0);
        if (slot != null && slot != player.getCurrentEquippedItem()) {
            GL11.glPushMatrix();
            GL11.glRotatef((float)-80.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glScalef((float)0.6f, (float)0.6f, (float)0.6f);
            GL11.glTranslatef((float)-1.0f, (float)-0.4f, (float)0.3f);
            ItemRenderHelper.renderItemIn3D(slot);
            GL11.glPopMatrix();
            GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.05f);
        }
        if ((slot = player.inventory.getStackInSlot(1)) != null && slot != player.getCurrentEquippedItem()) {
            GL11.glPushMatrix();
            GL11.glRotatef((float)-10.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glScalef((float)0.6f, (float)0.6f, (float)0.6f);
            GL11.glTranslatef((float)-0.6f, (float)-0.0f, (float)0.3f);
            ItemRenderHelper.renderItemIn3D(slot);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(AbstractClientPlayer clientPlayer) {
        return clientPlayer.isPlayerSleeping() ? TEXTURE_SLEEP : TEXTURE;
    }

    @Override
    public void renderFirstPersonArm(EntityPlayer player) {
        GL11.glColor3f((float)1.0f, (float)1.0f, (float)1.0f);
        this.modelBipedMain.onGround = 0.0f;
        boolean isRidingPrev = this.modelBipedMain.isRiding;
        this.modelBipedMain.isRiding = false;
        this.modelBipedMain.setRotationAngles(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f, player);
        this.modelBipedMain.isRiding = isRidingPrev;
        if (this.renderManager.renderEngine != null) {
            if (player.getCurrentArmor(2) != null) {
                this.bindTexture(this.getEntityTexture(player));
                this.myModel.bipedLeftArm.render(0.0625f);
                String armoredChest = player.getCurrentArmor(2).getUnlocalizedName().replace(':', '_');
                boolean prevArmR2Visible = this.myModel.armRight2.showModel;
                Minecraft.getMinecraft().getTextureManager().bindTexture(this.tryLoadArmorPiece("Chest", armoredChest, player, player.getCurrentArmor(2), 1));
                this.myModel.bipedRightArm.render(0.0625f);
                GL11.glPushMatrix();
                GL11.glScalef((float)1.05f, (float)1.05f, (float)1.05f);
                GL11.glTranslatef((float)0.015f, (float)0.0f, (float)0.0f);
                int armorColor = ((ItemArmor)player.getCurrentArmor(2).getItem()).getColor(player.getCurrentArmor(2));
                if (armorColor != -1) {
                    float red = (float)(armorColor >> 16 & 0xFF) / 255.0f;
                    float green = (float)(armorColor >> 8 & 0xFF) / 255.0f;
                    float blue = (float)(armorColor & 0xFF) / 255.0f;
                    GL11.glColor3f((float)red, (float)green, (float)blue);
                } else if (this.unknownTextureColorMap.containsKey(armoredChest)) {
                    SAPUtils.RGBAValues rgba = this.unknownTextureColorMap.get(armoredChest);
                    GL11.glColor3f((float)((float)rgba.getRed() / 255.0f), (float)((float)rgba.getGreen() / 255.0f), (float)((float)rgba.getBlue() / 255.0f));
                }
                this.myModel.armRight2.showModel = true;
                this.myModel.armRight2.render(0.0625f);
                this.myModel.armRight2.showModel = prevArmR2Visible;
                GL11.glColor3f((float)1.0f, (float)1.0f, (float)1.0f);
                GL11.glPopMatrix();
            } else {
                boolean prevArmR2Visible = this.myModel.armRight2.showModel;
                this.bindTexture(this.getEntityTexture(player));
                this.myModel.bipedRightArm.render(0.0625f);
                GL11.glPushMatrix();
                GL11.glScalef((float)1.05f, (float)1.05f, (float)1.05f);
                GL11.glTranslatef((float)0.015f, (float)0.0f, (float)0.0f);
                this.myModel.armRight2.showModel = true;
                this.myModel.armRight2.render(0.0625f);
                this.myModel.armRight2.showModel = prevArmR2Visible;
                GL11.glPopMatrix();
            }
        }
    }

    private ResourceLocation tryLoadArmorPiece(String part, String unlocName, EntityPlayer player, ItemStack stack, int pass) {
        ResourceLocation resLoc;
        if (this.unknownTextureColorMap.containsKey(unlocName)) {
            resLoc = new ResourceLocation("sapmanpack", "textures/entity/player/SanPlayer_" + part + "_unknown.png");
        } else {
            resLoc = new ResourceLocation("sapmanpack", "textures/entity/player/SanPlayer_" + part + '_' + unlocName + ".png");
            try {
                Minecraft.getMinecraft().getResourceManager().getResource(resLoc);
            }
            catch (IOException ex) {
                ManPackLoadingPlugin.MOD_LOG.printf(Level.WARN, "Can't load armor texture for item %s!", new Object[]{unlocName});
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(String.format("Can't load armor texture for item %s!", unlocName)));
                resLoc = RenderBiped.getArmorResource((Entity)player, (ItemStack)stack, (int)pass, null);
                try (InputStream textureStream = Minecraft.getMinecraft().getResourceManager().getResource(resLoc).getInputStream();){
                    this.unknownTextureColorMap.put(unlocName, AverageColorHelper.getAverageColor(textureStream));
                }
                catch (IOException ex2) {
                    ManPackLoadingPlugin.MOD_LOG.printf(Level.WARN, "Can't get avg. color for armor texture %s!", new Object[]{unlocName});
                    ManPackLoadingPlugin.MOD_LOG.log(Level.WARN, "", (Throwable)ex2);
                    this.unknownTextureColorMap.put(unlocName, new SAPUtils.RGBAValues(255, 255, 255, 255));
                }
                resLoc = new ResourceLocation("sapmanpack", "textures/entity/player/SanPlayer_" + part + "_unknown.png");
            }
        }
        return resLoc;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resManager) {
        this.unknownTextureColorMap.clear();
        this.hatRenderList.clear();
    }

    public static class CubeLoaderCube {
        public int textureX;
        public int textureY;
        public boolean mirror;
        public float boxX;
        public float boxY;
        public float boxZ;
        public int sizeX;
        public int sizeY;
        public int sizeZ;
        public float rotationPointX;
        public float rotationPointY;
        public float rotationPointZ;
        public float rotationX;
        public float rotationY;
        public float rotationZ;
        public float scale;
    }

    public static class CubeLoader {
        public CubeLoaderCube[] cubes = new CubeLoaderCube[0];
        public boolean hideTails;
        private ModelRenderer[] cubeInsts = new ModelRenderer[1];

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public static CubeLoader loadFromResource(String unlocName) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("sapmanpack", "model/hats/" + unlocName + ".json")).getInputStream()));){
                CubeLoader cubeLoader = (CubeLoader)new Gson().fromJson((Reader)in, CubeLoader.class);
                return cubeLoader;
            }
            catch (IOException ex) {
                ManPackLoadingPlugin.MOD_LOG.printf(Level.WARN, "Can't load hat model for item %s!", new Object[]{unlocName});
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(String.format("Can't load hat model for item %s!", unlocName)));
                return new CubeLoader();
            }
        }

        public void initCubeInstances(ModelBase model) {
            this.cubeInsts = new ModelRenderer[this.cubes.length];
            ModelRenderer parent = null;
            for (int index = 0; index < this.cubes.length; ++index) {
                CubeLoaderCube cubeDef = this.cubes[index];
                this.cubeInsts[index] = ModelBoxBuilder.newBuilder(model).setTexture(cubeDef.textureX, cubeDef.textureY, cubeDef.mirror).setLocation(cubeDef.rotationPointX, cubeDef.rotationPointY, cubeDef.rotationPointZ).setRotation(cubeDef.rotationX, cubeDef.rotationY, cubeDef.rotationZ).getBox(cubeDef.boxX, cubeDef.boxY, cubeDef.boxZ, cubeDef.sizeX, cubeDef.sizeY, cubeDef.sizeZ, cubeDef.scale);
                if (index == 0) {
                    parent = this.cubeInsts[index];
                    continue;
                }
                parent.addChild(this.cubeInsts[index]);
            }
        }

        public ModelRenderer getCubeParent() {
            return this.cubeInsts.length > 0 ? this.cubeInsts[0] : null;
        }
    }
}

