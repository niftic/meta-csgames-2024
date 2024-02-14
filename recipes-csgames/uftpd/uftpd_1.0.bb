SUMMARY = "uFTP server"
HOMEPAGE = "https://www.uftpserver.com/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "git://github.com/kingk85/uFTP;branch=master;protocol=https \
           file://uftpd.cfg \
           file://uftpd.service \
           file://flag.txt \
           file://plants/ "

SRCREV = "345828b5768af8613cc8109ee2f9b857f8a7b5f5"

S = "${WORKDIR}/git"

CSGAMES_FLAGS ?= "false"

inherit systemd useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system \
                       --no-create-home \
                       --home-dir ${localstatedir}/ftp \
                       --shell /sbin/nologin \
                       -g ftp ftp "
GROUPADD_PARAM:${PN} = "--system ftp"

TARGET_CC_ARCH += "${LDFLAGS}"
do_compile() {
    oe_runmake "CC=${CC}"
}

do_install() {
    # Install binary
    install -d ${D}${bindir}
    install -m 0755 build/uFTP ${D}${bindir}

    # Install files
    install -d -m 0550 -o root -g ftp ${D}${localstatedir}/ftp
    install -m 0440 -o root -g ftp ${WORKDIR}/plants/* ${D}${localstatedir}/ftp
    if ${CSGAMES_FLAGS} ; then
        install -m 0440 -o root -g ftp ${WORKDIR}/flag.txt ${D}${localstatedir}/ftp
    fi

    # Install config
    install -d ${D}${sysconfdir}
    install -m 0600 ${WORKDIR}/uftpd.cfg ${D}${sysconfdir}
    sed -i -e 's#@LOCALSTATEDIR@#${localstatedir}#g' ${D}${sysconfdir}/uftpd.cfg

    # Install systemd service
    install -d ${D}/${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/uftpd.service ${D}/${systemd_system_unitdir}
    sed -i -e 's#@BINDIR@#${bindir}#g' ${D}/${systemd_system_unitdir}/uftpd.service
}

SYSTEMD_SERVICE:${PN} = "uftpd.service"
FILES:${PN} = "${bindir}/uFTP \
               ${localstatedir}/ftp \
               ${sysconfdir}/uftpd.cfg"
CONFFILES:${PN} = "${sysconfdir}/uftpd.cfg"
