# BlueMap Custom Skin Server
<a title="Fabric Language Kotlin" href="https://minecraft.curseforge.com/projects/fabric-language-kotlin" target="_blank" rel="noopener noreferrer"><img style="display: block; margin-left: auto; margin-right: auto;" src="https://i.imgur.com/c1DH9VL.png" alt="" width="171" height="50" /></a>
<img src="https://cammiescorner.dev/images/extras/no_forge.png" width="147" height="50">

## Description
Simple addon for BlueMap that allows using a custom skin server.

## Config
```json5
{
    "debug": false,                               // Debug mode for issue or self error search 
    "serverType": "MOJANG",                       // Server type [ MOJANG | MOJANG_LIKE | CUSTOM ] 
    "customSkinServerUrl": "https://localhost/",  // Here u must put custom link. Examples u can see below
    "custom": {                                   // This params supported only by CUSTOM server type
        "suffix": "",                             // Text after username in request url. As example for https://mcskins.top/ need ".png"
        "getSkinBy": "NAME",                      // Type of search param [ NAME | UUID ]
        "skinByCase": "LOWER"                     // Name case [ LOWER | UPPER | DEFAULT ]. As example for https://mcskins.top/ need LOWER
    }
}
```
