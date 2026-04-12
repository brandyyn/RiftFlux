package com.voidsrift.riftflux.mixin.late.musicchoices;

import com.tmtravlr.musicchoices.MChHelper;
import com.tmtravlr.musicchoices.MusicChoicesEventHandler;
import com.tmtravlr.musicchoices.MusicChoicesMusicTicker;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = MusicChoicesEventHandler.class, remap = false)
public class MixinMusicChoicesEventHandler_NullSound {

    /**
     * @author Codex
     * @reason Music Choices assumes record events always carry a sound result, but Forge can post
     * this event with a null result. Preserve the original behavior while skipping those null
     * cases.
     */
    @Overwrite
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onSound(PlaySoundEvent17 event) {
        ISound sound = event.result;
        if (sound == null) {
            return;
        }
        if ((event.category == SoundCategory.MUSIC || event.category == SoundCategory.RECORDS)
                && !sound.getPositionedSoundLocation().toString().contains("note.")
                && !MChHelper.isSoundTracked(sound)) {
            MusicChoicesMusicTicker.ticker.setOvertopMusic(sound, null);
        }
    }
}
