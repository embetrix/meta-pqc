FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = "  file://openssl-oqs.cnf"

RDEPENDS:${PN} += " ${@bb.utils.contains('DISTRO_FEATURES', 'oqs', 'oqs-provider', '', d)}"

do_install:append:class-target () {

    install -m 0644 ${WORKDIR}/openssl-oqs.cnf      ${D}${sysconfdir}/ssl/
    if ${@bb.utils.contains('DISTRO_FEATURES','oqs','true','false',d)}; then
        printf "\n# Enable oqs provider config\n.include /etc/ssl/openssl-oqs.cnf\n"  >> ${D}${sysconfdir}/ssl/openssl.cnf
    fi
}

do_install:append:class-native () {

    install -m 0644 ${WORKDIR}/openssl-oqs.cnf      ${D}${sysconfdir}/ssl/
}

FILES:openssl-conf += "${sysconfdir}/ssl/openssl-oqs.cnf"
