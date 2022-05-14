package com.syllient.livingchest.animation.animator.entity;

import com.syllient.livingchest.animation.PhasedAnimation;
import com.syllient.livingchest.animation.animator.Animator;
import com.syllient.livingchest.animation.controller.OrderedAnimationController;
import com.syllient.livingchest.animation.controller.PhasedAnimationController;
import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.eventhandler.registry.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class ChesterAnimator extends Animator<ChesterEntity> {
  private static class Animation {
    private static final String IDLE = "animation.chester.idle";
    private static final String SPAWN = "animation.chester.spawn";
    private static final String DESPAWN = "animation.chester.despawn";
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
    private static final String IDLE = "controller.idle";
    private static final String JUMP = "controller.jump";
    private static final String MOUTH = "controller.mouth";
    private static final String SLEEP = "controller.sleep";
    private static final String DEATH = "controller.death";
    private static final String SPAWN = "controller.spawn";
    private static final String DESPAWN = "controller.despawn";
  }

  private int idleSoundTimes = 0;
  private boolean wasMouthOpen = false;
  private boolean didSpawnAnimation = false;

  public ChesterAnimator(final ChesterEntity animatable) {
    super(animatable);
  }

  @Override
  public void tick() {
    final boolean isJumping = !this.getAnimationData().getOrderedAnimationControllers()
        .get(Controller.JUMP).isAnimationStopped();

    if ((this.animatable.isMouthOpen() && !this.wasMouthOpen) || isJumping) {
      this.idleSoundTimes = 0;
    }

    this.wasMouthOpen = this.animatable.isMouthOpen();
  }

  @Override
  public void registerControllers(final AnimationData data) {
    final OrderedAnimationController<ChesterEntity> deathController =
        new OrderedAnimationController<>(this, Controller.DEATH,
            (controller) -> this.animatable.isDeadOrDying(), (event) -> new AnimationBuilder()
                .addAnimation(Animation.DEATH).addAnimation(Animation.IDLE_DEATH));

    final OrderedAnimationController<ChesterEntity> despawnController =
        new OrderedAnimationController<>(this, Controller.DESPAWN,
            (controller) -> this.animatable.isDespawning(),
            (event) -> new AnimationBuilder().addAnimation(Animation.DESPAWN));

    final OrderedAnimationController<ChesterEntity> spawnController =
        new OrderedAnimationController<>(this, Controller.SPAWN, (controller) -> {
          if (this.didSpawnAnimation) {
            return false;
          }

          this.didSpawnAnimation = true;
          return true;
        }, (event) -> new AnimationBuilder().addAnimation(Animation.SPAWN));

    final OrderedAnimationController<ChesterEntity> mouthController =
        new PhasedAnimationController<>(this, Controller.MOUTH,
            (controller) -> this.animatable.isMouthOpen(),
            PhasedAnimation.<ChesterEntity>builder()
                .setStartAnimation(
                    (event) -> new AnimationBuilder().addAnimation(Animation.OPEN_MOUTH))
                .setLoopAnimation(
                    (event) -> new AnimationBuilder().addAnimation(Animation.IDLE_MOUTH))
                .setEndAnimation(
                    (event) -> new AnimationBuilder().addAnimation(Animation.CLOSE_MOUTH)))
                        .addParallelController(Controller.IDLE);

    final OrderedAnimationController<ChesterEntity> sleepController =
        new PhasedAnimationController<>(this, Controller.SLEEP,
            (controller) -> this.animatable.isInSittingPose(),
            PhasedAnimation.<ChesterEntity>builder()
                .setStartAnimation(
                    (event) -> new AnimationBuilder().addAnimation(Animation.INIT_SLEEP))
                .setLoopAnimation((event) -> new AnimationBuilder().addAnimation(Animation.SLEEP))
                .setEndAnimation(
                    (event) -> new AnimationBuilder().addAnimation(Animation.STOP_SLEEP)));

    final OrderedAnimationController<ChesterEntity> jumpController =
        new PhasedAnimationController<>(this, Controller.JUMP,
            (controller) -> this.animatable.isMoving() && this.animatable.isOnGround(),
            PhasedAnimation.<ChesterEntity>builder()
                .setStartAnimation(
                    (event) -> new AnimationBuilder().addAnimation(Animation.INIT_JUMP))
                .setLoopAnimation((event) -> new AnimationBuilder().addAnimation(Animation.JUMP))
                .setEndAnimation(
                    (event) -> new AnimationBuilder().addAnimation(Animation.STOP_JUMP)));

    final OrderedAnimationController<ChesterEntity> idleController =
        new OrderedAnimationController<>(this, Controller.IDLE, (controller) -> true,
            (event) -> new AnimationBuilder().addAnimation(Animation.IDLE));

    data.addAnimationController(deathController);
    data.addAnimationController(despawnController);
    data.addAnimationController(spawnController);
    data.addAnimationController(mouthController);
    data.addAnimationController(sleepController);
    data.addAnimationController(jumpController);
    data.addAnimationController(idleController);

    idleController.registerSoundListener(this::handleSoundKeyframes);
    jumpController.registerSoundListener(this::handleSoundKeyframes);
    mouthController.registerSoundListener(this::handleSoundKeyframes);

    idleController.setAnimationSpeed(1.1D);
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
