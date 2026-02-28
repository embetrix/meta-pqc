FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = "  file://openssl-oqs.cnf"

RDEPENDS:${PN} += " ${@bb.utils.contains('DISTRO_FEATURES', 'oqs', 'oqs-provider', '', d)}"

do_install:append () {

    install -m 0644 ${WORKDIR}/openssl-oqs.cnf      ${D}${sysconfdir}/ssl/
    if ${@bb.utils.contains('DISTRO_FEATURES','oqs','true','false',d)}; then
        printf "\n# Enable oqs provider config\n.include openssl-oqs.cnf\n"  >> ${D}${sysconfdir}/ssl/openssl.cnf
    fi
}

FILES:openssl-conf += "${sysconfdir}/ssl/openssl-oqs.cnf"
