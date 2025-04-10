FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
           file://pqc-ca-cert.pem \
           "

do_install:prepend () {
    install -d ${D}${datadir}/ca-certificates/pqc
    install -m 0644 ${WORKDIR}/pqc-ca-cert.pem ${D}${datadir}/ca-certificates/pqc/pqc-ca.crt
}
