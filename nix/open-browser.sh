url="$1"

if [[ -n "${WSL_DISTRO_NAME:-}" ]]; then
  # explorer.exe exits with 1 even when it opened the URL.
  explorer.exe "$url" || true
elif [[ -n "${DEFAULT_BROWSER:-}" ]]; then
  "$DEFAULT_BROWSER" "$url"
else
  echo "DEFAULT_BROWSER is not set. Open $url in your browser."
fi
