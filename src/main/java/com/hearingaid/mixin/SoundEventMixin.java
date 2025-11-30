package com.hearingaid.mixin;

import com.hearingaid.config.HearingAidConfig;

import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
// import net.minecraft.client.world.ClientWorld;
// import net.minecraft.client.network.ClientPlayNetworkHandler;


import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEvent.class)
public abstract class SoundEventMixin {

    @Shadow private Optional<Float> fixedRange;

    @Inject(method = "getDistanceToTravel(F)F", at = @At("HEAD"), cancellable = true)
    private void overrideDistance(float volume, CallbackInfoReturnable<Float> cir) {
        SoundEvent self = (SoundEvent)(Object)this;

        // vanilla functionality with config override
        if (fixedRange.isPresent()) {
            // get fixed range
            cir.setReturnValue(HearingAidConfig.getFixedRange(self));
        } else {
            // get and calcualte volume-based range
            float distance = HearingAidConfig.getVolumeBaseRange(self, volume);
            cir.setReturnValue(distance);
        }
    }
}
