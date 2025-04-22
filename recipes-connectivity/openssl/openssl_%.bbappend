FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = "  file://openssl-oqs.cnf"

RDEPENDS:${PN} += " ${@bb.utils.contains('DISTRO_FEATURES', 'oqs', 'oqs-provider', '', d)}"

do_install:append () {

    if ${@bb.utils.contains('DISTRO_FEATURES','oqs','true','false',d)}; then
        cat ${WORKDIR}/openssl-oqs.cnf  >> ${D}${sysconfdir}/ssl/openssl.cnf
    else
        # can still be activated by setting OPENSSL_CONF env variable
        install -m 0644 ${WORKDIR}/openssl-oqs.cnf      ${D}${sysconfdir}/ssl/
    fi
}

FILES:openssl-conf += "${sysconfdir}/ssl"
