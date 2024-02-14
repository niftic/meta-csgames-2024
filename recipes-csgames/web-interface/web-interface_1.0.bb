SUMMARY = "Web configuration interface"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://flag.txt \
           file://website/ \
           file://cgi-bin"

CSGAMES_FLAGS ?= "false"

RDEPENDS:${PN} = "net-tools"

do_compile() {
    cd ${WORKDIR}/cgi-bin && oe_runmake
}

do_install() {
    # Install application
    install -d ${D}/var/www/html
    install -m 0644 ${WORKDIR}/website/*.{php,css} ${D}/var/www/html
    install -d ${D}/usr/libexec/apache2/modules/cgi-bin/
    install -m 0755 ${WORKDIR}/cgi-bin/diagnostic ${D}/usr/libexec/apache2/modules/cgi-bin/

    # Install flag
    if ${CSGAMES_FLAGS} ; then
        install -m 0440 ${WORKDIR}/flag.txt ${D}/var/www/html
    fi
}

pkg_postinst:${PN}() {
    if ${CSGAMES_FLAGS} ; then
        chown root:daemon $D/var/www/html/flag.txt
    fi
}

FILES:${PN} += " \
                /var/www/html"
