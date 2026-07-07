package site.zvolcan.customkb;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;
import site.zvolcan.customkb.command.KnockbackCommand;
import site.zvolcan.customkb.config.KnockbackConfig;
import site.zvolcan.customkb.listener.DamageTrackingListener;
import site.zvolcan.customkb.listener.VelocityPacketListener;

public final class Main extends JavaPlugin {

    private KnockbackConfig knockbackConfig;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();

        saveDefaultConfig();
        knockbackConfig = new KnockbackConfig(this);

        DamageTrackingListener damageTracker = new DamageTrackingListener();
        getServer().getPluginManager().registerEvents(damageTracker, this);
        PacketEvents.getAPI().getEventManager().registerListener(
                new VelocityPacketListener(knockbackConfig, damageTracker));

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();
            registrar.register(
                    KnockbackCommand.create(knockbackConfig),
                    "Modify the custom knockback multipliers.",
                    List.of("kb"));
        });
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}
