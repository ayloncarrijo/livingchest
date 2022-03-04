package com.syllient.livingchest.animation.entity;

import com.syllient.livingchest.animation.Animation;
import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.eventhandler.registry.SoundRegistry;
import com.syllient.livingchest.geckolib.ExtendedAnimationController;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class ChesterAnimation extends Animation<ChesterEntity> {
  private static class Animation {
    private static final String IDLE = "animation.chester.idle";
    private static final String INIT_JUMP = "animation.chester.init_jump";
    private static final String JUMP = "animation.chester.jump";
    private static final String STOP_JUMP = "animation.chester.stop_jump";
    private static final String OPEN_MOUTH = "animation.chester.open_mouth";
    private static final String IDLE_MOUTH = "animation.chester.idle_mouth";
    private static final String CLOSE_MOUTH = "animation.chester.close_mouth";
  }

  private static class Controller {
    private static final String IDLE = "idle_controller";
    private static final String JUMP = "jump_controller";
    private static final String OPEN = "open_controller";
  }

  private final ExtendedAnimationController<ChesterEntity> idleController;
  private final ExtendedAnimationController<ChesterEntity> jumpController;
  private final ExtendedAnimationController<ChesterEntity> openController;
  private int idleSoundTimes = 0;
  private int ticksIdling = 0;
  private boolean wasMouthOpen = false;

  public ChesterAnimation(final ChesterEntity chester) {
    super(chester);
    this.idleController =
        new ExtendedAnimationController<>(chester, Controller.IDLE, 0, this::handleIdleAnimation);
    this.jumpController =
        new ExtendedAnimationController<>(chester, Controller.JUMP, 0, this::handleJumpAnimation);
    this.openController =
        new ExtendedAnimationController<>(chester, Controller.OPEN, 0, this::handleOpenAnimation);
  }

  @Override
  public void registerControllers(final AnimationData data) {
    this.idleController.registerSoundListener(this::handleSoundKeyframe);
    this.jumpController.registerSoundListener(this::handleSoundKeyframe);
    this.openController.registerSoundListener(this::handleSoundKeyframe);
    data.addAnimationController(this.idleController);
    data.addAnimationController(this.jumpController);
    data.addAnimationController(this.openController);
  }

  private void handleAnimationTick() {
    final boolean isIdling = this.jumpController.isAnimationStopped();

    if (isIdling) {
      this.ticksIdling += 1;
    } else {
      this.ticksIdling = 0;
    }

    if ((this.animatable.isMouthOpen() && !this.wasMouthOpen) || this.ticksIdling < 5) {
      this.idleSoundTimes = 0;
    }

    this.wasMouthOpen = this.animatable.isMouthOpen();
  }

  private PlayState handleIdleAnimation(final AnimationEvent<? extends IAnimatable> event) {
    this.handleAnimationTick();

    if (this.ticksIdling < 5) {
      return PlayState.STOP;
    }

    this.idleController.setAnimationSpeed(1.1D);
    this.idleController.setAnimation(new AnimationBuilder().addAnimation(Animation.IDLE, true));

    return PlayState.CONTINUE;
  }

  private PlayState handleJumpAnimation(final AnimationEvent<? extends IAnimatable> event) {
    if (this.jumpController.isAnimationStopped(Animation.INIT_JUMP)) {
      this.jumpController.setAnimation(new AnimationBuilder().addAnimation(Animation.JUMP));

      return PlayState.CONTINUE;
    }

    final boolean isJumping = this.jumpController.isCurrentAnimation(Animation.JUMP);

    if (this.animatable.isMoving() && this.animatable.isOnGround()
        && this.openController.isAnimationStopped()) {
      if (!isJumping) {
        this.jumpController.setAnimation(new AnimationBuilder().addAnimation(Animation.INIT_JUMP));
      }

      return PlayState.CONTINUE;
    }

    if (isJumping && this.jumpController.hasJustFinishedAnimation()) {
      this.jumpController.setAnimation(new AnimationBuilder().addAnimation(Animation.STOP_JUMP));

      return PlayState.CONTINUE;
    }

    return PlayState.CONTINUE;
  }

  private PlayState handleOpenAnimation(final AnimationEvent<? extends IAnimatable> event) {
    if (this.ticksIdling < 5) {
      return PlayState.STOP;
    }

    if (this.animatable.isMouthOpen()) {
      this.openController.setAnimation(new AnimationBuilder().addAnimation(Animation.OPEN_MOUTH)
          .addAnimation(Animation.IDLE_MOUTH));

      return PlayState.CONTINUE;
    }

    if (this.openController.isCurrentAnimation(Animation.IDLE_MOUTH)) {
      this.openController.setAnimation(new AnimationBuilder().addAnimation(Animation.CLOSE_MOUTH));

      return PlayState.CONTINUE;
    }

    return PlayState.CONTINUE;
  }

  private void handleSoundKeyframe(final SoundKeyframeEvent<? extends IAnimatable> event) {
    switch (event.sound) {
      case "idle": {
        this.idleSoundTimes += 1;
        this.playSound(SoundRegistry.Entity.Chester.IDLE,
            Math.max(0.025F, 0.1F - this.idleSoundTimes * 0.005F), 1.0F);
        break;
      }
      case "jump": {
        this.playSound(SoundRegistry.Entity.Chester.JUMP, 0.15F, 1.0F);
        break;
      }
      case "open_mouth": {
        this.playSound(SoundRegistry.Entity.Chester.OPEN_MOUTH, 0.5F, 1.0F);
        break;
      }
      case "close_mouth": {
        this.playSound(SoundRegistry.Entity.Chester.CLOSE_MOUTH, 0.5F, 1.0F);
        break;
      }
      default: {

      }
    }
  }

  private void playSound(final SoundEvent sound, final float volume, final float pitch) {
    final Minecraft instance = Minecraft.getInstance();

    instance.level.playLocalSound(new BlockPos(this.animatable.position()), sound,
        SoundCategory.NEUTRAL, volume, pitch, false);
  }
}
