# YouTube Censor 0.9.0 - editable word list alpha

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

Automated suites cover 211 assertions, including 35 word-list/migration checks and 19 actual editor callback checks using Android view doubles. The earlier v0.8 runtime passed a phone listening check. This editor update still needs a new real-phone UI test; no phone was connected during development. Missing or unusable captions still cannot be filtered. No audio recognition, desktop export or project-operated service.

Assets:
- **youtube-censor.mpp** - import into Morphe Manager, not an APK.
- **SHA256SUMS** - SHA-256 integrity checks.
- **youtube-censor-corresponding-source.tar.gz** - complete modified upstream source plus overlay, build scripts and tests.
- **LICENSE / NOTICE** - required source and binary notices; preserved from upstream.

Independent from Morphe, YouTube and ContentAngel. No YouTube APKs or signing keys are distributed.
