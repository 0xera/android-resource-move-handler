### Android Studio plugin

[Plugin on JetBrains Marketplace](https://plugins.jetbrains.com/plugin/24130-android-resource-move-handler/edit)

The plugin that handles android resource file movement:
- Can change R-class package that is used to access to resource
- Can add `FIXME_RES` comment in xml files when resource cannot be accessed after file moves to other module. You can disable this feature in `Settings -> Android Resource Move Handler` 


#### Limitations
Attrs in declare-styleable are not handling, because AS can't find all usages of resource and also can found incorrect variants that may lead to unexpected changes when moving file. For example, AS can't found `R.attr.Style_customAttr` in code or `app:customAttr` in xml, when search usages of `<attr name="customAttr"/>` in `<declare-styleable name="Style">`
