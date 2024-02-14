FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
                  file://enable-netcat-listener.cfg \
                  file://ps-add-columns.cfg"
