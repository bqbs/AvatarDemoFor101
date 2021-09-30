## HarmoneyOS 国庆节国旗头像怎么画？

祖国过生日。当然要来蹭一波啦～～

先来看看效果图～

![](./previews/Capture.PNG)

###　Github
获取完整代码：[AvatarDemoFor101 ](https://github.com/bqbs/AvatarDemoFor101)

现在来回答一下标题的问题吧。在鸿蒙的 Java UI 框架（因为还有JS UI）,这里只讲 Java UI 部分。
首先，在鸿蒙里面，我们不需要去自定义或者继承某个 Component (我个人理解，相当于 Android 里面的 View )，然后重写一大堆方法才能实现我们的功能。只需要通过 `Component#addDrawTask` 方法即可。而且理论上是对所有 Component 都可用的。
例如：

``` java
// 这段代码在 MainAbilitySlice.java 下
// 找到 Component
Image image = (ohos.agp.components.Image) findComponentById(ResourceTable.Id_img_avatar);
// 为 Component 添加绘制任务
image.addDrawTask(...); //省略细节。具体实现，见下方代码
```
该方法有两个参数：
- task	表示要添加的 DrawTask
- layer	指定`task`的位置。可选的值为`DrawTask#BETWEEN_BACKGROUND_AND_CONTENT` 或者 `DrawTask#BETWEEN_CONTENT_AND_FOREGROUND`（默认值）。这两个选项就很好理解了，就是绘制顺序的区别 **背景->DrawTask->组件内容** ，还是**背景->组件内容->DrawTask**

现在来详细看看我们的 **DrawTask** 吧
``` java
// 这段代码在 MainAbilitySlice.java 下
image.addDrawTask((component, canvas) -> {

    // 绘制国旗的渐变背景
    RectFloat rect = new RectFloat(0f, 0f, component.getWidth(), component.getHeight());
    Paint paint = new Paint();
    LinearShader linearShader = new LinearShader(
    new Point[]{new Point(0, 0), new Point(component.getWidth() * 2 / 3, component.getHeight())},
    new float[]{0f, 1f},
    new Color[]{Color.RED, Color.TRANSPARENT}, Shader.TileMode.CLAMP_TILEMODE
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
    });
```
采用分层绘制的思路，先绘制了一个渐变（红色到透明）的矩形，，然后绘制五星的图片。我在这里碰到比较难得点是渐变背景的绘制（不熟悉 API 挺难的）。现在就把我知道的东西讲讲。
####　 定义一个线性着色器
``` java
LinearShader linearShader = new LinearShader(
    new Point[]{new Point(0, 0), new Point(component.getWidth() * 2 / 3, component.getHeight())},
    new float[]{0f, 1f},
    new Color[]{Color.RED, Color.TRANSPARENT}, Shader.TileMode.CLAMP_TILEMODE
    );
paint.setShader(linearShader, Paint.ShaderType.LINEAR_SHADER);
```
这一行代码，分别传进去四个参数了。

首先第一个数组` new Point[]{new Point(0, 0), new Point(component.getWidth() * 2 / 3, component.getHeight())}`，分别为渐变的起点和终点（我们这里是从左上角到组件的 2/3 * width ，组件的底部的点）。

`new float[]{0f, 1f}` 和 `new Color[]{Color.RED, Color.TRANSPARENT}` 是配合使用的。前者指示所有颜色在着色颜色数组（后者）中的位置。

最后一个`Shader.TileMode`。表示原始阴影图像的边缘颜色将在额外部分进行阴影处理。我们定义的渐变区域到水平方向的中间位置（调整了下代码，从组件的左边到组件的中间水平方向上渐变。详细代码可以见：DiffAbilitySlice#MyDrawTask ），即组件的右边部分不在我们的着色器定义的着色区域内。该参数有三个枚举值。分别为`CLAMP_TILEMODE`、`MIRROR_TILEMODE`和`REPEAT_TILEMODE`。

请留意下图中区别：

![](./previews/Capture2.PNG)

看看定义TileMode的区别。
- CLAMP_TILEMODE 表示原始阴影图像的边缘颜色将在额外部分进行阴影处理。即右半边部分保留了原始图片的内容

- MIRROR_TILEMODE 表示将在额外部分水平和垂直绘制原始阴影图像的交替镜像。镜像，对称，按照这个说法，设置结束点在屏幕中心时，应该是＞＜这样四个方向镜像的。为什么呢？期待更多的代码或者有小伙伴知道的可以留言。

- REPEAT_TILEMODE  表示将在多余部分水平和垂直重复绘制原始阴影图像。重复着色器的动作

    
So.代码和逻辑都非常简单～

### 总结

API 现学现卖，所以写得不好的地方请各位不吝赐教。学习新东西的过程，难，但是有意思。
各位国庆节快乐！！！

### 灵感来源
掘友文章 [Android之生成国庆节头像——举儿](https://juejin.cn/post/7013539853610516511)

### 瑟图
![](./previews/ame.png)

八王的没有高清就算了～想要可以搜“耳机夹脸”
