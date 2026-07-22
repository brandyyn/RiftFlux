/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.util.client.helpers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(value=Side.CLIENT)
public final class ModelBoxBuilder<T extends ModelRenderer> {
    private T box;

    public static ModelBoxBuilder<ModelRenderer> newBuilder(ModelBase model) {
        return ModelBoxBuilder.newBuilder(model, ModelRenderer.class);
    }

    public static <T extends ModelRenderer> ModelBoxBuilder<T> newBuilder(ModelBase model, Class<T> boxClass) {
        return new ModelBoxBuilder<T>(model, boxClass);
    }

    private ModelBoxBuilder(ModelBase model, Class<T> boxClass) {
        try {
            this.box = boxClass.getConstructor(ModelBase.class).newInstance(model);
            ((ModelRenderer)this.box).textureWidth = model.textureWidth;
            ((ModelRenderer)this.box).textureHeight = model.textureHeight;
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Unable to build Model box! Check your inheritance or constructors of your ModelRenderer subclass if you've provided one!", e);
        }
    }

    public ModelBoxBuilder<T> setTexture(int x, int y, boolean mirror) {
        ((ModelRenderer)this.box).setTextureOffset(x, y);
        ((ModelRenderer)this.box).mirror = mirror;
        return this;
    }

    public ModelBoxBuilder<T> setTexture(int x, int y, boolean mirror, float width, float height) {
        ((ModelRenderer)this.box).textureWidth = width;
        ((ModelRenderer)this.box).textureHeight = height;
        ((ModelRenderer)this.box).setTextureOffset(x, y);
        ((ModelRenderer)this.box).mirror = mirror;
        return this;
    }

    public ModelBoxBuilder<T> setLocation(float pointX, float pointY, float pointZ) {
        ((ModelRenderer)this.box).rotationPointX = pointX;
        ((ModelRenderer)this.box).rotationPointY = pointY;
        ((ModelRenderer)this.box).rotationPointZ = pointZ;
        return this;
    }

    public ModelBoxBuilder<T> setRotation(float angleX, float angleY, float angleZ) {
        ((ModelRenderer)this.box).rotateAngleX = angleX;
        ((ModelRenderer)this.box).rotateAngleY = angleY;
        ((ModelRenderer)this.box).rotateAngleZ = angleZ;
        return this;
    }

    public T getBox(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize, float scale) {
        ((ModelRenderer)this.box).addBox(xOffset, yOffset, zOffset, xSize, ySize, zSize, scale);
        return this.box;
    }
}
