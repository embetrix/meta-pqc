FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
           file://ca-cert.pem \
           file://pqc-ca-cert.pem \
           "
# Add demo classical CA and PQC CA to certificates truststore 
do_install:prepend () {
    if ${@bb.utils.contains('DISTRO_FEATURES','oqs','true','false',d)}; then
        install -d ${D}${datadir}/ca-certificates/demo
        install -m 0644 ${WORKDIR}/ca-cert.pem     ${D}${datadir}/ca-certificates/demo/ca.crt
        install -m 0644 ${WORKDIR}/pqc-ca-cert.pem ${D}${datadir}/ca-certificates/demo/pqc-ca.crt
    fi
}
