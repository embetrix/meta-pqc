FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'oqs', 'file://openssl-oqs.cnf', '', d)}"

RDEPENDS:${PN} += " ${@bb.utils.contains('DISTRO_FEATURES', 'oqs', 'oqs-provider', '', d)}"

do_install:append:class-target () {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'oqs', 'true', 'false', d)}; then
        install -m 0644 ${UNPACKDIR}/openssl-oqs.cnf ${D}${sysconfdir}/ssl/
        printf "\n# Enable oqs provider config\n.include /etc/ssl/openssl-oqs.cnf\n" >> ${D}${sysconfdir}/ssl/openssl.cnf
    fi
}

do_install:append:class-native () {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'oqs', 'true', 'false', d)}; then
        install -m 0644 ${UNPACKDIR}/openssl-oqs.cnf ${D}${sysconfdir}/ssl/
    fi
}

FILES:openssl-conf += "${sysconfdir}/ssl/openssl-oqs.cnf"
