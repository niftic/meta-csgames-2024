SUMMARY = "Debug root key"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://id_rsa.pub"

do_install () {
    if ${CSGAMES_FLAGS} ; then
        # Install key
        install -d -m 0500 ${D}${ROOT_HOME}/.ssh
        install -m 0400 ${WORKDIR}/id_rsa.pub ${D}${ROOT_HOME}/.ssh/authorized_keys
    fi
}

FILES:${PN} = "${ROOT_HOME}/.ssh"
