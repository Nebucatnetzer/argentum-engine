{
  lib,
  buildNpmPackage,
  nodejs_22,
  commitHash,
}:

buildNpmPackage {
  pname = "argentum-web-client";
  version = "0.1.0";

  src = ../web-client;

  nodejs = nodejs_22;
  # Update after every package-lock.json change: build once and copy the hash from the error.
  npmDepsHash = "sha256-wXE7vUaSSWe5ICWej0CvizveF2tiOSHTXtb9NT4fs6o=";

  env = {
    VITE_COMMIT_HASH = commitHash;
    # Playwright is only for E2E tests. The build does not need browsers.
    PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD = "1";
  };

  installPhase = ''
    runHook preInstall
    cp -r dist $out
    runHook postInstall
  '';

  meta.description = "Argentum Engine web client (static build)";
}
