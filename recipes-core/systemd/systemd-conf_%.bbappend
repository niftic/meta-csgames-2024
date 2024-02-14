FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
                  file://wired.network"

do_install:append() {
    if [ -z "${SYSTEMD_IP}" ] ; then
        # Use DHCP
        echo "DHCP=yes" >> ${D}${systemd_unitdir}/network/80-wired.network
    else
        # Use static IP
        GATEWAY=$(echo ${SYSTEMD_IP} | awk 'BEGIN {FS=".";}{print $1 "." $2 "." $3}').1
        echo "Address=${SYSTEMD_IP}" >> ${D}${systemd_unitdir}/network/80-wired.network
        echo "Gateway=${GATEWAY}" >> ${D}${systemd_unitdir}/network/80-wired.network
        echo "DNS=${GATEWAY}" >> ${D}${systemd_unitdir}/network/80-wired.network
    fi
}
