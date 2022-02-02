package com.syllient.livingchest.item;

import com.syllient.livingchest.animation.EyeBoneItemAnimation;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EyeBoneItem extends ItemBlock implements IAnimatable {
  private final EyeBoneItemAnimation animation = new EyeBoneItemAnimation(this);

  public EyeBoneItem(final Block block) {
    super(block);
  }

  @Override
  public void registerControllers(final AnimationData data) {
    this.animation.registerControllers(data);
  }

  @Override
  public AnimationFactory getFactory() {
    return this.animation.getFactory();
  }
}
