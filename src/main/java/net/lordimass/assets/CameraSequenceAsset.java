package net.lordimass.assets;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.camera.CameraKeyframeBuilder;
import com.hypixel.hytale.server.core.modules.camera.CameraSequenceBuilder;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import lombok.Getter;

public class CameraSequenceAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, CameraSequenceAsset>> {
    public static final String ASSET_PATH = "CameraSequence";

    private static AssetStore<String, CameraSequenceAsset, DefaultAssetMap<String, CameraSequenceAsset>> assetStore;

    @Nonnull public static final AssetBuilderCodec<String, @NotNull CameraSequenceAsset> CODEC =
        AssetBuilderCodec.builder(
            CameraSequenceAsset.class,
            CameraSequenceAsset::new,
            Codec.STRING,
            (asset, id) -> asset.id = id,
            CameraSequenceAsset::getId,
            (asset, data) -> asset.data = data,
            asset -> asset.data
        )
            .append(new KeyedCodec<>("Keyframes", new ArrayCodec<>(CameraKeyframe.CODEC, CameraKeyframe[]::new)),
                (asset, keyframes) -> asset.cameraKeyframes = keyframes,
                CameraSequenceAsset::getCameraKeyframes
            )
            .addValidator(Validators.nonNull())
            .addValidator(Validators.nonEmptyArray())
            .add()
            .append(new KeyedCodec<>("BaseFov", Codec.FLOAT),
                (asset, baseFov) -> asset.baseFov = baseFov,
                CameraSequenceAsset::getBaseFov
            )
            .add()
            .append(new KeyedCodec<>("HideUi", Codec.BOOLEAN),
                (asset, hideUI) -> asset.hideUI = hideUI,
                CameraSequenceAsset::isHideUI
            )
            .documentation("Whether the rest of the UI should be hidden while the effect is playing.")
            .add()
            .build();

    private AssetExtraInfo.Data data;
    @Getter private String id;
    @Getter private CameraKeyframe[] cameraKeyframes = new CameraKeyframe[0];
    @Getter private Float baseFov;
    @Getter private boolean hideUI;

    public CameraSequenceAsset() {}

    public static AssetStore<String, CameraSequenceAsset, DefaultAssetMap<String, CameraSequenceAsset>> getAssetStore() {
        if (assetStore == null) assetStore = AssetRegistry.getAssetStore(CameraSequenceAsset.class);
        return assetStore;
    }

    public static DefaultAssetMap<String, CameraSequenceAsset> getAssetMap() {
        return getAssetStore().getAssetMap();
    }

    public static void register(JavaPlugin host) {
        host.getAssetRegistry().register(
            HytaleAssetStore.builder(CameraSequenceAsset.class, new DefaultAssetMap<>())
                .setPath(ASSET_PATH)
                .setCodec(CODEC)
                .setKeyFunction(CameraSequenceAsset::getId)
                .build()
        );

        CameraKeyframe.CODEC.register(
            "Keyframe",
            CameraKeyframe.Keyframe.class,
            CameraKeyframe.Keyframe.CODEC);

        CameraKeyframe.CODEC.register(
            "KeyframeLookingAt",
            CameraKeyframe.KeyframeLookingAt.class,
            CameraKeyframe.KeyframeLookingAt.CODEC);
    }

    /**
     * Build the camera sequence builder object with all the keyframes.
     * WARNING: If <code>HideUI</code> is enabled, this will not take effect here.
     */
    public CameraSequenceBuilder buildSequenceBuilder(@Nullable PlayerRef playerRef, @Nullable Consumer<PlayerRef> onComplete) {
        CameraSequenceBuilder seqBuilder = new CameraSequenceBuilder()
            .baseFov(baseFov)
            .onComplete((playerRef1) -> {
                if (onComplete != null) onComplete.accept(playerRef1);
                showUI(playerRef1);
            });

        // Add keyframes
        for (CameraKeyframe keyframe : cameraKeyframes) {
            Vector3d position = keyframe.isRelativeToPlayer() && playerRef != null
                ? new Vector3d(playerRef.getTransform().getPosition()).add(keyframe.getPosition())
                : keyframe.getPosition();
            if (keyframe instanceof CameraKeyframe.Keyframe) {
                Rotation3f look = keyframe.isRelativeToPlayer() && playerRef != null
                    ? new Rotation3f(playerRef.getHeadRotation()).add(((CameraKeyframe.Keyframe) keyframe).getLookRadians())
                    : ((CameraKeyframe.Keyframe) keyframe).getLookRadians();
                look.x = ((CameraKeyframe.Keyframe) keyframe).getLookRadians().x; // Pitch should never be relative.
                look.z = ((CameraKeyframe.Keyframe) keyframe).getLookRadians().z; // Roll should never be relative.
                float yaw = playerRef != null ? playerRef.getHeadRotation().yaw() : 0;
                position = keyframe.isRelativeToPlayer() && playerRef != null
                    ? new Vector3d(playerRef.getTransform().getPosition()).add(new Vector3d(
                    -keyframe.getPosition().x*Math.sin(yaw) + keyframe.getPosition().z*Math.cos(yaw),
                    keyframe.getPosition().y,
                    -keyframe.getPosition().x*Math.cos(yaw) - keyframe.getPosition().z*Math.sin(yaw)
                )) : keyframe.getPosition();
                seqBuilder = seqBuilder.keyframe(
                    new CameraKeyframeBuilder(
                        keyframe.getDurationSeconds(),
                        keyframe.getEasing()
                    )
                    .position(position)
                    .look(look)
                    .fov(keyframe.getFov())
                );
            } else if (keyframe instanceof CameraKeyframe.KeyframeLookingAt) {
                Vector3d lookAtPoint = keyframe.isRelativeToPlayer() && playerRef != null
                    ? playerRef.getTransform().getPosition().add(((CameraKeyframe.KeyframeLookingAt) keyframe).getLookAtPoint())
                    : ((CameraKeyframe.KeyframeLookingAt) keyframe).getLookAtPoint();
                seqBuilder = seqBuilder.keyframe(
                    new CameraKeyframeBuilder(
                        keyframe.getDurationSeconds(),
                        keyframe.getEasing()
                    )
                    .position(position)
                    .lookAt(lookAtPoint)
                    .fov(keyframe.getFov())
                );
            }
        }
        // Add flags to return camera control to player afterwards.
        return seqBuilder.addFlag((byte)1).addFlag((byte)2).addFlag((byte)4);
    }

    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(new AssetKeyValidator<>(CameraSequenceAsset::getAssetStore));


    /**
     * Build the camera sequence builder object with all the keyframes.
     * WARNING: If <code>HideUI</code> is enabled, this will not take effect here.
     */
    public CameraSequenceBuilder buildSequenceBuilder(@Nullable PlayerRef playerRef) {
        return buildSequenceBuilder(playerRef, null);
    }

    public void play(@Nonnull PlayerRef playerRef, @Nullable Consumer<PlayerRef> onComplete) {
        if (hideUI) hideUI(playerRef);
        buildSequenceBuilder(playerRef, onComplete).sendTo(playerRef);
    }

    public void play(@Nonnull PlayerRef playerRef) {
        play(playerRef, null);
    }

    private void hideUI(PlayerRef playerRef) {
        // Hide UI if enabled
        UUID worldUUID = playerRef.getWorldUuid();
        if (worldUUID == null) return;
        World world = Universe.get().getWorld(playerRef.getWorldUuid());
        if (world == null) return;
        world.execute(() -> {
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref == null) return;
            Player player = ref.getStore().getComponent(ref, Player.getComponentType());
            if (player == null) return;
            player.getHudManager().setVisibleHudComponents(playerRef);
        });
    }

    private void showUI(PlayerRef playerRef) {
        // Hide UI if enabled
        UUID worldUUID = playerRef.getWorldUuid();
        if (worldUUID == null) return;
        World world = Universe.get().getWorld(playerRef.getWorldUuid());
        if (world == null) return;
        world.execute(() -> {
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref == null) return;
            Player player = ref.getStore().getComponent(ref, Player.getComponentType());
            if (player == null) return;
            player.getHudManager().resetVisibleHudComponents(playerRef);
        });
    }

    public void addKeyframe(CameraKeyframe keyframe) {
        CameraKeyframe[] updated = Arrays.copyOf(cameraKeyframes, cameraKeyframes.length + 1);
        updated[cameraKeyframes.length] = keyframe;
        cameraKeyframes = updated;
    }
}
