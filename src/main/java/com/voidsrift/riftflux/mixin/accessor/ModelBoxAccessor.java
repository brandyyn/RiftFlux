package com.voidsrift.riftflux.mixin.accessor;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelBox.class)
public interface ModelBoxAccessor {
    @Accessor("vertexPositions")
    PositionTextureVertex[] riftflux$getVertexPositions();

    @Accessor("quadList")
    TexturedQuad[] riftflux$getQuadList();

    @Accessor("quadList")
    void riftflux$setQuadList(TexturedQuad[] quads);
}
