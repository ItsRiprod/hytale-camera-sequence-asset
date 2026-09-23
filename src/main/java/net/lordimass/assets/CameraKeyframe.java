package net.lordimass.assets;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.EasingType;
import lombok.Getter;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import javax.annotation.Nonnull;

public class CameraKeyframe {
    @Nonnull public static CodecMapCodec<CameraKeyframe> CODEC = new CodecMapCodec<>();

    @Nonnull public static final BuilderCodec<@NotNull CameraKeyframe> ABSTRACT_CODEC = BuilderCodec
        .abstractBuilder(CameraKeyframe.class)
        .appendInherited(new KeyedCodec<>("Position", Vector3dUtil.CODEC),
            (keyframe, position) -> keyframe.position = position,
            CameraKeyframe::getPosition,
            (cameraKeyframe, parent) -> cameraKeyframe.position = parent.position
        )
        .addValidator(Validators.nonNull())
        .documentation("The position the camera should have moved to by the end of this keyframe.")
        .add()
        .appendInherited(new KeyedCodec<>("Duration", Codec.FLOAT),
            (keyframe, duration) -> keyframe.durationSeconds = duration == null ? 0 : duration,
            CameraKeyframe::getDurationSeconds,
            (cameraKeyframe, parent) -> cameraKeyframe.durationSeconds = parent.durationSeconds
        )
        .documentation("The length of time this keyframe should take, in seconds.")
        .add()
        .appendInherited(new KeyedCodec<>("Easing", new EnumCodec<>(EasingType.class)),
            (keyframe, easing) -> keyframe.easing = easing == null ? EasingType.Linear : easing,
            CameraKeyframe::getEasing,
            (cameraKeyframe, parent) -> cameraKeyframe.easing = parent.easing
        )
        .documentation("The type of camera easing that should be used.")
        .add()
        .appendInherited(new KeyedCodec<>("Fov", Codec.FLOAT),
            (keyframe, fov) -> keyframe.fov = fov,
            CameraKeyframe::getFov,
            (cameraKeyframe, parent) -> cameraKeyframe.fov = parent.fov
        )
        .documentation("The FOV the camera should have moved to by the end of this keyframe.")
        .add()
        .appendInherited(new KeyedCodec<>("RelativeToPlayer", Codec.BOOLEAN),
            (keyframe, relativeToPlayer) -> keyframe.relativeToPlayer = relativeToPlayer,
            CameraKeyframe::isRelativeToPlayer,
            (cameraKeyframe, parent) -> cameraKeyframe.relativeToPlayer = parent.relativeToPlayer
        )
        .documentation("Whether the position and rotation should be relative to the player's current view, or global.")
        .add()
        .build();

    @Getter private Vector3d position;
    @Getter private float durationSeconds;
    @Getter private EasingType easing = EasingType.Linear;
    @Getter private Float fov = 70f;
    @Getter private boolean relativeToPlayer;

    public CameraKeyframe() {}

    public CameraKeyframe(Transform transform) {
        this.position = transform.getPosition();
        relativeToPlayer = false;
        durationSeconds = 3;
    }

    public static class Keyframe extends CameraKeyframe {
        public static final BuilderCodec<@NotNull Keyframe> CODEC = BuilderCodec
            .builder(Keyframe.class, Keyframe::new, CameraKeyframe.ABSTRACT_CODEC)
            .append(new KeyedCodec<>("Look", Rotation3f.CODEC),
                (keyframe, look) -> keyframe.look = look,
                Keyframe::getLook
            )
            .addValidator(Validators.nonNull())
            .add()
            .build();

        @Getter private Rotation3f look;

        protected Keyframe() {}

        public Keyframe(Transform transform) {
            super(transform);
            this.look = transform.getRotation();
        }

        protected Rotation3f getLookRadians() {
            return new Rotation3f(
                (float) (Math.toRadians(look.x)),
                (float) (Math.toRadians(look.y)),
                (float) (Math.toRadians(look.z))
            );
        }
    }

    public static class KeyframeLookingAt extends CameraKeyframe {
        public static final BuilderCodec<@NotNull KeyframeLookingAt> CODEC = BuilderCodec
            .builder(KeyframeLookingAt.class, KeyframeLookingAt::new, CameraKeyframe.ABSTRACT_CODEC)
            .append(new KeyedCodec<>("LookAtPoint", Vector3dUtil.CODEC),
                (keyframe, lookAtPoint) -> keyframe.lookAtPoint = lookAtPoint,
                KeyframeLookingAt::getLookAtPoint
            )
            .addValidator(Validators.nonNull())
            .add()
            .build();

        @Getter private Vector3d lookAtPoint;

        protected KeyframeLookingAt() {}
    }
}
