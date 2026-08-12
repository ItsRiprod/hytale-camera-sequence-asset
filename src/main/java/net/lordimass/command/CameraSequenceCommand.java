package net.lordimass.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.exceptions.SenderTypeException;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.lordimass.assets.CameraSequenceAsset;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class CameraSequenceCommand extends AbstractCommand {
    final RequiredArg<String> sequenceArg;
    final OptionalArg<PlayerRef> playerRefOptionalArg;

    public CameraSequenceCommand() {
        super("camerasequence", "Play back a camera sequence asset.");
        this.addAliases("cinematic");

        this.sequenceArg = withRequiredArg("sequence", "The ID of the camera sequence asset to play back", ArgTypes.STRING);
        this.playerRefOptionalArg = withOptionalArg("player", "The player to play the sequence to", ArgTypes.PLAYER_REF);
    }

    @Override
    protected @Nullable CompletableFuture<Void> execute(@NonNull CommandContext commandContext) {
        boolean fromConsole;
        var targetPlayer = new Object() {
            PlayerRef val = playerRefOptionalArg.get(commandContext);
        };
        if (targetPlayer.val == null) {
            try {
                targetPlayer.val = commandContext.senderAs(PlayerRef.class);
            } catch (SenderTypeException _) {}
            fromConsole = false;
        } else {
            fromConsole = true;
        }
        if (targetPlayer.val == null) {
            commandContext.sendMessage(Message.raw("Sender must be a player or provide the --player option!"));
            return null;
        }

        CameraSequenceAsset seq = CameraSequenceAsset.getAssetMap().getAsset(sequenceArg.get(commandContext));
        if (seq == null) {
            if (!fromConsole) targetPlayer.val.sendMessage(
                Message.raw("Couldn't find camera sequence '"+sequenceArg.get(commandContext)+"'")
                    .color(Color.RED)
            );
            return null;
        }
        if (!fromConsole) targetPlayer.val.sendMessage(
            Message.raw("Playing sequence '"+sequenceArg.get(commandContext)+"' to player.")
        );
        seq.play(targetPlayer.val, _ -> {
            if (!fromConsole) targetPlayer.val.sendMessage(Message.raw("Camera sequence completed").color(Color.GREEN));
        });
        return null;
    }
}
