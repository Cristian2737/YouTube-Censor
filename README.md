# YouTube Censor

Mute profanity in the Android YouTube app using caption word timing. Runs on your phone, works with automatic and creator captions, and lets you customize words, regular expressions, caption masking, and channel exceptions.

**Public alpha 0.10.1.** Includes an editable word list plus a channel-page whitelist button and searchable channel editor. Existing word-list choices and whitelist entries carry forward. Settings are grouped into Words, Whitelisted channels, Mute timing and Advanced settings; preset switches and Always censor are removed from the interface. The earlier v0.8 runtime passed a real-phone listening check; the editors and channel button passed native Android checks in 0.10.0. The simplified 0.10.1 screen and grouped menus also passed native Android checks. See [testing and limits](TESTING.md).

## Install on Android

1. Install [Morphe Manager from its official releases](https://github.com/MorpheApp/morphe-manager/releases/latest). For the tested YouTube version, use Android 10 or later and leave about 1-2 GB free for patching.
2. Open **[Add YouTube Censor to Morphe](https://morphe.software/add-source?github=Cristian2737/YouTube-Censor&name=YouTube%20Censor&app=com.google.android.youtube)** on your phone and confirm. Alternatively, in Manager choose **Sources > + > Remote** and enter `https://github.com/Cristian2737/YouTube-Censor`. Keep YouTube enabled for this source.
3. Use **YouTube Censor as the source for YouTube**. This bundle includes the upstream Morphe patches as well as this patch; choose your other desired patches from this bundle too. Do not combine duplicate copies of the same patches from both bundles in one patching job. The official source can still serve other apps.
4. Select a clean, supported YouTube APK. **21.30.216 is the version tested here.** Obtain it yourself from a source you trust; no YouTube APK is distributed here. In the patch list, select **YouTube Censor** along with the other patches you want, then patch and install. For non-root installation, retain **GmsCore support** and follow [Morphe's installation guide](https://github.com/MorpheApp/morphe-manager/blob/main/docs/patching-simple-mode.md) for the required GmsCore app.
5. In the patched app, open **Settings > Morphe > Player > YouTube Censor**. Under **Mute timing**, leave **Fetch word timing on this device** enabled. Restart the app after changing settings when prompted. Keep **Advanced settings > Test audio muting (first 5 seconds)** off for normal viewing.

If the add-source link does not launch Manager, use the manual Remote method above. If your Manager hides alpha releases, enable **Pre-release patches** for this source. If remote import is unavailable, download `youtube-censor.mpp` from [Releases](https://github.com/Cristian2737/YouTube-Censor/releases/tag/v0.10.1) and add it using **Sources > + > Local**; local sources require manual updates.

### Updating an existing patched YouTube app

Save your patch selection/options and **back up your Manager signing key before changing anything**. Repatch a clean original APK with the desired patches from this bundle. To update the installed app in place and retain its data, use the same package name and signing key as the existing installation. Android rejects an update signed with a different key. This repository has no access to your key. See [Manager backups and keystore](https://github.com/MorpheApp/morphe-manager/blob/main/docs/backup-and-keystore.md).

## Editing the censored word list

Open **Settings > Morphe > Player > YouTube Censor > Censored words and phrases**. The list shows active defaults together with your existing custom words; previously excluded defaults remain excluded.

- Search to find an entry. Tap **Edit** to change it or **Remove** to stop censoring it.
- Tap **Add word or phrase** for a new entry. Type ordinary text; regex punctuation is treated literally.
- **Save** commits the list; **Cancel** discards the whole draft. Restart YouTube after saving so already-loaded caption schedules use your changes.
- **Restore defaults** stages the full common-word and irreverent-expression presets. It takes effect only when you Save. Regex and channel settings are retained.

Unchanged built-in entries retain their existing related-word matching, identified in the editor. Newly added words and phrases are literal entries. YouTube's `[ __ ]` caption markers are always filtered, even with an empty word list. Optional regex rules are separate under **Advanced settings**; they can still match words you removed from the ordinary list.

## Whitelisting channels

On a channel page, tap the **shield +** button beside the toolbar search actions, then **Whitelist**. It uses the channel you are viewing, even if a different channel's video is playing. An optional label helps identify the entry in settings; new channel-page entries use a stable channel ID. A **checked shield** means the channel is whitelisted; tap it to remove that exception. Channel search remains available and is not required for this button to work.

Open **Settings > Morphe > Player > YouTube Censor > Whitelisted channels** to search, add, edit or remove entries. You can change a readable label without changing its channel ID. **Save** applies the draft; **Cancel** discards it. Manual additions accept a channel ID, a YouTube `/channel/…` URL, or an exact display name. `@handles` are not resolved. Older name-based exceptions remain supported.

Whitelist changes immediately refresh the censor's audio-mute decision. The main Enable switch controls all other channels; the obsolete Always censor override has been removed. They preserve your own mute and volume settings. Already-rendered masked caption text may require reopening the video to refresh. Labels are stored locally; adding a channel does not make a new network request.

## Features

- Full editable word list, default matching families, optional advanced regex rules and word boundaries.
- Precise word-timed mutes when YouTube supplies offsets; short estimated mutes or broader fallback for sentence-only captions.
- Optional caption masking: asterisks, replacement text, hidden words or blocks.
- Channel-page whitelist button and editable channel list; stable IDs with readable labels. The main Enable switch controls all non-whitelisted channels.
- Word timing fetched directly on Android, including when on-screen captions are hidden. No desktop export, companion server, AI model, subscription or paid API.

## Limits and privacy

Captions must contain usable timing and text. Missing, inaccurate, already-redacted or inaccessible captions can cause missed words or imperfect timing. Videos without usable captions cannot be filtered. This does not analyze the audio or promise a child-safe video. Ads, live streams and Shorts are not validated by the current release.

The patch requests public video/player configuration and caption data **directly from YouTube**. It adds no analytics or third-party relay. YouTube still receives those requests, and other selected patches retain their own behavior. Diagnostics are optional. See [PRIVACY.md](PRIVACY.md).

## Source, testing and support

- [Release downloads and checksums](https://github.com/Cristian2737/YouTube-Censor/releases)
- [Build from source](BUILDING.md) - [Testing evidence and limitations](TESTING.md) - [Changelog](CHANGELOG.md)
- [Report a problem](https://github.com/Cristian2737/YouTube-Censor/issues): include Android/YouTube/Manager versions, video link, timestamp, caption language, settings and what happened. Do not post cookies, account tokens, signing keys or full device logs.

Independent project maintained by Cristian2737, derived from [Morphe patches v2.2.0](https://github.com/MorpheApp/morphe-patches/tree/44031c561ea048c8f196877f30a49c13bfd6afb6). Not an official Morphe, YouTube or ContentAngel release. ContentAngel was a behavior/timing reference; no ContentAngel extension code is included. Licensed under [GPLv3](LICENSE) with the preserved [Morphe NOTICE](NOTICE). No affiliation or trademark rights are claimed.
