FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:${THISDIR}/files:"

SRC_URI:append = " \
                  file://httpd.conf"

do_install:append() {
    install -d ${D}${sysconfdir}/apache2
    install -m 0644 ${WORKDIR}/httpd.conf ${D}${sysconfdir}/${BPN}/httpd.conf
}
