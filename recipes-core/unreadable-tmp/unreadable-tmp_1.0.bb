SUMMARY = "Make /tmp directory not readable"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

pkg_postinst_ontarget:${PN} () {
    chmod 1773 /tmp
}
