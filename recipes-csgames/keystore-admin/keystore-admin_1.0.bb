SUMMARY = "DB admin user setup"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

USER = "caladium"

SRC_URI = "file://id_rsa \
           file://id_rsa.pub \
           file://populate.py \
           file://flag.txt \
           file://banner \
           file://keystore-admin.cron"

DEPENDS = "openssl-native"
RDEPENDS:${PN} = "python3-requests openssh cronie"

CSGAMES_FLAGS ?= "false"

inherit useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-g ${USER} ${USER}"
USERADD_PARAM:${PN} = "--create-home \
                       --shell /bin/sh \
                       --groups adm \
                       --password '$(openssl passwd -1 b6ef5cd39f3bbb6ff1862e918067e97c)' \
                       -g ${USER} ${USER}"
GROUPADD_PARAM:${PN} = "${USER}"

do_install () {
    # Install key
    install -d -m 0550 -o root -g ${USER} ${D}/home/${USER}/.ssh
    install -m 0440 -o root -g ${USER} ${WORKDIR}/id_rsa ${D}/home/${USER}/.ssh/id_rsa
    install -m 0440 -o root -g ${USER} ${WORKDIR}/id_rsa.pub ${D}/home/${USER}/.ssh/id_rsa.pub
    install -m 0440 -o root -g ${USER} ${WORKDIR}/id_rsa.pub ${D}/home/${USER}/.ssh/authorized_keys

    # Install banner
    install -d ${D}/etc/ssh
    install -m 0644 ${WORKDIR}/banner ${D}/etc/ssh/${USER}_banner

    if ${CSGAMES_FLAGS} ; then
        # Install script
        install -d -m 0550 ${D}/root/scripts/keystore-admin
        install -m 0440 ${WORKDIR}/populate.py ${D}/root/scripts/keystore-admin
        install -m 0440 ${WORKDIR}/flag.txt ${D}/root/scripts/keystore-admin

        # Install cronjob
        install -d ${D}${sysconfdir}/cron.d
        install -m 0440 ${WORKDIR}/keystore-admin.cron ${D}${sysconfdir}/cron.d
    fi
}

pkg_postinst:${PN}() {
    # Fix permissions
    chmod 750 $D/home/${USER}
    chown -R root:${USER} $D/home/${USER}
    # Install banner
    echo -e "\nMatch User ${USER}\n\tBanner /etc/ssh/${USER}_banner" >> $D/etc/ssh/sshd_config
}

FILES:${PN} = "/home/${USER} \
               /etc/ssh/${USER}_banner \
               /root/scripts/keystore-admin \
               ${sysconfdir}/cron.d/keystore-admin.cron"
