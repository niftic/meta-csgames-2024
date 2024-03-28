SUMMARY = "Filebrowser web based file browser"
HOMEPAGE = "https://filebrowser.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=6f7f172b8cd1d9eecd0462bffef34c41"

SRC_URI = "https://github.com/filebrowser/filebrowser/releases/download/v${PV}/linux-arm64-filebrowser.tar.gz;name=filebrowser \
           file://filebrowser.service \
           file://filebrowser.db \
           file://flag.txt \
           file://icons/ \
           file://manifesto/ \
           file://logo.svg"

SRC_URI[filebrowser.sha256sum] = "fd8de3bcc35307a0b662a86fdb51b2a511eff18f3b912e96b89aec3ad8827866"

CSGAMES_FLAGS ?= "false"

RDEPENDS:${PN} = "glibc-utils"

inherit systemd useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--no-create-home \
                       --home-dir ${localstatedir}/filebrowser \
                       --shell /sbin/nologin \
                       --system \
                       -g filebrowser filebrowser"
GROUPADD_PARAM:${PN} = "--system filebrowser"

do_install() {
    # Install binary
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/filebrowser ${D}${bindir}

    # Install config
    install -d ${D}${sysconfdir}/filebrowser
    install -m 0660 -o root -g filebrowser ${WORKDIR}/filebrowser.db ${D}${sysconfdir}/filebrowser
    install -d ${D}${sysconfdir}/filebrowser/branding/img/icons
    install -m 0644 ${WORKDIR}/logo.svg ${D}${sysconfdir}/filebrowser/branding/img
    install -m 0644 ${WORKDIR}/icons/* ${D}${sysconfdir}/filebrowser/branding/img/icons

    # Install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/filebrowser.service ${D}${systemd_system_unitdir}
    sed -i -e 's#@BINDIR@#${bindir}#g' ${D}${systemd_system_unitdir}/filebrowser.service
    sed -i -e 's#@SYSCONFDIR@#${sysconfdir}#g' ${D}${systemd_system_unitdir}/filebrowser.service
    sed -i -e 's#@LOCALSTATEDIR@#${localstatedir}#g' ${D}${systemd_system_unitdir}/filebrowser.service

    # Install browsable files
    install -d -m 0550 -o root -g filebrowser ${D}${localstatedir}/filebrowser
    install -m 0440 -o root -g filebrowser ${WORKDIR}/manifesto/manifesto.txt ${D}${localstatedir}/filebrowser/
    install -d -m 0550 -o root -g filebrowser ${D}${localstatedir}/filebrowser/takeover_phases
    install -m 0440 -o root -g filebrowser ${WORKDIR}/manifesto/takeover_phases/* ${D}${localstatedir}/filebrowser/takeover_phases/
    install -d -m 0550 -o root -g filebrowser ${D}${localstatedir}/filebrowser/scripts
    install -m 0440 -o root -g filebrowser ${WORKDIR}/manifesto/scripts/* ${D}${localstatedir}/filebrowser/scripts/

    # Install flag
    install -d -m 0550 -o root -g filebrowser ${D}${localstatedir}/local/filebrowser
    if ${CSGAMES_FLAGS} ; then
        install -m 0440 -o root -g filebrowser ${WORKDIR}/flag.txt ${D}${localstatedir}/local/filebrowser
    fi
}

INSANE_SKIP:${PN} += "already-stripped"

SYSTEMD_SERVICE:${PN} = "filebrowser.service"
FILES:${PN} = "${bindir}/filebrowser \
               ${localstatedir}/filebrowser \
               ${localstatedir}/local/filebrowser \
               ${sysconfdir}/filebrowser"
CONFFILES:${PN} = "${sysconfdir}/filebrowser/filebrowser.db"
