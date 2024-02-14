SUMMARY = "Flag for extracting the filesystem"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://flag.txt"

do_install () {
    # Install flag
    if ! ${CSGAMES_FLAGS} ; then
        install -m 0400 ${WORKDIR}/flag.txt ${D}
    fi
}

FILES:${PN} = "/flag.txt"
