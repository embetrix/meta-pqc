# Add drop-in configs
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://restart.conf \
           file://0001-Add-tls_groups-config-option-and-log-TLS-key-exchang.patch \
           "

do_install:append() {
    install -d ${D}${sysconfdir}/mosquitto/conf.d
    echo "include_dir /etc/mosquitto/conf.d" >> ${D}${sysconfdir}/mosquitto/mosquitto.conf

    install -d ${D}${systemd_unitdir}/system/mosquitto.service.d
    install -m 0644 ${WORKDIR}/restart.conf ${D}${systemd_unitdir}/system/mosquitto.service.d/
}

CONFFILES:${PN} += "${sysconfdir}/mosquitto/conf.d "

FILES:${PN} += "${systemd_unitdir}/system/mosquitto.service.d"
