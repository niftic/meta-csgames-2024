do_configure:append() {
    # Disabling autostart of systemd-resolved
    sed -i -e "s/enable systemd-resolved.service/disable systemd-resolved.service/g" ${S}/presets/90-systemd.preset
}
