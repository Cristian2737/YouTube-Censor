# Changelog

## 0.10.1 - simplified settings alpha

- Remove Always censor channels and its global-switch override; keep one whitelist. Legacy override values no longer enable censoring when the main switch is off.
- Reduce the main settings page to six entries. Group timing controls under Mute timing and expert/troubleshooting controls under Advanced settings.
- Remove duplicate preset switches and unused raw-word/channel labels from the interface. The word editor remains the single place to manage entries, preserving saved presets and word rules.
- Shorten repeated descriptions and clarify that caption masking follows the selected style.
- Retain all existing word timing, whitelist and user-volume behavior. Update runtime regression checks for the simplified enable/whitelist rules.

## 0.10.0 - channel whitelist alpha

- Add a separate shield action on channel pages using the shared browse and toolbar hooks, following Morphe's Channel search layout. Does not require selecting Channel search or replace its controls.
- Capture the viewed channel's stable ID, show checked state for a whitelist entry, and confirm additions/removals.
- Add a searchable whitelist editor with labels, editable IDs/names, per-row removal, Save/Cancel and legacy-setting compatibility.
- Apply audio exceptions immediately, retain user mute/ducking and keep Never censor priority over Always censor.
- Add 30 channel-model checks, 24 toolbar/editor callback checks, and five immediate-volume regression checks. Native phone UI verification remains pending.

## 0.9.0 - editable word list alpha

- Show the complete active list of default and custom words and phrases in a native editor.
- Add search, per-entry Edit/Remove, Add word or phrase, Save/Cancel and confirmed Restore Defaults.
- Preserve existing additions, exclusions, literal overrides and unchanged default word families without a settings-format migration.
- Move regex and preset switches into Advanced word rules; replace the raw custom-word/exclusion field on the primary screen.
- Keep mute scheduling and caption acquisition behavior unchanged; add 35 matching/migration assertions and 19 editor callback checks. New on-device UI testing remains pending.

## 0.8.1 - public alpha

- Publish the tested v0.8 Java runtime under the name **YouTube Censor**.
- Rename the selectable patch, settings screen and bundle; retain internal preference keys for compatibility.
- Add an Android remote-source feed, installation guide, CI tests, checksums and complete corresponding-source release asset.
- Preserve the upstream Morphe v2.2.0 base and required license/NOTICE.

The v0.8 runtime obtains rich caption timing directly on Android, prefers word offsets over sentence-only captions, and uses bounded fetching with cached fresh player configuration. This release remains experimental; see TESTING.md.
