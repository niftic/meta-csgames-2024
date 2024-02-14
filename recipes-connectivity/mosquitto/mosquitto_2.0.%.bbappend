FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
                  file://mosquitto.conf \
                  file://password_file \
                  file://publish.py \
                  file://flag.txt \
                  file://mosquitto.cron"

CSGAMES_FLAGS ?= "false"

do_install:append() {
    # Install mosquitto files
    install -m 0644 ${WORKDIR}/mosquitto.conf ${D}${sysconfdir}/mosquitto
    install -o root -g mosquitto -m 0440 ${WORKDIR}/password_file ${D}${sysconfdir}/mosquitto

    if ${CSGAMES_FLAGS} ; then
        # Install script
        install -d -m 0550 ${D}/root/scripts/mosquitto
        install -m 0440 ${WORKDIR}/publish.py ${D}/root/scripts/mosquitto
        install -m 0440 ${WORKDIR}/flag.txt ${D}/root/scripts/mosquitto

        # Install cronjob
        install -d ${D}${sysconfdir}/cron.d
        install -m 0440 ${WORKDIR}/mosquitto.cron ${D}${sysconfdir}/cron.d
    fi
}

FILES:${PN} += " \
                /root/scripts/mosquitto \
                ${sysconfdir}/cron.d/mosquitto.cron"
