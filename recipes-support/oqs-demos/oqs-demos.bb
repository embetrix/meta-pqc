SUMMARY = "OQS Demos using OQS provider"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = " \
           file://ca-cert.pem \
           file://ca-key.pem \
           file://pqc-ca-cert.pem \
           file://pqc-ca-key.pem \
           file://nginx-pqc-ssl.conf \
           file://nginx-hybrid-ssl.conf \
           file://openvpn-pqc.conf \
           file://openvpn-hybrid.conf \
           file://device-certs.sh \
           file://openssl-tls-server.service \
           "

inherit systemd

DEPENDS = "openssl-native oqs-provider-native"
RDEPENDS:${PN} += "openssl-bin oqs-provider openvpn hostname-setup nginx ca-certificates"

do_install () {
    install -d ${D}/opt/oqs-demos/certs
    install -m 0644 ${WORKDIR}/ca-cert.pem  ${D}/opt/oqs-demos/certs
    install -m 0644 ${WORKDIR}/ca-key.pem   ${D}/opt/oqs-demos/certs
    install -m 0644 ${WORKDIR}/pqc-ca-cert.pem  ${D}/opt/oqs-demos/certs
    install -m 0644 ${WORKDIR}/pqc-ca-key.pem   ${D}/opt/oqs-demos/certs
   
    install -d ${D}/opt/oqs-demos/scripts
    install -m 0755 ${WORKDIR}/device-certs.sh ${D}/opt/oqs-demos/scripts/device-certs.sh
    install -d ${D}${sysconfdir}/nginx/conf.d
    install -m 0644 ${WORKDIR}/nginx-pqc-ssl.conf ${D}${sysconfdir}/nginx/conf.d/pqc-ssl.conf
    install -m 0644 ${WORKDIR}/nginx-hybrid-ssl.conf ${D}${sysconfdir}/nginx/conf.d/hybrid-ssl.conf

    install -d ${D}${sysconfdir}/openvpn/server
    install -m 0644 ${WORKDIR}/openvpn-pqc.conf ${D}${sysconfdir}/openvpn/server/openvpn-pqc.conf
    install -m 0644 ${WORKDIR}/openvpn-hybrid.conf ${D}${sysconfdir}/openvpn/server/openvpn-hybrid.conf

}

FILES:${PN} = "/opt/oqs-demos ${sysconfdir}/nginx/conf.d ${sysconfdir}/openvpn/server ${sysconfdir}/systemd"

SYSTEMD_SERVICE:${PN} = "openssl-tls-server.service"
SYSTEMD_PACKAGES = "${PN}"

do_install:append() {
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/openssl-tls-server.service ${D}${systemd_unitdir}/system/

	# Enable openvpn template instances (unit provided by openvpn package)
	install -d ${D}${sysconfdir}/systemd/system/multi-user.target.wants
	ln -sf ${systemd_unitdir}/system/openvpn-server@.service \
		${D}${sysconfdir}/systemd/system/multi-user.target.wants/openvpn-server@openvpn-pqc.service
	ln -sf ${systemd_unitdir}/system/openvpn-server@.service \
		${D}${sysconfdir}/systemd/system/multi-user.target.wants/openvpn-server@openvpn-hybrid.service
}
