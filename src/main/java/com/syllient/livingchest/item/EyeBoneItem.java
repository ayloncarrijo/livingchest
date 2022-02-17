package com.syllient.livingchest.item;

import com.syllient.livingchest.client.renderer.EyeBoneItemRenderer;
import com.syllient.livingchest.registry.BlockRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EyeBoneItem extends BlockItem implements IAnimatable {
  public EyeBoneItem() {
    super(BlockRegistry.EYE_BONE, new Item.Properties().tab(ItemGroup.TAB_MISC).stacksTo(1)
        .setISTER(() -> EyeBoneItemRenderer::new));
  }

  @Override
  public void registerControllers(final AnimationData data) {

  }

  @Override
  public AnimationFactory getFactory() {
    return null;
  }
}
