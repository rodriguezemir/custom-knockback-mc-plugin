package site.zvolcan.customkb.listener;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import org.bukkit.entity.Player;
import site.zvolcan.customkb.config.KnockbackConfig;

/**
 * Intercepts outgoing {@code Play.Server.ENTITY_VELOCITY} packets (the modern replacement for
 * {@code PacketPlayOutEntityVelocity}) and rescales the combat knockback a player is about to
 * receive for themselves according to {@link KnockbackConfig}.
 */
public final class VelocityPacketListener extends PacketListenerAbstract {

    private final KnockbackConfig config;
    private final DamageTrackingListener damageTracker;

    public VelocityPacketListener(KnockbackConfig config, DamageTrackingListener damageTracker) {
        this.config = config;
        this.damageTracker = damageTracker;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_VELOCITY) {
            return;
        }
        if (!(event.getPlayer() instanceof Player receiver)) {
            return;
        }

        WrapperPlayServerEntityVelocity wrapper = new WrapperPlayServerEntityVelocity(event);
        if (wrapper.getEntityId() != receiver.getEntityId()) {
            // Velocity packets for other entities only drive client-side animation; only a
            // player's own entity id affects their movement.
            return;
        }
        if (!damageTracker.consumePending(receiver.getUniqueId())) {
            return;
        }

        Vector3d velocity = wrapper.getVelocity();
        wrapper.setVelocity(new Vector3d(
                velocity.getX() * config.getHorizontalMultiplier(),
                velocity.getY() * config.getVerticalMultiplier(),
                velocity.getZ() * config.getHorizontalMultiplier()));
    }
}
