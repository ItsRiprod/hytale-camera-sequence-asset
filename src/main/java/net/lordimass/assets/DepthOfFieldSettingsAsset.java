package net.lordimass.assets;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.codec.AssetCodecMapCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.codec.validation.validator.RangeValidator;
import com.hypixel.hytale.protocol.DepthOfFieldSettings;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import lombok.Getter;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

public class DepthOfFieldSettingsAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, DepthOfFieldSettingsAsset>> {
    public static final String ASSET_PATH = "CameraSequence/DepthOfFieldSettings";

    private static AssetStore<String, DepthOfFieldSettingsAsset, DefaultAssetMap<String, DepthOfFieldSettingsAsset>> assetStore;

    @Nonnull public static final AssetBuilderCodec<String, @NotNull DepthOfFieldSettingsAsset> CODEC = AssetBuilderCodec
        .builder(
            DepthOfFieldSettingsAsset.class,
            DepthOfFieldSettingsAsset::new,
            Codec.STRING,
            (asset, id) -> asset.id = id,
            DepthOfFieldSettingsAsset::getId,
            (asset, data) -> asset.data = data,
            asset -> asset.data
        )
        .append(new KeyedCodec<>("FarBlurMax", Codec.FLOAT),
            (a, v) -> a.farBlurMax = v,
            DepthOfFieldSettingsAsset::getFarBlurMax
        )
        .addValidator(new RangeValidator<>(0F, 1F, true))
        .add()
        .append(new KeyedCodec<>("FarBlurry", Codec.FLOAT),
            (a, v) -> a.farBlurry = v,
            DepthOfFieldSettingsAsset::getFarBlurry
        )
        .addValidator(Validators.greaterThanOrEqual(0F))
        .add()
        .append(new KeyedCodec<>("FarSharp", Codec.FLOAT),
            (a, v) -> a.farSharp = v,
            DepthOfFieldSettingsAsset::getFarSharp
        )
        .addValidator(Validators.greaterThanOrEqual(0F))
        .add()
        .append(new KeyedCodec<>("NearBlurMax", Codec.FLOAT),
            (a, v) -> a.nearBlurMax = v,
            DepthOfFieldSettingsAsset::getNearBlurMax
        )
        .addValidator(new RangeValidator<>(0F, 1F, true))
        .add()
        .append(new KeyedCodec<>("NearBlurry", Codec.FLOAT),
            (a, v) -> a.nearBlurry = v,
            DepthOfFieldSettingsAsset::getNearBlurry
        )
        .addValidator(Validators.greaterThanOrEqual(0F))
        .add()
        .append(new KeyedCodec<>("NearSharp", Codec.FLOAT),
            (a, v) -> a.nearSharp = v,
            DepthOfFieldSettingsAsset::getNearSharp
        )
        .addValidator(Validators.greaterThanOrEqual(0F))
        .add()
        .build();

    public static final AssetCodecMapCodec<String, DepthOfFieldSettingsAsset> ASSET_CODEC_MAP_CODEC = new AssetCodecMapCodec<>(
        Codec.STRING, (t, k) -> t.id = k, t -> t.id, (t, data) -> t.data = data, t -> t.data
    );
    @Nonnull public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec<>(DepthOfFieldSettingsAsset.class, ASSET_CODEC_MAP_CODEC);

    private AssetExtraInfo.Data data;
    @Getter private String id;

    @Getter private float farBlurMax;
    @Getter private float farBlurry;
    @Getter private float farSharp;
    @Getter private float nearBlurMax;
    @Getter private float nearBlurry;
    @Getter private float nearSharp;

    public DepthOfFieldSettingsAsset() {}

    public static AssetStore<String, DepthOfFieldSettingsAsset, DefaultAssetMap<String, DepthOfFieldSettingsAsset>> getAssetStore() {
        if (assetStore == null) assetStore = AssetRegistry.getAssetStore(DepthOfFieldSettingsAsset.class);
        return assetStore;
    }

    public static DefaultAssetMap<String, DepthOfFieldSettingsAsset> getAssetMap() {
        return getAssetStore().getAssetMap();
    }

    public static void register(JavaPlugin host) {
        host.getAssetRegistry().register(
            HytaleAssetStore.builder(DepthOfFieldSettingsAsset.class, new DefaultAssetMap<>())
                .setPath(ASSET_PATH)
                .setCodec(CODEC)
                .setKeyFunction(DepthOfFieldSettingsAsset::getId)
                .build()
        );
    }

    public DepthOfFieldSettings getDepthOfFieldSettings() {
        return new DepthOfFieldSettings(
            nearBlurry,
            nearSharp,
            farSharp,
            farBlurry,
            nearBlurMax,
            farBlurMax
        );
    }
}
