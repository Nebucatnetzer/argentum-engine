{
  lib,
  stdenv,
  gradle_9,
  jdk21_headless,
  makeWrapper,
}:

let
  gradle = gradle_9;
  root = ../.;

  # Gradle configures every project in settings.gradle.kts, so all of them must be present.
  gradleBuild = lib.fileset.unions [
    ../settings.gradle.kts
    ../build.gradle.kts
    ../gradle.properties
    ../gradle/libs.versions.toml
    ../buildSrc
    ../ai
    ../game-server
    ../gym
    ../gym-server
    ../gym-trainer
    ../mtg-sdk
    ../mtg-sdk-tooling
    ../mtg-search
    ../mtg-sets
    ../mtgish-tooling
    ../oracle-assay
    ../rules-engine
  ];

  # Tests and docs are not part of the jar. ai/CHANGELOG.md is, because the build bakes it in.
  withoutTestsAndDocs = lib.fileset.fromSource (
    lib.sources.cleanSourceWith {
      src = root;
      filter =
        path: type:
        let
          relativePath = lib.removePrefix "${toString root}/" path;
          isTestSources =
            type == "directory" && builtins.match ".*/src/(test|testFixtures|jvmTest)" relativePath != null;
          isDoc = lib.hasSuffix ".md" relativePath && relativePath != "ai/CHANGELOG.md";
        in
        !(isTestSources || isDoc);
    }
  );
in
stdenv.mkDerivation (finalAttrs: {
  pname = "argentum-game-server";
  version = "0-unstable";

  # Only the files that go into the jar, so other edits do not rebuild the server.
  src = lib.fileset.toSource {
    inherit root;
    fileset = lib.fileset.intersection gradleBuild withoutTestsAndDocs;
  };

  nativeBuildInputs = [
    gradle
    makeWrapper
  ];

  # Regenerate after a dependency or plugin change:
  # nix build .#game-server.mitmCache.updateScript && ./result
  mitmCache = gradle.fetchDeps {
    pkg = finalAttrs.finalPackage;
    data = ./game-server-deps.json;
  };

  # The Kotlin compile daemon talks to Gradle over local sockets.
  __darwinAllowLocalNetworking = true;

  # The sandbox has no network, so the foojay plugin must not download a JDK.
  gradleFlags = [
    "-Porg.gradle.java.installations.auto-download=false"
    "-Porg.gradle.java.installations.paths=${jdk21_headless.home}"
    # nixpkgs' dependency-recording task reads the project at execution time,
    # and the configuration cache does not allow that.
    "--no-configuration-cache"
  ];

  gradleBuildTask = ":game-server:bootJar";

  # CI runs the test suite.
  doCheck = false;

  installPhase = ''
    runHook preInstall
    install -Dm644 game-server/build/libs/game-server.jar $out/share/argentum/game-server.jar
    makeWrapper ${jdk21_headless}/bin/java $out/bin/argentum-game-server \
      --add-flags "-jar $out/share/argentum/game-server.jar"
    runHook postInstall
  '';

  meta = {
    description = "Argentum Engine game server";
    mainProgram = "argentum-game-server";
  };
})
