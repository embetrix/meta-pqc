# Add drop-in configs and TLS groups patch
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://restart.conf \
            file://0001-Add-tls_groups-config-option-and-log-TLS-key-exchang.patch \
            "

do_install:append() {
    echo "include_dir /etc/mosquitto/conf.d" >> ${D}${sysconfdir}/mosquitto/mosquitto.conf

    install -d ${D}${systemd_unitdir}/system/mosquitto.service.d
    install -m 0644 ${UNPACKDIR}/restart.conf ${D}${systemd_unitdir}/system/mosquitto.service.d/
}

FILES:${PN} += "${systemd_unitdir}/system/mosquitto.service.d"
