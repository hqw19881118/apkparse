# apkparse
对apk进行解析的工具

使用说明：

  - java -jar apkparse.jar [srcApk] [apkInfoFile] [apkInfoDir] <iconName>

其中
  - srcApk表示需要解析的apk文件完整名称(包含路径，可使用相对或绝对路径)，必选
  - apkInfoFile表示解析出来的apk信息的存放文件名称(不包含路径)，必选
  - apkInfoDir表示apkInfoDir需要存放的路径(可使用相对路径或绝对路径)，必选
  - iconName表示此apk的icon在该包中的绝对路径(可从apkInfoFile中获取), 可选
