# YouTube Censor

Mute profanity in the Android YouTube app using caption word timing. Runs on your phone, works with automatic and creator captions, and lets you customize words, regular expressions, caption masking, and channel exceptions.

**Public alpha 0.8.1.** The v0.8 runtime passed a real-phone listening check: the profanity was muted and the following words remained audible. This release gives that same runtime its public name and distribution. Coverage is not guaranteed; see [testing and limits](TESTING.md).

## Install on Android

1. Install [Morphe Manager from its official releases](https://github.com/MorpheApp/morphe-manager/releases/latest). For the tested YouTube version, use Android 10 or later and leave about 1-2 GB free for patching.
2. Open **[Add YouTube Censor to Morphe](https://morphe.software/add-source?github=Cristian2737/YouTube-Censor&name=YouTube%20Censor&app=com.google.android.youtube)** on your phone and confirm. Alternatively, in Manager choose **Sources > + > Remote** and enter `https://github.com/Cristian2737/YouTube-Censor`. Keep YouTube enabled for this source.
3. Use **YouTube Censor as the source for YouTube**. This bundle includes the upstream Morphe patches as well as this patch; choose your other desired patches from this bundle too. Do not combine duplicate copies of the same patches from both bundles in one patching job. The official source can still serve other apps.
4. Select a clean, supported YouTube APK. **21.30.216 is the version tested here.** Obtain it yourself from a source you trust; no YouTube APK is distributed here. In the patch list, select **YouTube Censor** along with the other patches you want, then patch and install. For non-root installation, retain **GmsCore support** and follow [Morphe's installation guide](https://github.com/MorpheApp/morphe-manager/blob/main/docs/patching-simple-mode.md) for the required GmsCore app.
5. In the patched app, open **Settings > Morphe > Player > YouTube Censor**. Leave **Fetch word timing on this device** enabled. Restart the app after changing settings when prompted. Keep **Test audio muting (first 5 seconds)** off for normal viewing.

If the add-source link does not launch Manager, use the manual Remote method above. If your Manager hides alpha releases, enable **Pre-release patches** for this source. If remote import is unavailable, download `youtube-censor.mpp` from [Releases](https://github.com/Cristian2737/YouTube-Censor/releases/tag/v0.8.1) and add it using **Sources > + > Local**; local sources require manual updates.

### Updating an existing patched YouTube app

Save your patch selection/options and **back up your Manager signing key before changing anything**. Repatch a clean original APK with the desired patches from this bundle. To update the installed app in place and retain its data, use the same package name and signing key as the existing installation. Android rejects an update signed with a different key. This repository has no access to your key. See [Manager backups and keystore](https://github.com/MorpheApp/morphe-manager/blob/main/docs/backup-and-keystore.md).

## Features

- Default word list, custom words/phrases and regex rules; optional word boundaries.
- Precise word-timed mutes when YouTube supplies offsets; short estimated mutes or broader fallback for sentence-only captions.
- Optional caption masking: asterisks, replacement text, hidden words or blocks.
- Always-censor and never-censor channel lists; the never-censor list takes priority.
- Word timing fetched directly on Android, including when on-screen captions are hidden. No desktop export, companion server, AI model, subscription or paid API.

## Limits and privacy

Captions must contain usable timing and text. Missing, inaccurate, already-redacted or inaccessible captions can cause missed words or imperfect timing. Videos without usable captions cannot be filtered. This does not analyze the audio or promise a child-safe video. Ads, live streams and Shorts are not validated by the current release.

The patch requests public video/player configuration and caption data **directly from YouTube**. It adds no analytics or third-party relay. YouTube still receives those requests, and other selected patches retain their own behavior. Diagnostics are optional. See [PRIVACY.md](PRIVACY.md).

## Source, testing and support

- [Release downloads and checksums](https://github.com/Cristian2737/YouTube-Censor/releases)
- [Build from source](BUILDING.md) - [Testing evidence and limitations](TESTING.md) - [Changelog](CHANGELOG.md)
- [Report a problem](https://github.com/Cristian2737/YouTube-Censor/issues): include Android/YouTube/Manager versions, video link, timestamp, caption language, settings and what happened. Do not post cookies, account tokens, signing keys or full device logs.

Independent project maintained by Cristian2737, derived from [Morphe patches v2.2.0](https://github.com/MorpheApp/morphe-patches/tree/44031c561ea048c8f196877f30a49c13bfd6afb6). Not an official Morphe, YouTube or ContentAngel release. ContentAngel was a behavior/timing reference; no ContentAngel extension code is included. Licensed under [GPLv3](LICENSE) with the preserved [Morphe NOTICE](NOTICE). No affiliation or trademark rights are claimed.
