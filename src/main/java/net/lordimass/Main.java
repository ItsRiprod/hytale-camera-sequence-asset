package net.lordimass;

import com.creditor.Creditor;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import net.lordimass.assets.CameraSequenceAsset;
import net.lordimass.command.CameraSequenceCommand;
import net.lordimass.tvEffects.TriggerCameraSequenceEffect;

import java.util.logging.Level;

public class Main extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public Main(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void start() {
        LOGGER.at(Level.INFO).log("Starting Camera Sequence Assets!");

        Creditor.start(this);
    }

    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("Setting up Camera Sequence Assets!");
        CameraSequenceAsset.register(this);
        getCommandRegistry().registerCommand(new CameraSequenceCommand());
        TriggerEffect.CODEC.register("TriggerCameraSequence", TriggerCameraSequenceEffect.class, TriggerCameraSequenceEffect.CODEC);

        Creditor.setup(this);
    }

    @Override
    protected void shutdown() {
        LOGGER.at(Level.INFO).log("Shutting down Camera Sequence Assets!");
    }
}
