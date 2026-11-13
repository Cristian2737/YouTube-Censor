# Testing and release limits

## Runtime tested on Android

The v0.8 candidate was tested on a Motorola moto g play (2024), Android 14, YouTube 21.30.216. Video: https://www.youtube.com/watch?v=8_vTxsB3IYA (ibz).

- Android-only caption acquisition produced 3,263 word offsets, matching the reference schedule, without a desktop export or cookie transfer.
- The first checked mute window was 94,741?95,200 ms.
- A 216-sample integration pass observed muting within that window and restoration afterward, with the audio self-test disabled.
- The user confirmed: ?Yes?clean mute, following words audible.?
- The user's main installation was separately rebuilt with its 114 existing selected patches plus the filter; its signing identity and app data were retained. Main-app settings and Android caption acquisition were checked silently.

Sanitized machine-readable candidate evidence is in `tests/`. The hashes there identify the **earlier v0.8 candidate**, not this newly named 0.8.1 bundle. The public 0.8.1 packaging changes the patch/settings/source name and bundle version; its Java runtime is unchanged. Release checksums identify the new bundle.

## Automated checks

157 assertions: 35 rules/scheduling, 41 reference-timing cases, 34 runtime lifecycle/volume cases and 47 acquisition/parser cases. They check matching, narrow windows, volume composition, stale responses, bounded acquisition and richer-caption precedence. Host tests do not prove every native YouTube hook or device works.

## Still unverified

Only one v0.8 audible video/timing pass was confirmed. Broader v0.8 live seek/pause/channel/CC-hidden checks and more videos/devices remain to be tested. Earlier v0.7 observations are not counted as v0.8 results. Ads, Shorts, live streams, all languages and every upstream-compatible YouTube version have not been validated. Word timing and direct caption endpoints can change at YouTube's discretion.

For a listening test, pick a known profanity timestamp, confirm captions are usable, leave the five-second audio self-test off, and check both the word's removal and the audibility of the next words. Record video ID, timestamp, caption type/language, settings and app version. Never publish raw request URLs containing account/session parameters.
