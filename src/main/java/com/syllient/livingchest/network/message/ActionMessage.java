package com.syllient.livingchest.network.message;

import java.util.function.Supplier;
import java.util.stream.Stream;
import com.syllient.livingchest.entity.ai.action.Action;
import com.syllient.livingchest.entity.ai.action.ActionControllerProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;

public class ActionMessage {
  private final int entityId;

  private final int actionId;

  private ActionMessage(final int entityId, final int actionId) {
    this.entityId = entityId;
    this.actionId = actionId;
  }

  public static <T extends Entity & ActionControllerProvider<T>> ActionMessage create(
      final T entity, final Action action) {
    return new ActionMessage(entity.getId(), action == null ? -1 : action.getId());
  }

  public static void encode(final ActionMessage message, final PacketBuffer buffer) {
    buffer.writeInt(message.entityId);
    buffer.writeInt(message.actionId);
  }

  public static ActionMessage decode(final PacketBuffer buffer) {
    return new ActionMessage(buffer.readInt(), buffer.readInt());
  }

  public static void handle(final ActionMessage message, final Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> new SafeRunnable() {
        @Override
        public void run() {
          final Minecraft instance = Minecraft.getInstance();

          final Entity entity = instance.level.getEntity(message.entityId);

          if (entity instanceof ActionControllerProvider) {
            final ActionControllerProvider<?> provider = (ActionControllerProvider<?>) entity;

            provider.getActionController()
                .setActionWithoutSync(message.actionId == -1 ? null
                    : Stream.of(provider.getActions())
                        .filter((action) -> action.getId() == message.actionId).findFirst().get());
          }
        }
      });
    });

    ctx.get().setPacketHandled(true);
  }
}
