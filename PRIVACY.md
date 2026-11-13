# Privacy

YouTube Censor matches words and computes mute intervals on the Android device. It makes direct HTTPS requests to YouTube for public player configuration and timed captions; YouTube receives ordinary request metadata such as your network address. It does not send audio, your word list or your channel rules to an external analysis service. No AI model, analytics SDK, paid API or project-operated relay is added.

The runtime uses bounded in-process caption/configuration caches and an acquisition worker. Existing YouTube networking, login behavior and other installed patches are outside this patch's control. Optional diagnostics are intended for troubleshooting, not telemetry. Review issue attachments before posting: do not include account cookies/tokens, signing keys, complete UI dumps or raw caption URLs with session parameters.
