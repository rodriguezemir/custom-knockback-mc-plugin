package site.zvolcan.customkb.listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Marks players as "expecting" a combat knockback so {@link VelocityPacketListener} can tell
 * combat knockback apart from unrelated velocity packets (fall damage recovery, elytra, etc.).
 */
public final class DamageTrackingListener implements Listener {

    private static final long PENDING_TTL_MILLIS = 250L;

    private final Map<UUID, Long> pendingKnockback = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        pendingKnockback.put(victim.getUniqueId(), System.currentTimeMillis());
    }

    /**
     * Returns whether the given player is currently expected to receive a combat knockback
     * velocity packet, consuming the pending state so it is only applied once.
     */
    public boolean consumePending(UUID uuid) {
        Long timestamp = pendingKnockback.remove(uuid);
        return timestamp != null && System.currentTimeMillis() - timestamp <= PENDING_TTL_MILLIS;
    }
}
