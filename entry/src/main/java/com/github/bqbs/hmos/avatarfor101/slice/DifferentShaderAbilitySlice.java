package com.github.bqbs.hmos.avatarfor101.slice;

import com.github.bqbs.hmos.avatarfor101.ResourceTable;
import com.github.bqbs.hmos.avatarfor101.utils.LogUtils;
import com.github.bqbs.hmos.avatarfor101.utils.PixelMapUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.RadioContainer;
import ohos.agp.render.*;
import ohos.agp.utils.Color;
import ohos.agp.utils.Point;
import ohos.agp.utils.RectFloat;
import ohos.global.configuration.DeviceCapability;
import ohos.media.image.PixelMap;
import ohos.media.image.common.Size;

public class DifferentShaderAbilitySlice extends AbilitySlice {
    private final String TAG = "DifferentShaderAbilitySlice";

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_different_shader);

        RadioContainer radioContainer = (RadioContainer) findComponentById(ResourceTable.Id_radio_container);
        radioContainer.setMarkChangedListener(new RadioContainer.CheckedStateChangedListener() {
            @Override
            public void onCheckedChanged(RadioContainer radioContainer, int i) {
                Shader.TileMode tileMode;

                switch (radioContainer.getMarkedButtonId()) {
                    case 1: {
                        tileMode = Shader.TileMode.MIRROR_TILEMODE;
                        break;
                    }
                    case 2: {
                        tileMode = Shader.TileMode.REPEAT_TILEMODE;
                        break;
                    }
                    case 0:
                    default: {
                        tileMode = Shader.TileMode.CLAMP_TILEMODE;
                        break;
                    }
                }

                addDrawTask(tileMode);
            }
        });
        addDrawTask(Shader.TileMode.CLAMP_TILEMODE);
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    private void addDrawTask(Shader.TileMode tileMode) {
        LogUtils.debug(TAG, "addDrawTask#" + tileMode.name());
        Image image = (Image) findComponentById(ResourceTable.Id_img_linear);
        Image image2 = (Image) findComponentById(ResourceTable.Id_img_sweep);
        Image image3 = (Image) findComponentById(ResourceTable.Id_img_radial);
        Image image4 = (Image) findComponentById(ResourceTable.Id_img_group);
        Image image5 = (Image) findComponentById(ResourceTable.Id_img_pixelmap);
        image.addDrawTask(new MyDrawTask(tileMode));
        image2.addDrawTask(new MyDrawTask(tileMode));
        image3.addDrawTask(new MyDrawTask(tileMode));
        image4.addDrawTask(new MyDrawTask(tileMode));
        image5.addDrawTask(new MyDrawTask(tileMode));
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
                    new Point[]{new Point(0, 0), new Point(component.getWidth() / 2f, 0)}, // 为了演示TileMode的区别，这里高度修改成了0
                    new float[]{0f, 1f},
                    new Color[]{Color.RED, Color.TRANSPARENT}, mTileMode // 为了演示TileMode的区别，这里修改成了变量
            );

            SweepShader sweepShader = new SweepShader(component.getWidth() / 2f,
                    component.getHeight() / 2f,
                    new Color[]{Color.RED, Color.TRANSPARENT},
                    new float[]{0f, 0.1f}
            );

            RadialShader radialShader = new RadialShader(
                    new Point(component.getWidth() / 2f, component.getHeight() / 2f),
                    component.getHeight() / 2f, new float[]{0f, 1f},
                    new Color[]{Color.RED, Color.TRANSPARENT},
                    mTileMode
            );
            GroupShader groupShader = new GroupShader(sweepShader, radialShader, BlendMode.DST_IN);
            Paint paintImage = new Paint();
            paintImage.setAntiAlias(true);
            paintImage.setStrokeCap(Paint.StrokeCap.BUTT_CAP);
            paintImage.setStyle(Paint.Style.FILLANDSTROKE_STYLE);
            PixelMap pixelMaps = PixelMapUtils.createPixelMapByResId(ResourceTable.Media_lgd, getContext()).get();
            PixelMapHolder pixelMapHolder = new PixelMapHolder(pixelMaps);
            PixelMapShader pixelMapShader = new PixelMapShader(pixelMapHolder, mTileMode, mTileMode);
            String text = "";
            switch (component.getId()) {
                case ResourceTable.Id_img_linear:
                    paint.setShader(linearShader, Paint.ShaderType.LINEAR_SHADER);
                    text = "LinearShader";
                    break;
                case ResourceTable.Id_img_sweep:
                    paint.setShader(sweepShader, Paint.ShaderType.SWEEP_SHADER);
                    text = "SweepShader";

                    break;
                case ResourceTable.Id_img_radial:
                    paint.setShader(radialShader, Paint.ShaderType.RADIAL_SHADER);
                    text = "RadialShader";

                    break;
                case ResourceTable.Id_img_group:
                    text = "GroupShader(SweepShader, RadialShader)";
                    paint.setShader(groupShader, Paint.ShaderType.GROUP_SHADER);
                    break;
                case ResourceTable.Id_img_pixelmap:
                    text = "PixelMap";
                    paint.setShader(pixelMapShader, Paint.ShaderType.PIXELMAP_SHADER);
                    break;
            }
            canvas.drawRect(rect, paint);

            paint.reset();
            paint.setColor(new Color(Color.rgb(0xff, 0xff, 0xff)));
            paint.setAntiAlias(true);
            paint.setTextSize(vp2px(20));
            paint.setStyle(Paint.Style.FILLANDSTROKE_STYLE);
            canvas.drawText(paint, text, 0, component.getHeight());
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
