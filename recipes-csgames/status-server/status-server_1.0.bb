SUMMARY = "Status server and client"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://client.jar;unpack=0 \
           file://server.jar;unpack=0 \
           file://status-server.service \
           file://flag.txt"

DEPENDS = "openssl-native"
RDEPENDS:${PN} = "openjre-8"

CSGAMES_FLAGS ?= "false"

inherit systemd useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--no-create-home \
                       --home /opt/status-server \
                       --shell /sbin/nologin \
                       --system \
                       -g status-server status-server"
GROUPADD_PARAM:${PN} = "--system status-server"

do_install() {
    # Install jars
    install -d ${D}/opt/status-server
    install -m 0440 -o root -g status-server ${WORKDIR}/client.jar ${D}/opt/status-server/
    install -m 0440 -o root -g status-server ${WORKDIR}/server.jar ${D}/opt/status-server/

    # Install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/status-server.service ${D}${systemd_system_unitdir}

    # Install flag
    if ${CSGAMES_FLAGS} ; then
        install -m 0440 -o root -g status-server ${WORKDIR}/flag.txt ${D}/opt/status-server/
    fi
}

SYSTEMD_SERVICE:${PN} = "status-server.service"
FILES:${PN} = "/opt/status-server/"
