SUMMARY = "OQS Demos using OQS provider"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = " \
           file://pqc-ca-cert.pem \
           file://pqc-ca-key.pem \
           file://device-certs.sh \
           file://openssl-tls-server.service \
           "

inherit systemd

RDEPENDS:${PN} += "openssl-bin oqs-provider hostname-setup"

do_install () {
    install -d ${D}/opt/oqs-demos/certs
    install -m 0644 ${WORKDIR}/pqc-ca-cert.pem  ${D}/opt/oqs-demos/certs
    install -m 0644 ${WORKDIR}/pqc-ca-key.pem   ${D}/opt/oqs-demos/certs
    install -d ${D}/opt/oqs-demos/scripts
	install -m 0755 ${WORKDIR}/device-certs.sh ${D}/opt/oqs-demos/scripts/device-certs.sh
}

FILES:${PN} = "/opt/oqs-demos"

SYSTEMD_SERVICE:${PN} = "openssl-tls-server.service"
SYSTEMD_PACKAGES = "${PN}"

do_install:append() {
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/openssl-tls-server.service ${D}${systemd_unitdir}/system/
}
