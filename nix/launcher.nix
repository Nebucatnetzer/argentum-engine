{
  writeShellApplication,
  git,
  process-compose,
  caddy,
  game-server,
  web-client,
  appVersion,
}:

let
  open-browser = writeShellApplication {
    name = "argentum-open-browser";
    text = builtins.readFile ./open-browser.sh;
  };
in
writeShellApplication {
  name = "argentum";
  runtimeInputs = [
    git
    process-compose
    caddy
    game-server
    open-browser
  ];
  # State follows the devenv layout: <project>/.devenv/state/<service>.
  # process-compose serves its own API on port 8080 by default, which is the game server port.
  text = ''
    project_root="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
    export ARGENTUM_STATE_DIR="''${ARGENTUM_STATE_DIR:-$project_root/.devenv/state}"
    mkdir --parents \
      "$ARGENTUM_STATE_DIR/game-server" \
      "$ARGENTUM_STATE_DIR/caddy" \
      "$ARGENTUM_STATE_DIR/process-compose"

    export ARGENTUM_PORT="''${ARGENTUM_PORT:-5173}"
    export ARGENTUM_SERVER_PORT="''${ARGENTUM_SERVER_PORT:-8080}"
    export ARGENTUM_WEB_ROOT="${web-client}"
    export ARGENTUM_CADDYFILE="${./Caddyfile}"
    export APP_VERSION="''${APP_VERSION:-${appVersion}}"
    exec process-compose up \
      --no-server \
      --log-file "$ARGENTUM_STATE_DIR/process-compose/process-compose.log" \
      --config ${./process-compose.yml} \
      "$@"
  '';
  meta.description = "Run the Argentum game server and web client on http://localhost:5173";
}
