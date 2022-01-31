package com.syllient.livingchest.client.renderer;

import com.syllient.livingchest.client.model.EyeBoneItemModel;
import com.syllient.livingchest.item.EyeBoneItem;

import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class EyeBoneItemRenderer extends GeoItemRenderer<EyeBoneItem> {
  public EyeBoneItemRenderer() {
    super(new EyeBoneItemModel());
  }
}
