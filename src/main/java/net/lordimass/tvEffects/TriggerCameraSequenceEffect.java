package net.lordimass.tvEffects;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import net.lordimass.assets.CameraSequenceAsset;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;

public class TriggerCameraSequenceEffect extends TriggerEffect {
    @Nonnull public static final BuilderCodec<@NotNull TriggerCameraSequenceEffect> CODEC = BuilderCodec
        .builder(TriggerCameraSequenceEffect.class, TriggerCameraSequenceEffect::new, TriggerEffect.BASE_CODEC)
        .append(new KeyedCodec<>("CameraSequence", Codec.STRING),
            (s, v) -> s.cameraSequenceKey = v,
            TriggerCameraSequenceEffect::getCameraSequenceKey
            )
        .documentation("The ID of a CameraSequence asset to play.")
        .addValidator(CameraSequenceAsset.VALIDATOR_CACHE.getValidator())
        .add()
        .build();

    @Getter private String cameraSequenceKey;

    public TriggerCameraSequenceEffect() {}

    @Override
    public void execute(@NonNull TriggerContext triggerContext) {
        Ref<EntityStore> ref = triggerContext.getEntityRef();
        if (ref == null) return;
        PlayerRef playerRef = ref.getStore().getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;
        CameraSequenceAsset cameraSequenceAsset = CameraSequenceAsset.getAssetMap().getAsset(cameraSequenceKey);
        if (cameraSequenceAsset == null) return;
        cameraSequenceAsset.play(playerRef);
    }
}
