# Changelog

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
