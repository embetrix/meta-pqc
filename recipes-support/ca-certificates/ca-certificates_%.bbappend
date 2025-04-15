FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
           file://ca-cert.pem \
           file://pqc-ca-cert.pem \
           "

do_install:prepend () {
    if ${@bb.utils.contains('DISTRO_FEATURES','oqs','true','false',d)}; then
        install -d ${D}${datadir}/ca-certificates/embetrix
        install -m 0644 ${WORKDIR}/ca-cert.pem     ${D}${datadir}/ca-certificates/embetrix/ca.crt
        install -m 0644 ${WORKDIR}/pqc-ca-cert.pem ${D}${datadir}/ca-certificates/embetrix/pqc-ca.crt
    fi
}
