# Scratch Assets

This folder contains the Scratch build files provided by the Scratch team.

## How to deploy Scratch for a release build

Run this from the project root before building:

```bash
unzip -o scratch-assets/build-wo-remix.zip -d /tmp/scratch-build && cp -r /tmp/scratch-build/build-wo-remix/* app/src/main/assets/
```

## Why assets are not in git

Scratch static assets are large and only change when the Scratch team provides a new build. Committing them would bloat the repo. Extract locally before building.

## Updating

Replace `scratch-assets/build-wo-remix.zip` with the new zip from the Scratch team and re-run the command above.
