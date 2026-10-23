{
  description = "Argentum Engine: Magic: The Gathering rules engine and online play platform";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs =
    { self, nixpkgs }:
    let
      systems = [
        "x86_64-linux"
        "aarch64-linux"
        "x86_64-darwin"
        "aarch64-darwin"
      ];
      forAllSystems =
        function: nixpkgs.lib.genAttrs systems (system: function nixpkgs.legacyPackages.${system});
      revision = self.shortRev or self.dirtyShortRev or "unknown";
    in
    {
      packages = forAllSystems (
        pkgs:
        let
          game-server = pkgs.callPackage ./nix/game-server.nix { };
          web-client = pkgs.callPackage ./nix/web-client.nix { commitHash = revision; };
        in
        {
          inherit game-server web-client;
          default = pkgs.callPackage ./nix/launcher.nix {
            inherit game-server web-client;
            appVersion = revision;
          };
        }
      );

      devShells = forAllSystems (pkgs: {
        default = pkgs.mkShell {
          packages = [
            pkgs.jdk21
            pkgs.nodejs_22
            pkgs.just
            pkgs.python3
            pkgs.git
            pkgs.process-compose
          ];
          JAVA_HOME = pkgs.jdk21.home;
        };
      });

      formatter = forAllSystems (pkgs: pkgs.nixfmt);
    };
}
