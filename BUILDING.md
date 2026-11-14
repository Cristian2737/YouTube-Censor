# Building YouTube Censor

Consumer installation needs only Android; these steps are for developers.

## Dependencies

Git, Python 3, JDK 21, Android SDK (as required by the pinned upstream Gradle build), and access to upstream Maven/GitHub Packages dependencies. Use your own `GITHUB_TOKEN` if a GitHub Packages dependency requires authentication; never commit it. The GitHub Actions workflow supplies its ephemeral job token with packages-read permission. The Gradle wrapper is taken from the pinned upstream source.

```sh
git clone https://github.com/Cristian2737/YouTube-Censor.git
cd YouTube-Censor
git clone https://github.com/MorpheApp/morphe-patches.git upstream
git -C upstream checkout 44031c561ea048c8f196877f30a49c13bfd6afb6
python3 apply_to_morphe.py upstream
cd upstream
./gradlew buildAndroid --no-daemon
```

On Windows use `gradlew.bat`. Set `JAVA_HOME` and `ANDROID_HOME` to your installations. The output is the `.mpp` under `upstream/patches/build/libs`. Upstream anchors are checked before applying modifications; do not apply this installer to an arbitrary newer upstream revision. Reapplying it to the same checkout is supported; use a fresh checkout for a newer overlay.

## Tests and release

Word-manager regression entry points: `tests/ProfanityWordsTest.java` (pure rules/migration) and `python3 tests/run_word_editor_tests.py` (actual preference callbacks with host view doubles).

Channel regression entry points: `tests/ProfanityChannelsTest.java` and `python3 tests/run_channel_editor_tests.py`. `ProfanityRuntimeTest` checks immediate audio restoration after whitelist changes using the real filter/volume code with host doubles.

The [release workflow](.github/workflows/release.yml) runs the rule, ContentAngel-reference timing, runtime lifecycle and Android-web-caption host checks before compiling the bundle. It pins upstream commit `44031c561ea048c8f196877f30a49c13bfd6afb6` (v2.2.0) and verifies the SHA-256 of the host-only org.json test dependency. No extra JSON dependency is added to the Android app.

A published release includes `youtube-censor.mpp`, `SHA256SUMS`, LICENSE, NOTICE and `youtube-censor-corresponding-source.tar.gz`. That source archive contains the complete modified upstream build source plus this overlay, tests and build scripts, excluding generated build artifacts and credentials. GitHub's automatic source archives contain the overlay repository only; use the corresponding-source asset for the expanded build tree.

For a new version, update the version in the installer, feed, catalog generator and workflow. Create and push a lightweight version tag on the exact commit using a maintainer account, then dispatch that commit's workflow with `publish_release` enabled. The workflow verifies the tag before publishing. GitHub's job token cannot always create a tag on a commit containing workflow changes; the initial release was uploaded using the maintainer CLI after the build and all tests passed. Already published versions must not be replaced with different builds. A release is marked alpha/prerelease even though its numeric bundle version is 0.10.0. `patches-bundle.json` supplies Manager's download URL. Do not distribute a prepatched YouTube APK or anyone's signing key.
