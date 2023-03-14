ProtonMail for Android
=======================
Copyright (c) 2023 Proton Technologies AG

## Build instructions
- Install and configure the environment (two options available)
    - [Android Studio bundle](https://developer.android.com/studio/install)
    - [Standalone Android SDK](https://android-doc.github.io/sdk/installing/index.html?pkg=tools)
- Install and configure Java 11 (not needed for Android Studio bundle as it's included)
    - Install java11 with `brew install java11` | `apt install openjdk-11-jdk`
    - Set Java 11 as the current version by using the `JAVA_HOME` environment variable.
- Clone this repository (Use `git clone [url]`.).
- Build with any of the following: 
  - Execute `./gradlew assembleDevDebug` in a terminal.
  - Execute `bundle exec fastlane assembleDevDebug` in a terminal.
  - Open Android Studio and build the `:app` module.

## CI / CD
CI stages are defined in the `.gitlab-ci.yml` file and we rely on [fastlane](https://docs.fastlane.tools/) to implement most of them.
Fastlane can be installed and used locally by performing
```
bundle install
```
(requires Ruby and `bundler` to be available locally)
```
bundle exec fastlane lanes
```
will show all the possible actions that are available.

## UI Tests
UI tests are executed on Firebase Test Lab through the CI. UI tests must run on a `dev` flavour (`devDebug` for instance).

While instrumented tests can be run locally with no additional setup, in order to run the tests located in the `app/src/uiTest` folder, some assets (`users.json` and `internal_api.json` for instance) might need to be downloaded and configured.

For more information, head over to **Confluence** -> **MAILAND space** -> **ProtonMail UI Tests setup**. 

## Deploy
Each merge to `master` branch builds the branch's HEAD and deploys it to [Firebase App Distribution](https://firebase.google.com/docs/app-distribution).

In order to invite someone as a tester for such builds, their email address needs to be added to the `v6-internal-alpha-testers` group on Firebase.

## Signing
All `release` builds done on CI are automatically signed with ProtonMail's keystore. In order to perform signing locally, the keystore will need to be placed into the `keystore/` directory and the credentials will be read from `private.properties` file.

## Observability
Crashes and errors that happen in `release` (non debuggable) builds are reported to Sentry in an anonymized form.
The CI sets up the integration with Sentry by providing in the build environment `private.properties` and `sentry.properties` files that contain the secrets needed. 
This can as well be performed locally by creating `private.properties` and `sentry.properties` files and filling them with the needed secrets (eg. `SentryDSN`; for more details about the `sentry.properties` file, see https://docs.sentry.io/platforms/android/gradle/#proguardr8--dexguard).

## Use core libraries from local git submodule
It is possible to run the application getting the "core" libraries from the local git submodule instead of gradle by setting the following flag to true in `gradle.properties` file:

```
useCoreGitSubmodule=true
```


## Code style
This project's code style and formatting is checked by detekt. The rule set is [ktlint's default one](https://github.com/pinterest/ktlint)


## Troubleshooting
- `goopenpgp.aar` library not found: submodule not properly setup, please follow steps in build instructions

License
-------
The code and data files in this distribution are licensed under the terms of the GPLv3 as published by the Free Software Foundation. See https://www.gnu.org/licenses/ for a copy of this license.

