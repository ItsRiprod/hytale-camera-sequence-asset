package net.lordimass.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.AssetArgumentType;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.lordimass.assets.CameraSequenceAsset;
import java.awt.*;
import javax.annotation.Nonnull;

public class CameraSequenceCommand extends AbstractTargetPlayerCommand {
    final RequiredArg<CameraSequenceAsset> sequenceArg;

    public CameraSequenceCommand() {
        super("camerasequence", "Play back a camera sequence asset.");
        this.addAliases("cinematic");

        this.sequenceArg = withRequiredArg("sequence", "The ID of the camera sequence asset to play back",
                new AssetArgumentType<>("CameraAsset", CameraSequenceAsset.class,
                        "Camera Asset"));
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Ref<EntityStore> sourceRef,
            @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef,
            @Nonnull World world, @Nonnull Store<EntityStore> store) {

        var seq = sequenceArg.get(commandContext);
        if (seq == null) {
            commandContext.sendMessage(
                    Message.raw("Couldn't find camera sequence '" + sequenceArg.get(commandContext).getId() + "'")
                            .color(Color.RED));
            return;
        }
        commandContext.sendMessage(
                Message.raw("Playing sequence '" + sequenceArg.get(commandContext).getId() + "' to player."));

        seq.play(playerRef, _ -> {
            commandContext.sendMessage(Message.raw("Camera sequence completed").color(Color.GREEN));
        });
    }
}
