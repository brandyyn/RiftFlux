package com.voidsrift.riftflux.mixin.late.thaumcraft;

import com.voidsrift.riftflux.ModConfig;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.client.gui.GuiResearchBrowser;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.research.PlayerKnowledge;

@Mixin(value = GuiResearchBrowser.class, remap = false)
public abstract class MixinGuiResearchBrowser_WarpOnlyResearchText {
    @Shadow
    private ResearchItem currentHighlight;

    @Redirect(
            method = "genResearchBackground",
            at = @At(
                    value = "FIELD",
                    target = "Lthaumcraft/common/config/Config;researchDifficulty:I",
                    opcode = Opcodes.GETSTATIC,
                    ordinal = 4
            ),
            remap = false
    )
    private int riftflux$useOriginalResearchTextForWarpTooltipFirstCheck() {
        return this.riftflux$getDifficultyForHighlightedResearch();
    }

    @Redirect(
            method = "genResearchBackground",
            at = @At(
                    value = "FIELD",
                    target = "Lthaumcraft/common/config/Config;researchDifficulty:I",
                    opcode = Opcodes.GETSTATIC,
                    ordinal = 5
            ),
            remap = false
    )
    private int riftflux$useOriginalResearchTextForWarpTooltipSecondCheck() {
        return this.riftflux$getDifficultyForHighlightedResearch();
    }

    @Redirect(
            method = "mouseClicked",
            at = @At(
                    value = "FIELD",
                    target = "Lthaumcraft/common/config/Config;researchDifficulty:I",
                    opcode = Opcodes.GETSTATIC,
                    ordinal = 0
            ),
            remap = false
    )
    private int riftflux$useResearchNoteClickPathForWarpFirstCheck() {
        return this.riftflux$getDifficultyForHighlightedResearch();
    }

    @Redirect(
            method = "mouseClicked",
            at = @At(
                    value = "FIELD",
                    target = "Lthaumcraft/common/config/Config;researchDifficulty:I",
                    opcode = Opcodes.GETSTATIC,
                    ordinal = 1
            ),
            remap = false
    )
    private int riftflux$useResearchNoteClickPathForWarpSecondCheck() {
        return this.riftflux$getDifficultyForHighlightedResearch();
    }

    @Redirect(
            method = "mouseClicked",
            at = @At(
                    value = "INVOKE",
                    target = "Lthaumcraft/common/lib/research/PlayerKnowledge;getAspectPoolFor(Ljava/lang/String;Lthaumcraft/api/aspects/Aspect;)S"
            ),
            remap = false
    )
    private short riftflux$allowWarpNoteWithoutAspectPool(PlayerKnowledge knowledge, String player, Aspect aspect) {
        if (this.riftflux$isWarpResearchNotePath()) {
            return Short.MAX_VALUE;
        }
        return knowledge.getAspectPoolFor(player, aspect);
    }

    private int riftflux$getDifficultyForHighlightedResearch() {
        if (this.riftflux$isWarpResearchNotePath()) {
            return 1;
        }
        return Config.researchDifficulty;
    }

    private boolean riftflux$isWarpResearchNotePath() {
        return ModConfig.thaumcraftOnlyWarpResearchRequiresMinigame
                && this.currentHighlight != null
                && ThaumcraftApi.getWarp(riftflux$cleanResearchKey(this.currentHighlight.key)) > 0;
    }

    private static String riftflux$cleanResearchKey(String key) {
        if (key == null) {
            return null;
        }
        return key.startsWith("@") ? key.substring(1) : key;
    }
}
