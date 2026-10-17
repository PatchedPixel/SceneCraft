<div align="center">

# 🎬 SceneCraft

**Record your Minecraft world. Replay it from any angle. Render it as a cinematic video.**

Started as a [ReplayMod](https://github.com/ReplayMod/ReplayMod) fork for modern Minecraft, and growing into its own project: multi-version, multi-loader, and rebuilt for the future after the first stable release.

![Version](https://img.shields.io/badge/version-beta-orange)
![Loader](https://img.shields.io/badge/loader-NeoForge%20%7C%20Forge%20(planned)-E68C3A)
![Renderer](https://img.shields.io/badge/renderer-OpenGL%20%7C%20Vulkan-8A63D2)
![License](https://img.shields.io/badge/license-GPLv3-blue)

</div>

> ⚠️ **Beta.** SceneCraft is on its first release. The core workflow (record → replay → edit → render) works, but expect rough edges. See [Known issues](#-known-issues).
>
> 🧭 **Where this is going:** the beta and 1.0.0 focus on a solid, stable port. After the stable release, SceneCraft moves beyond ReplayMod with an architecture overhaul and new modern features. See [Vision](#-vision) and [Roadmap](#-roadmap).

---

## ✨ Features

| Feature                      | What you can do                                                                                                                                                  |
|------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 🔴 **Recording**             | Capture singleplayer worlds and multiplayer servers into `.mcpr` replay files, saved automatically.                                                             |
| ▶️ **Replay viewer**         | Play, pause, change speed, and jump around the replay timeline. Fly a free camera through the recorded world, or spectate a player.                             |
| 🎞️ **Camera path editor**   | Place position and time keyframes on a timeline, preview the path, and edit or drag keyframes. Time keyframes let you speed up, slow down, or reverse playback. |
| 📍 **Markers**               | Add event markers to the replay timeline and jump back to them later.                                                                                           |
| 🖥️ **Rendering**            | Render the camera path to video through FFmpeg, with configurable resolution, frame rate and bitrate. Works on both the OpenGL and Vulkan backends.             |
| 🌐 **360° and stereoscopic** | Besides the default render, there are stereoscopic, cubic and equirectangular modes. ODS (VR video) needs an Iris shader pack and the OpenGL backend.           |
| ⚡ **Quick Mode**             | A faster replay mode for scrubbing through long recordings.                                                                                                     |

## 📦 Supported versions

| Minecraft      | Loader | Status |
|----------------|---|---|
| 26.3           | NeoForge | ✅ Available (beta) |
| 1.20.1         | Forge | 🛠️ Planned |
| Other versions | NeoForge / Forge | 🛠️ Planned |

Each build targets one Minecraft version and one loader, so pick the jar that matches your game.

**You also need**
- The **Java** version your Minecraft version requires.
- **FFmpeg** for video rendering. SceneCraft uses the default `ffmpeg` executable unless you set your own in the render settings.

## Contributing to SceneCraft
If you want to contribute to SceneCraft, read [workflow](docs/CONTRIBUTING.md).

## 🚀 Installation

1. Install the mod loader for your Minecraft version.
2. Download the jar that matches your Minecraft version and loader from the releases page.
3. Put the jar in your `mods` folder.
4. Start the game. Replays are saved automatically while you play.

## 🎮 Quick start

1. **Record.** Join a world or a server and play normally. SceneCraft records in the background.
2. **Replay.** Open the SceneCraft button on the title screen, pick a recording, and the viewer opens.
3. **Plan your shot.** In the editor, move the camera and add position keyframes (and time keyframes if you want to change speed). Left-click empty space on the lower timeline to move the cursor, click a keyframe to select it, double-click to edit it, and drag to move it. Right-click a keyframe to snap the camera to it.
4. **Render.** Open the render settings, pick resolution, frame rate and method, and start the render. The video file is saved when it finishes.

All actions can be rebound under **Options → Controls** (SceneCraft category): play/pause, add marker, position / time / both keyframes, path preview, camera roll and tilt reset, thumbnail capture, quick mode, settings, and more.

## 🛠️ Building from source

```bash
git clone <this repository>
cd SceneCraft
./gradlew build
```

The jar appears in `build/libs/`. To start a development client, run the `runClient` Gradle task (or the equivalent run configuration in your IDE).

You need the JDK that matches the target Minecraft version.

## ⚠️ Known issues

- **ODS (VR) rendering** needs Iris, which only exists for OpenGL.
- Some **26.3 packets are not mapped** in the studio library yet; unmapped packets pass through unchanged.
- **EXR export** is not tested yet.
- Languages other than English may still show older translations or fall back to English for new strings.
- Do not minimize the window while rendering.

Found something else? Please open an issue with your `latest.log` and, if there was a crash, the crash report.

## 🧭 Vision

SceneCraft starts from ReplayMod's proven record, replay and render workflow, but it is not meant to stay a straight port forever.

- **Now (beta → 1.0.0):** a stable, well-tested port. Same familiar workflow, running on current Minecraft and mod loaders, with fixes and polish along the way.
- **After 1.0.0:** a rework of the architecture and new features that fit modern Minecraft and modern tooling, rather than carrying over every design decision of the original. Details will be announced as they are decided, and may change.

Existing replays and workflows are a priority when this happens, so the stable release is intended as the solid base to build on.

## 🗺️ Roadmap

**Beta (0.x)**
- Support for more Minecraft versions
- Full mapping of Minecraft packets
- Testing and fixes for known issues (EXR, translations, and more)

**Toward 1.0.0 (stable)**
- SceneCraft official logo
- Forge support
- Access Transformer migration
- Translation updates
- Cleanup of temporary debug code and wider testing across OpenGL and Vulkan

**After 1.0.0**
- Architecture overhaul
- New, modern features beyond what ReplayMod offers

## 🙏 Credits

- **[ReplayMod](https://github.com/ReplayMod/ReplayMod)** by johni0702, CrushedPixel and contributors. SceneCraft is built on their work.
- **jGui (MIT)** and **ReplayStudio (GNU Lesser v3.0)** by johni0702 and contributors, the GUI toolkit used for the editor screens.

## 📄 License

SceneCraft is licensed under the **GNU General Public License v3.0**. See [LICENSE.md](LICENSE.md). Bundled components keep their own licenses and copyright notices.
