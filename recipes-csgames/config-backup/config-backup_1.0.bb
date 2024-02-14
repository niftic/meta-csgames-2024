SUMMARY = "Encrypted configuration backup/restore API"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://app/ \
           file://config-backup.service \
           file://flag.txt"

CSGAMES_FLAGS ?= "false"

RDEPENDS:${PN} = "python3 python3-flask python3-pycryptodome"

inherit systemd

do_install() {
    # Install application
    install -d ${D}/opt/config-backup
    install -m 0440 ${WORKDIR}/app/*.{py,json,pem} ${D}/opt/config-backup

    # Install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/config-backup.service ${D}${systemd_system_unitdir}

    # Install flag
    if ${CSGAMES_FLAGS} ; then
        install -m 0440 ${WORKDIR}/flag.txt ${D}/opt/config-backup
    fi
}

SYSTEMD_SERVICE:${PN} = "config-backup.service"
FILES:${PN} += " \
                /opt/config-backup"
