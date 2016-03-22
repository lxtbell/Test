Android 开发规范
=============

0. 使用 Javadoc 注明 public Library methods
0. 由专用 Library 调用 Android 及 Java API 便于发布及移植, 例如

```java
System.out.println(“”);
```
应为
```java
if (Util.debug()) Util.println(“”);
```

0. Math.sin(Math.toRadians(alpha)) 应为 MathUtil.sin(alpha)
0. 标记每个 class 中 non-temporary fields
Android 需要实现 Parcelable, 即 class instance 与序列化数据集合的转换
0. 本项目地理多于数学, 一律使用角度制
0. 使用 Java package 对 class 进行分类
0. 最低支持Android API 17 (4.2 Jelly Bean)
(即使用的 API不超过 Android API 17)
针对 Android API 21 (5.0 Lollipop) 开发

