package net.lordimass.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.lordimass.assets.CameraSequenceAsset;
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class CameraSequenceCommand extends AbstractPlayerCommand {
    final RequiredArg<String> sequenceArg;
    final OptionalArg<PlayerRef> playerRefOptionalArg;

    public CameraSequenceCommand() {
        super("camerasequence", "Play back a camera sequence asset.");
        this.addAliases("cinematic");

        this.sequenceArg = withRequiredArg("sequence", "The ID of the camera sequence asset to play back", ArgTypes.STRING);
        this.playerRefOptionalArg = withOptionalArg("player", "The player to play the sequence to", ArgTypes.PLAYER_REF);
    }

    @Override
    protected void execute(
        @NonNull CommandContext commandContext,
        @NonNull Store<EntityStore> store,
        @NonNull Ref<EntityStore> ref,
        @NonNull PlayerRef playerRef,
        @NonNull World world
    ) {
        PlayerRef targetPlayer = playerRefOptionalArg.get(commandContext);
        targetPlayer = targetPlayer == null ? playerRef : targetPlayer;
        CameraSequenceAsset seq = CameraSequenceAsset.getAssetMap().getAsset(sequenceArg.get(commandContext));
        if (seq == null) {
            playerRef.sendMessage(
                Message.raw("Couldn't find camera sequence '"+sequenceArg.get(commandContext)+"'")
                    .color(Color.RED)
            );
            return;
        }
        playerRef.sendMessage(Message.raw("Playing sequence '"+sequenceArg.get(commandContext)+"' to player."));
        seq.play(targetPlayer, _ -> {
            playerRef.sendMessage(Message.raw("Camera sequence completed").color(Color.GREEN));
        });
    }
}
