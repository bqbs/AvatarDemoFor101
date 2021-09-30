package com.jclian.hmos.avatarfor101.slice;

import com.jclian.hmos.avatarfor101.ResourceTable;
import com.jclian.hmos.avatarfor101.utils.PixelMapUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.render.*;
import ohos.agp.utils.Color;
import ohos.agp.utils.Point;
import ohos.agp.utils.RectFloat;
import ohos.global.configuration.DeviceCapability;
import ohos.media.image.PixelMap;

public class DiffAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_diff);

        Image image = (ohos.agp.components.Image) findComponentById(ResourceTable.Id_img_ame1);
        Image image2 = (ohos.agp.components.Image) findComponentById(ResourceTable.Id_img_ame2);
        Image image3 = (ohos.agp.components.Image) findComponentById(ResourceTable.Id_img_ame3);
        image.addDrawTask(new MyDrawTask(Shader.TileMode.CLAMP_TILEMODE));
        image2.addDrawTask(new MyDrawTask(Shader.TileMode.MIRROR_TILEMODE));
        image3.addDrawTask(new MyDrawTask(Shader.TileMode.REPEAT_TILEMODE));
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    class MyDrawTask implements Component.DrawTask {
        private Shader.TileMode mTileMode;

        public MyDrawTask(Shader.TileMode tileMode) {
            this.mTileMode = tileMode;
        }

        @Override
        public void onDraw(Component component, Canvas canvas) {
             //渐变红背景
            RectFloat rect = new RectFloat(0f, 0f, component.getWidth(), component.getHeight());
            Paint paint = new Paint();
            paint.setGradientShaderColor(new Color[]{Color.RED, Color.TRANSPARENT});
            LinearShader linearShader = new LinearShader(
                    new Point[]{new Point(0, 0), new Point(component.getWidth() /2, 0)}, // 为了演示TileMode的区别，这里高度修改成了0
                    new float[]{0f, 1f},
                    new Color[]{Color.RED, Color.TRANSPARENT}, mTileMode // 为了演示TileMode的区别，这里修改成了变量
            );
            paint.setShader(linearShader, Paint.ShaderType.LINEAR_SHADER);
            canvas.drawRect(rect, paint);

            // 绘制五星
            Paint paintImage = new Paint();
            paintImage.setAntiAlias(true);
            paintImage.setStrokeCap(Paint.StrokeCap.ROUND_CAP);
            paintImage.setStyle(Paint.Style.STROKE_STYLE);
            PixelMap pixelMaps = PixelMapUtils.createPixelMapByResId(ResourceTable.Media_cn_starts, getContext()).get();
            int imageWidth = pixelMaps.getImageInfo().size.width;
            int imageHeight = pixelMaps.getImageInfo().size.height;

            // 指定图片在屏幕上显示的区域
            RectFloat dst = new RectFloat(0, 0,
                    component.getWidth() * 2 / 3, component.getWidth() * 2 / 3 * imageHeight / imageWidth);
            canvas.drawPixelMapHolderRect(new PixelMapHolder(pixelMaps), dst, paintImage);
            paint.reset();
            paint.setColor(new Color(Color.rgb(0xff, 0xff, 0xff)));
            paint.setAntiAlias(true);
            paint.setTextSize(vp2px(20));
            paint.setStyle(Paint.Style.FILLANDSTROKE_STYLE);
            canvas.drawText(paint, mTileMode.name(), 0, component.getHeight());
        }

        /**
         * vp2px 将vp转换成px
         *
         * @param size size
         * @return int
         */
        public int vp2px(int size) {
            int density = getResourceManager().getDeviceCapability().screenDensity / DeviceCapability.SCREEN_MDPI;
            return size * density;
        }
    }
}
