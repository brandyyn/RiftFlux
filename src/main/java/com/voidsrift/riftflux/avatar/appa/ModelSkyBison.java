package com.voidsrift.riftflux.avatar.appa;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import com.voidsrift.riftflux.mixin.accessor.ModelBoxAccessor;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class ModelSkyBison extends ModelBase {
    private static final String CEM_RESOURCE = "/assets/riftflux/cem/sky_bison.jem";
    private static final Gson GSON = new Gson();

    private final ModelRenderer root;
    private final ModelRenderer head;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer leg4;
    private final ModelRenderer leg5;
    private final ModelRenderer leg6;
    private final ModelRenderer tail;
    private final ModelRenderer tail2;
    private static final Map<Entity, Float> PREV_HEAD_YAW =
            Collections.synchronizedMap(new WeakHashMap<Entity, Float>());
    private static final Map<Entity, Float> PREV_SPEED =
            Collections.synchronizedMap(new WeakHashMap<Entity, Float>());
    private static final Map<Entity, Float> PREV_PHASE =
            Collections.synchronizedMap(new WeakHashMap<Entity, Float>());

    public ModelSkyBison() {
        CemBuildResult result = loadCem();
        this.root = result.root;
        this.head = result.parts.get("head");
        this.leg1 = result.parts.get("leg1");
        this.leg2 = result.parts.get("leg2");
        this.leg3 = result.parts.get("leg3");
        this.leg4 = result.parts.get("leg4");
        this.leg5 = result.parts.get("leg5");
        this.leg6 = result.parts.get("leg6");
        this.tail = result.parts.get("tail");
        this.tail2 = result.parts.get("tail2");
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        if (root == null) {
            return;
        }
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scale, Entity entity) {
        if (head != null) {
            head.rotateAngleX = 0.0f;
        }

        float dx = (float) (entity.posX - entity.prevPosX);
        float dz = (float) (entity.posZ - entity.prevPosZ);
        float moveMag = (float) Math.sqrt(dx * dx + dz * dz);
        boolean riding = entity.riddenByEntity != null;
        float smoothSpeed;
        if (riding) {
            // Keep a steady stride while mounted to avoid stop-start leg jitter.
            smoothSpeed = 1.0f;
            PREV_SPEED.put(entity, smoothSpeed);
        } else {
            float limbSpeed = limbSwingAmount;
            boolean useMotion = limbSpeed < 0.001f;
            float speedScale = useMotion ? Math.min(moveMag * 20.0f, 1.0f) : limbSpeed;
            float prevSpeed = PREV_SPEED.containsKey(entity) ? PREV_SPEED.get(entity) : speedScale;
            smoothSpeed = prevSpeed + (speedScale - prevSpeed) * 0.2f;
            PREV_SPEED.put(entity, smoothSpeed);
        }

        float offset = getRandomOffset(entity);
        float baseTime = (ageInTicks / 6.0f) + offset;
        PREV_PHASE.put(entity, baseTime);

        float speedClamp = smoothSpeed > 0.001f ? clamp(0.05f * smoothSpeed, 0.02f, 0.1f) : 0.0f;
        float legBase = smoothSpeed > 0.001f ? (float) Math.toRadians(15.0f * smoothSpeed) : 0.0f;
        if (leg1 != null) {
            leg1.rotateAngleZ = MathHelper.sin(baseTime) * speedClamp + legBase;
        }
        if (leg2 != null) {
            leg2.rotateAngleZ = MathHelper.sin(baseTime + (float) Math.PI / 3.0f) * speedClamp + legBase;
        }
        if (leg3 != null) {
            leg3.rotateAngleZ = MathHelper.sin(baseTime + (float) (2.0f * Math.PI / 3.0f)) * speedClamp + legBase;
        }
        if (leg4 != null) {
            leg4.rotateAngleZ = MathHelper.sin(baseTime + (float) (3.0f * Math.PI / 3.0f)) * speedClamp + legBase;
        }
        if (leg5 != null) {
            leg5.rotateAngleZ = MathHelper.sin(baseTime + (float) (4.0f * Math.PI / 3.0f)) * speedClamp + legBase;
        }
        if (leg6 != null) {
            leg6.rotateAngleZ = MathHelper.sin(baseTime + (float) (6.0f * Math.PI / 3.0f)) * speedClamp + legBase;
        }

        if (head != null) {
            float turning = MathHelper.wrapAngleTo180_float(entity.rotationYaw - entity.prevRotationYaw);
            float targetYaw = 0.0f;
            if (Math.abs(turning) > 8.0f) {
                float direction = turning > 0.0f ? 1.0f : -1.0f;
                targetYaw = (float) Math.toRadians(20.0f * direction);
            }
            float frameTime = ageInTicks - (float) entity.ticksExisted;
            if (frameTime < 0.0f) {
                frameTime = 0.0f;
            }
            float lerpAmount = Math.min(1.0f, 3.0f * frameTime);
            if (Math.abs(turning) <= 8.0f) {
                PREV_HEAD_YAW.put(entity, 0.0f);
                head.rotateAngleY = 0.0f;
            } else {
                float lastHead = PREV_HEAD_YAW.containsKey(entity) ? PREV_HEAD_YAW.get(entity) : targetYaw;
                float headYaw = lastHead + (targetYaw - lastHead) * lerpAmount;
                PREV_HEAD_YAW.put(entity, headYaw);
                head.rotateAngleY = headYaw;
            }
        }

        if (tail != null) {
            float limbTime = (ageInTicks / 6.0f) + getRandomOffset(entity);
            tail.rotateAngleZ = (MathHelper.sin(limbTime) / 8.0f) + (float) Math.toRadians(-32.0f);
            if (tail2 != null) {
                tail2.rotateAngleZ = (-MathHelper.cos(limbTime) / 8.0f) + (float) Math.toRadians(20.0f);
            }
        }
        if (tail != null && entity instanceof EntityBison) {
            tail.rotateAngleZ += (float) Math.toRadians(((EntityBison) entity).tailAngle);
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float normalizeAngle(float angle) {
        while (angle < -180.0f) {
            angle += 360.0f;
        }
        while (angle >= 180.0f) {
            angle -= 360.0f;
        }
        return angle;
    }

    private static float getRandomOffset(Entity entity) {
        if (entity == null) {
            return 0.0f;
        }
        float seed = entity.getEntityId() * 100.0f;
        float rnd = MathHelper.sin(seed * 12.9898f) * 43758.5453f;
        rnd = rnd - (float) Math.floor(rnd);
        return rnd * (float) Math.PI * 2.0f;
    }

    private CemBuildResult loadCem() {
        InputStream stream = ModelSkyBison.class.getResourceAsStream(CEM_RESOURCE);
        if (stream == null) {
            return CemBuildResult.empty();
        }
        CemRoot cem = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), CemRoot.class);
        if (cem == null || cem.models == null) {
            return CemBuildResult.empty();
        }
        CemPart bisonPart = findById(cem.models, "body");
        if (bisonPart == null) {
            bisonPart = findById(cem.models, "bison");
        }
        if (bisonPart == null) {
            return CemBuildResult.empty();
        }
        int texWidth = cem.textureSize != null && cem.textureSize.length > 0 ? cem.textureSize[0] : 64;
        int texHeight = cem.textureSize != null && cem.textureSize.length > 1 ? cem.textureSize[1] : 64;
        CemBuildResult result = new CemBuildResult();
        result.parts = new HashMap<String, ModelRenderer>();
        result.root = buildPart(this, bisonPart, texWidth, texHeight, result.parts);
        return result;
    }

    private static CemPart findById(List<CemPart> parts, String id) {
        if (parts == null) {
            return null;
        }
        for (CemPart part : parts) {
            if (id.equals(part.id)) {
                return part;
            }
            CemPart child = findById(part.submodels, id);
            if (child != null) {
                return child;
            }
        }
        return null;
    }

    private static ModelRenderer buildPart(ModelBase base, CemPart part, int texWidth, int texHeight,
                                           Map<String, ModelRenderer> parts) {
        int texU = 0;
        int texV = 0;
        ModelRenderer model = new ModelRenderer(base, texU, texV);
        model.setTextureSize(texWidth, texHeight);

        boolean invX = part.invertAxis != null && part.invertAxis.contains("x");
        boolean invY = part.invertAxis != null && part.invertAxis.contains("y");
        boolean invZ = part.invertAxis != null && part.invertAxis.contains("z");

        float tx = get(part.translate, 0);
        float ty = get(part.translate, 1);
        float tz = get(part.translate, 2);
        if (invX) {
            tx = -tx;
        }
        if (invY) {
            ty = -ty;
        }
        if (invZ) {
            tz = -tz;
        }
        model.setRotationPoint(tx, ty, tz);

        float rx = (float) Math.toRadians(get(part.rotate, 0));
        float ry = (float) Math.toRadians(get(part.rotate, 1));
        float rz = (float) Math.toRadians(get(part.rotate, 2));
        if (invX) {
            ry = -ry;
            rz = -rz;
        }
        if (invY) {
            rx = -rx;
            rz = -rz;
        }
        if (invZ) {
            rx = -rx;
            ry = -ry;
        }
        model.rotateAngleX = rx;
        model.rotateAngleY = ry;
        model.rotateAngleZ = rz;

        if (part.boxes != null) {
            for (CemBox box : part.boxes) {
                if (box.coordinates == null || box.coordinates.length < 6) {
                    continue;
                }
                float x = box.coordinates[0];
                float y = box.coordinates[1];
                float z = box.coordinates[2];
                int dx = Math.round(box.coordinates[3]);
                int dy = Math.round(box.coordinates[4]);
                int dz = Math.round(box.coordinates[5]);

                float expand = box.sizeAdd != null ? box.sizeAdd : 0.0f;
                float expandX = box.sizeAddX != null ? box.sizeAddX : expand;
                float expandY = box.sizeAddY != null ? box.sizeAddY : expand;
                float expandZ = box.sizeAddZ != null ? box.sizeAddZ : expand;
                float uniformExpand = Math.max(expand, Math.max(expandX, Math.max(expandY, expandZ)));

                if (invX) {
                    x = -x - dx;
                }
                if (invY) {
                    y = -y - dy;
                }
                if (invZ) {
                    z = -z - dz;
                }

                int[] offset = box.textureOffset;
                if (offset == null && box.uvNorth != null && box.uvNorth.length >= 2) {
                    offset = new int[]{Math.round(box.uvNorth[0]), Math.round(box.uvNorth[1])};
                }

                boolean hasFaceUvs = hasPerFaceUvs(box);
                if (hasFaceUvs) {
                    float[] uvEast = box.uvEast;
                    float[] uvWest = box.uvWest;
                    float[] uvUp = box.uvUp;
                    float[] uvDown = box.uvDown;
                    float[] uvNorth = box.uvNorth;
                    float[] uvSouth = box.uvSouth;
                    if (invX) {
                        float[] swap = uvEast;
                        uvEast = uvWest;
                        uvWest = swap;
                    }
                    if (invY) {
                        float[] swap = uvUp;
                        uvUp = uvDown;
                        uvDown = swap;
                    }
                    if (invZ) {
                        float[] swap = uvNorth;
                        uvNorth = uvSouth;
                        uvSouth = swap;
                    }
                    ModelBox modelBox = new ModelBox(model, 0, 0, x, y, z, dx, dy, dz, uniformExpand);
                    applyFaceUvs(modelBox, uvEast, uvWest, uvDown, uvUp, uvNorth, uvSouth, texWidth, texHeight);
                    model.cubeList.add(modelBox);
                } else {
                    if (offset != null) {
                        model.setTextureOffset(offset[0], offset[1]);
                    }
                    model.addBox(x, y, z, dx, dy, dz, uniformExpand);
                }
            }
        }

        if (part.id != null) {
            parts.put(part.id, model);
        }

        if (part.submodels != null) {
            for (CemPart child : part.submodels) {
                ModelRenderer childModel = buildPart(base, child, texWidth, texHeight, parts);
                model.addChild(childModel);
            }
        }

        return model;
    }

    private static boolean hasPerFaceUvs(CemBox box) {
        return (box.uvNorth != null && box.uvNorth.length >= 4)
                || (box.uvEast != null && box.uvEast.length >= 4)
                || (box.uvSouth != null && box.uvSouth.length >= 4)
                || (box.uvWest != null && box.uvWest.length >= 4)
                || (box.uvUp != null && box.uvUp.length >= 4)
                || (box.uvDown != null && box.uvDown.length >= 4);
    }

    private static void applyFaceUvs(ModelBox box,
                                     float[] uvEast,
                                     float[] uvWest,
                                     float[] uvDown,
                                     float[] uvUp,
                                     float[] uvNorth,
                                     float[] uvSouth,
                                     int texWidth,
                                     int texHeight) {
        ModelBoxAccessor accessor = (ModelBoxAccessor) box;
        PositionTextureVertex[] vertices = accessor.riftflux$getVertexPositions();
        TexturedQuad[] quads = accessor.riftflux$getQuadList();
        if (vertices == null || quads == null || quads.length < 6) {
            return;
        }
        PositionTextureVertex[] v = vertices;

        if (uvEast != null && uvEast.length >= 4) {
            quads[0] = makeQuad(new PositionTextureVertex[]{v[5], v[1], v[2], v[6]}, uvEast, texWidth, texHeight);
        }
        if (uvWest != null && uvWest.length >= 4) {
            quads[1] = makeQuad(new PositionTextureVertex[]{v[0], v[4], v[7], v[3]}, uvWest, texWidth, texHeight);
        }
        if (uvDown != null && uvDown.length >= 4) {
            quads[2] = makeQuad(new PositionTextureVertex[]{v[5], v[4], v[0], v[1]}, uvDown, texWidth, texHeight);
        }
        if (uvUp != null && uvUp.length >= 4) {
            quads[3] = makeQuad(new PositionTextureVertex[]{v[2], v[3], v[7], v[6]}, uvUp, texWidth, texHeight);
        }
        if (uvNorth != null && uvNorth.length >= 4) {
            quads[4] = makeQuad(new PositionTextureVertex[]{v[1], v[0], v[3], v[2]}, uvNorth, texWidth, texHeight);
        }
        if (uvSouth != null && uvSouth.length >= 4) {
            quads[5] = makeQuad(new PositionTextureVertex[]{v[4], v[5], v[6], v[7]}, uvSouth, texWidth, texHeight);
        }
        accessor.riftflux$setQuadList(quads);
    }

    private static TexturedQuad makeQuad(PositionTextureVertex[] verts, float[] uv, int texWidth, int texHeight) {
        int u1 = Math.round(uv[0]);
        int v1 = Math.round(uv[1]);
        int u2 = Math.round(uv[2]);
        int v2 = Math.round(uv[3]);
        return new TexturedQuad(verts, u1, v1, u2, v2, texWidth, texHeight);
    }

    private static float get(float[] values, int idx) {
        if (values == null || values.length <= idx) {
            return 0.0f;
        }
        return values[idx];
    }

    private static final class CemBuildResult {
        ModelRenderer root;
        Map<String, ModelRenderer> parts;

        static CemBuildResult empty() {
            CemBuildResult result = new CemBuildResult();
            result.root = null;
            result.parts = new HashMap<String, ModelRenderer>();
            return result;
        }
    }

    private static final class CemRoot {
        int[] textureSize;
        List<CemPart> models;
    }

    private static final class CemPart {
        String id;
        String part;
        String invertAxis;
        float[] translate;
        float[] rotate;
        List<CemBox> boxes;
        List<CemPart> submodels;
    }

    private static final class CemBox {
        float[] coordinates;
        int[] textureOffset;
        @SerializedName("sizeAdd")
        Float sizeAdd;
        @SerializedName("sizeAddX")
        Float sizeAddX;
        @SerializedName("sizeAddY")
        Float sizeAddY;
        @SerializedName("sizeAddZ")
        Float sizeAddZ;
        float[] uvNorth;
        float[] uvEast;
        float[] uvSouth;
        float[] uvWest;
        float[] uvUp;
        float[] uvDown;
    }
}
