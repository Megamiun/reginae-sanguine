# Assets

Currently the application has to be created with their assets bundled to the project. In the future asset loading bia HTTP should also be available.

## Important Notice

**This repository does not include copyrighted assets from Final Fantasy VII Rebirth.** Card images, character artwork, logos, and other visual assets are the exclusive property of Square Enix Co., Ltd.

## Asset Requirements

To run this project with visual assets, you will need to obtain card images and other game assets separately. The project is designed to work with placeholder assets and can be extended with official artwork.

## Available Assets

High-quality card assets inspired by Queen's Blood are available from:

[Queen's Blood assets](https://miguelsanto.com/projects/queens-blood) by Miguel Espírito Santo**

These assets are fan-created recreations based on the original game and are provided separately from this codebase.

## Asset Structure

When using assets, place them in the following structure:

```
assets/
├──packs/
├──── queens_blood/
│     ├── base/
│     │   ├── 001.png
│     │   ├── 002.png
│     │   └── ...
│     └── pack_info.json
└──── custom/
      └── base/
      │   ├── 001.png
      │   ├── 002.png
      │   └── ...
      └── pack_info.json
```

## Legal Considerations

- All original Final Fantasy VII assets remain property of Square Enix
- This project is for educational and non-commercial use only
- Assets should be obtained through legitimate means
- Respect copyright and licensing terms of any assets you use

## Disclaimer

This project and its contributors are not affiliated with Square Enix. All trademarks, copyrights, and other intellectual property rights remain with their respective owners.
