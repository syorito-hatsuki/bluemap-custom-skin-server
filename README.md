<a name="readme-top"></a>

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]
[![Discord][discord-shield]][discord-url]
[![Modrinth][modrinth-shield]][modrinth-url]

<br />
<div align="center">
  <a href="https://github.com/syorito-hatsuki/bluemap-custom-skin-server">
    <img src="https://github.com/syorito-hatsuki/bluemap-custom-skin-server/blob/master/src/main/resources/assets/bluemap_custom_skin_server/icon.png?raw=true" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">Bluemap Custom Skin Server</h3>

  <p align="center">
    BlueMap addon that allow to use custom skin server
    <br />
    <a href="https://discord.com/invite/zmkyJa3">Support</a>
    ·
    <a href="https://github.com/syorito-hatsuki/bluemap-custom-skin-server/issues">Report Bug</a>
    ·
    <a href="https://github.com/syorito-hatsuki/bluemap-custom-skin-server/issues">Request Feature</a>
  </p>
</div>

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#usage">Usage</a>
      <ul>
        <li><a href="#config">Config</a></li>
      </ul>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>

## About The Project

### Built With

* ![Fabric][fabric]
* ![Fabric-Language-Kotlin][fabric-language-kotlin]
* ![Ducky-Updater-Lib][ducky-updater-lib]
* ![BlueMap][bluemap]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Usage

### Config
  
```json5
{
  "debug": false,                              // Debug mode for issue or self error search 
  "serverType": "MOJANG",                      // Server type [ MOJANG_LIKE | CUSTOM ] 
  "customSkinServerUrl": "https://localhost/", // Here u must put custom link. Examples u can see below
  "custom": {                                  // This params supported only by CUSTOM server type
    "suffix": "",                              // Text after username in request url. As example for https://mcskins.top/ need ".png"
    "getSkinBy": "NAME",                       // Type of search param [ NAME | UUID ]
    "skinByCase": "LOWER"                      // Name case [ LOWER | UPPER | DEFAULT ]. As example for https://mcskins.top/ need LOWER
  }
}
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Roadmap

- [ ] Change link format

See the [open issues](https://github.com/syorito-hatsuki/bluemap-custom-skin-server/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

[contributors-shield]: https://img.shields.io/github/contributors/syorito-hatsuki/bluemap-custom-skin-server.svg?style=for-the-badge
[contributors-url]: https://github.com/syorito-hatsuki/bluemap-custom-skin-server/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/syorito-hatsuki/bluemap-custom-skin-server.svg?style=for-the-badge
[forks-url]: https://github.com/syorito-hatsuki/bluemap-custom-skin-server/network/members
[stars-shield]: https://img.shields.io/github/stars/syorito-hatsuki/bluemap-custom-skin-server.svg?style=for-the-badge
[stars-url]: https://github.com/syorito-hatsuki/bluemap-custom-skin-server/stargazers
[issues-shield]: https://img.shields.io/github/issues/syorito-hatsuki/bluemap-custom-skin-server?style=for-the-badge
[issues-url]: https://github.com/syorito-hatsuki/bluemap-custom-skin-server/issues
[license-shield]: https://img.shields.io/github/license/syorito-hatsuki/bluemap-custom-skin-server.svg?style=for-the-badge
[license-url]: https://github.com/syorito-hatsuki/bluemap-custom-skin-server/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/kit-lehto
[fabric]: https://img.shields.io/badge/fabric%20api-DBD0B4?style=for-the-badge
[fabric-language-kotlin]: https://img.shields.io/badge/fabric%20language%20kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white
[ducky-updater-lib]: https://img.shields.io/badge/ducky%20updater%20lib-1bd96a?style=for-the-badge
[bluemap]: https://img.shields.io/badge/bluemap-2c84e4?style=for-the-badge
[discord-shield]: https://img.shields.io/discord/1032138561618726952?logo=discord&logoColor=white&style=for-the-badge&label=Discord
[discord-url]: https://discord.com/invite/zmkyJa3
[modrinth-shield]: https://img.shields.io/modrinth/v/bcss?label=Modrinth&style=for-the-badge
[modrinth-url]: https://modrinth.com/mod/yacg
