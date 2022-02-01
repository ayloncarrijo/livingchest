package com.syllient.livingchest.client.renderer;

import com.syllient.livingchest.client.model.EyeBoneTileEntityModel;
import com.syllient.livingchest.tileentity.EyeBoneTileEntity;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class EyeBoneTileEntityRenderer extends GeoBlockRenderer<EyeBoneTileEntity> {
  public EyeBoneTileEntityRenderer() {
    super(new EyeBoneTileEntityModel());
  }
}
