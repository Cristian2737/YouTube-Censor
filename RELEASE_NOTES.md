# YouTube Censor 0.10.0 - channel whitelist alpha

Whitelist the channel you are viewing with the new **shield +** toolbar action. A checked shield means the channel is exempt; tap it to remove the exception. This works alongside the existing Channel search action and does not require that patch to be selected.

Manage entries under **Settings > Morphe > Player > YouTube Censor > Whitelisted channels**: search, add, edit IDs or readable labels, remove, Save and Cancel. Existing channel exceptions are preserved. Channel-page additions use stable IDs. Audio mute decisions refresh immediately while preserving your own mute and volume choices. No new network request is needed to add an entry.

See and customize the full active word list on Android. Open **Settings > Morphe > Player > YouTube Censor > Censored words and phrases**.

- Search your active default and custom words and phrases.
- Add, edit or remove entries using plain text. No regex required.
- Save commits your edits; Cancel discards them. Restore Defaults is a draft until saved.
- Existing additions, exclusions and literal overrides carry forward. Unchanged default matching families keep their behavior.
- Optional regex and preset switches are now under Advanced word rules. Regex and channel choices are preserved.

Restart YouTube after saving changes to refresh already-loaded caption schedules.

**[Add source to Morphe on Android](https://morphe.software/add-source?github=Cristian2737/YouTube-Censor&name=YouTube%20Censor&app=com.google.android.youtube)** - [Installation guide](https://github.com/Cristian2737/YouTube-Censor#install-on-android)

Select **YouTube Censor** from this bundle alongside your desired upstream patches. This is a full Morphe-derived bundle; avoid applying duplicate stock/fork patches in the same job. Tested with YouTube **21.30.216**, Android 14. For an in-place app update, retain your original package name and signing key.

Update the source in Morphe Manager, then repatch a clean supported YouTube APK with **YouTube Censor** and your existing patches. Updating a patch source alone does not modify an installed app.

Automated suites cover 270 assertions, including channel navigation, preserving search actions, stable IDs, editing labels, Save/Cancel and immediate audio restoration. Toolbar/editor callbacks use Android view doubles. The earlier v0.8 runtime passed a phone listening check. New interfaces still need real-phone UI testing; no phone was connected during development. Missing or unusable captions still cannot be filtered. No audio recognition, desktop export or project-operated service.

Assets:
- **youtube-censor.mpp** - import into Morphe Manager, not an APK.
- **SHA256SUMS** - SHA-256 integrity checks.
- **youtube-censor-corresponding-source.tar.gz** - complete modified upstream source plus overlay, build scripts and tests.
- **LICENSE / NOTICE** - required source and binary notices; preserved from upstream.

Independent from Morphe, YouTube and ContentAngel. No YouTube APKs or signing keys are distributed.
