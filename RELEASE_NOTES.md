# YouTube Censor 0.8.1 ? public alpha

Mute caption-timed profanity on Android, including automatic captions. Customize words/phrases, regex, caption masking and channel exceptions. No computer, desktop export, AI model or project-operated service is required.

**[Add source to Morphe on Android](https://morphe.software/add-source?github=Cristian2737/YouTube-Censor&name=YouTube%20Censor&app=com.google.android.youtube)** ? [Installation guide](https://github.com/Cristian2737/YouTube-Censor#install-on-android)

Select **YouTube Censor** from this bundle alongside your desired upstream patches. This is a full Morphe-derived bundle; avoid applying duplicate stock/fork patches in the same job. Tested with YouTube **21.30.216**, Android 14. For an in-place app update, retain your original package name and signing key.

The Java runtime is unchanged from the v0.8 candidate that passed the user's listening check: clean profanity mute, following words audible. Automated suites cover 157 assertions. Packaging renames the patch/settings/source and adds public distribution. This is still an alpha: one audible candidate pass, broader live/device coverage pending. No usable captions means no filtering; inaccurate/redacted captions can miss words. Not an audio profanity recognizer or a child-safety guarantee.

Assets:
- **youtube-censor.mpp** ? import into Morphe Manager, not an APK.
- **SHA256SUMS** ? SHA-256 integrity checks.
- **youtube-censor-corresponding-source.tar.gz** ? complete modified upstream source plus overlay, build scripts and tests.
- **LICENSE / NOTICE** ? required source and binary notices; preserved from upstream.

Independent from Morphe, YouTube and ContentAngel. No YouTube APKs or signing keys are distributed.
