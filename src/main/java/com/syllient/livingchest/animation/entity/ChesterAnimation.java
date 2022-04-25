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
    private static final String SPAWN = "animation.chester.spawn";
    private static final String DEATH = "animation.chester.death";
    private static final String IDLE_DEATH = "animation.chester.idle_death";
    private static final String INIT_SLEEP = "animation.chester.init_sleep";
    private static final String SLEEP = "animation.chester.sleep";
    private static final String STOP_SLEEP = "animation.chester.stop_sleep";
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
    private static final String MOUTH = "mouth_controller";
    private static final String SLEEP = "sleep_controller";
    private static final String DEATH = "death_controller";
    private static final String SPAWN = "spawn_controller";
  }

  private final ExtendedAnimationController<ChesterEntity> idleController;
  private final ExtendedAnimationController<ChesterEntity> jumpController;
  private final ExtendedAnimationController<ChesterEntity> mouthController;
  private final ExtendedAnimationController<ChesterEntity> sleepController;
  private final ExtendedAnimationController<ChesterEntity> deathController;
  private final ExtendedAnimationController<ChesterEntity> spawnController;
  private int idleSoundTimes = 0;
  private boolean wasMouthOpen = false;

  public ChesterAnimation(final ChesterEntity chester) {
    super(chester);
    this.idleController =
        new ExtendedAnimationController<>(chester, Controller.IDLE, 0, this::animateIdle);
    this.jumpController =
        new ExtendedAnimationController<>(chester, Controller.JUMP, 0, this::animateJump);
    this.mouthController =
        new ExtendedAnimationController<>(chester, Controller.MOUTH, 0, this::animateMouth);
    this.sleepController =
        new ExtendedAnimationController<>(chester, Controller.SLEEP, 0, this::animateSleep);
    this.deathController =
        new ExtendedAnimationController<>(chester, Controller.DEATH, 0, this::animateDeath);
    this.spawnController =
        new ExtendedAnimationController<>(chester, Controller.SPAWN, 0, this::animateSpawn);
  }

  @Override
  public void registerControllers(final AnimationData data) {
    this.idleController.registerSoundListener(this::handleSoundKeyframes);
    this.jumpController.registerSoundListener(this::handleSoundKeyframes);
    this.mouthController.registerSoundListener(this::handleSoundKeyframes);
    data.addAnimationController(this.idleController);
    data.addAnimationController(this.jumpController);
    data.addAnimationController(this.mouthController);
    data.addAnimationController(this.sleepController);
    data.addAnimationController(this.deathController);
    data.addAnimationController(this.spawnController);
  }

  private void tick() {
    if ((this.animatable.isMouthOpen() && !this.wasMouthOpen)
        || this.jumpController.ticksStopped < 5) {
      this.idleSoundTimes = 0;
    }

    this.wasMouthOpen = this.animatable.isMouthOpen();
  }

  private PlayState animateIdle(final AnimationEvent<? extends IAnimatable> event) {
    this.tick();

    if (!this.spawnController.isAnimationStopped() || !this.deathController.isAnimationStopped()
        || this.sleepController.ticksStopped < 3 || this.jumpController.ticksStopped < 5) {
      return PlayState.STOP;
    }

    this.idleController.setAnimationSpeed(1.1D);
    this.idleController.setAnimation(new AnimationBuilder().addAnimation(Animation.IDLE, true));

    return PlayState.CONTINUE;
  }

  private PlayState animateJump(final AnimationEvent<? extends IAnimatable> event) {
    if (!this.spawnController.isAnimationStopped() || !this.deathController.isAnimationStopped()
        || this.sleepController.ticksStopped < 3) {
      return PlayState.STOP;
    }

    if (this.jumpController.isAnimationStopped(Animation.INIT_JUMP)) {
      this.jumpController.setAnimation(new AnimationBuilder().addAnimation(Animation.JUMP));

      return PlayState.CONTINUE;
    }

    final boolean isJumping = this.jumpController.isCurrentAnimation(Animation.JUMP);

    if (this.animatable.isMoving() && this.animatable.isOnGround()
        && this.mouthController.isAnimationStopped()) {
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

  private PlayState animateMouth(final AnimationEvent<? extends IAnimatable> event) {
    if (!this.spawnController.isAnimationStopped() || !this.deathController.isAnimationStopped()
        || this.sleepController.ticksStopped < 3 || this.jumpController.ticksStopped < 5) {
      return PlayState.STOP;
    }

    if (this.animatable.isMouthOpen()) {
      this.mouthController.setAnimation(new AnimationBuilder().addAnimation(Animation.OPEN_MOUTH)
          .addAnimation(Animation.IDLE_MOUTH));

      return PlayState.CONTINUE;
    }

    if (this.mouthController.isCurrentAnimation(Animation.IDLE_MOUTH)) {
      this.mouthController.setAnimation(new AnimationBuilder().addAnimation(Animation.CLOSE_MOUTH));

      return PlayState.CONTINUE;
    }

    return PlayState.CONTINUE;
  }

  private PlayState animateSleep(final AnimationEvent<? extends IAnimatable> event) {
    if (!this.spawnController.isAnimationStopped() || !this.deathController.isAnimationStopped()
        || this.jumpController.ticksStopped < 5) {
      return PlayState.STOP;
    }

    if (this.sleepController.isAnimationStopped(Animation.INIT_SLEEP)) {
      this.sleepController.setAnimation(new AnimationBuilder().addAnimation(Animation.SLEEP));

      return PlayState.CONTINUE;
    }

    final boolean isSleeping = this.sleepController.isCurrentAnimation(Animation.SLEEP);

    if (this.animatable.isInSittingPose() && !this.animatable.isMouthOpen()
        && this.mouthController.isAnimationStopped()) {
      if (!isSleeping) {
        this.sleepController
            .setAnimation(new AnimationBuilder().addAnimation(Animation.INIT_SLEEP));
      }

      return PlayState.CONTINUE;
    }

    if (isSleeping && this.sleepController.hasJustFinishedAnimation()) {
      this.sleepController.setAnimation(new AnimationBuilder().addAnimation(Animation.STOP_SLEEP));

      return PlayState.CONTINUE;
    }

    return PlayState.CONTINUE;
  }

  private PlayState animateDeath(final AnimationEvent<? extends IAnimatable> event) {
    if (this.animatable.isDeadOrDying()) {
      this.deathController.setAnimation(
          new AnimationBuilder().addAnimation(Animation.DEATH).addAnimation(Animation.IDLE_DEATH));
    }

    return PlayState.CONTINUE;
  }

  private PlayState animateSpawn(final AnimationEvent<? extends IAnimatable> event) {
    if (this.animatable.isSpawning()) {
      this.spawnController.setAnimation(new AnimationBuilder().addAnimation(Animation.SPAWN));
    }

    return PlayState.CONTINUE;
  }

  private void handleSoundKeyframes(final SoundKeyframeEvent<? extends IAnimatable> event) {
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
        return;
      }
    }
  }

  private void playSound(final SoundEvent sound, final float volume, final float pitch) {
    final Minecraft minecraft = Minecraft.getInstance();

    minecraft.level.playLocalSound(new BlockPos(this.animatable.position()), sound,
        SoundCategory.NEUTRAL, volume, pitch, false);
  }
}
