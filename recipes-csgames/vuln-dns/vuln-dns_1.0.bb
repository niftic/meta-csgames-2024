SUMMARY = "Vulnerable DNS server - DO NOT USE"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e12c025f33913f2167514339be62c3fd"

SRC_URI = "git://github.com/mwarning/SimpleDNS;branch=master;protocol=https \
           file://make-vulnerable.patch \
           file://simpleDNS.service \
           file://flag.txt"

SRCREV = "81cc8c5fb75f29ce36f6acdd192cd5e038784eae"

S = "${WORKDIR}/git"

DEPENDS += " openssl"
RDEPENDS:${PN} = "bind-utils"

CSGAMES_FLAGS ?= "false"

inherit systemd useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--no-create-home \
                       --shell /sbin/nologin \
                       --system \
                       -g dns dns"
GROUPADD_PARAM:${PN} = "--system dns"

# Disable make clean
do_configure[noexec] = "1"

do_compile() {
    oe_runmake
}

do_install() {
    # Install binary
    install -d ${D}${bindir}
    install -m 0755 main ${D}${bindir}/simpleDNS

    # Install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/simpleDNS.service ${D}${systemd_system_unitdir}
    sed -i -e 's#@BINDIR@#${bindir}#g' ${D}${systemd_system_unitdir}/simpleDNS.service

    # Install flag
    install -d -m 0550 -o root -g dns ${D}${localstatedir}/local/dns
    if ${CSGAMES_FLAGS} ; then
        install -m 0440 -o root -g dns ${WORKDIR}/flag.txt ${D}${localstatedir}/local/dns
    fi
}

pkg_postinst:${PN}() {
    # Set DNS to CIRA
    rm -f $D/etc/resolv.conf
    echo -e "nameserver 149.112.121.10\nnameserver 149.112.122.10" > $D/etc/resolv.conf
}

INHIBIT_PACKAGE_STRIP = "1"

FILES:${PN} = "${bindir}/simpleDNS \
               ${localstatedir}/local/dns"
SYSTEMD_SERVICE:${PN} = "simpleDNS.service"
