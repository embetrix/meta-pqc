SUMMARY = "OQS Demos using OQS provider"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = " \
           file://ca-cert.pem \
           file://ca-key.pem \
           file://pqc-ca-cert.pem \
           file://pqc-ca-key.pem \
           file://nginx-classical.conf \
           file://nginx-hybrid.conf \
           file://nginx-pqc.conf \
           file://openvpn-classical.conf \
           file://openvpn-hybrid.conf \
           file://openvpn-pqc.conf \
           file://mosquitto-classical.conf \
           file://mosquitto-hybrid.conf \
           file://mosquitto-pqc.conf \
           file://device-certs.sh \
           file://device-certs.service \
           file://tls-handshake-bench.sh \
           "

inherit systemd

DEPENDS = "openssl-native oqs-provider-native"
RDEPENDS:${PN} += "openssl-bin oqs-provider openvpn hostname-setup nginx ca-certificates mosquitto mosquitto-clients"

do_install () {
    install -d ${D}/opt/oqs-demos/certs
    install -m 0644 ${WORKDIR}/ca-cert.pem  ${D}/opt/oqs-demos/certs
    install -m 0644 ${WORKDIR}/ca-key.pem   ${D}/opt/oqs-demos/certs
    install -m 0644 ${WORKDIR}/pqc-ca-cert.pem  ${D}/opt/oqs-demos/certs
    install -m 0644 ${WORKDIR}/pqc-ca-key.pem   ${D}/opt/oqs-demos/certs
   
    install -d ${D}${sysconfdir}/nginx/conf.d
    install -m 0644 ${WORKDIR}/nginx-classical.conf ${D}${sysconfdir}/nginx/conf.d/nginx-classical.conf
    install -m 0644 ${WORKDIR}/nginx-hybrid.conf ${D}${sysconfdir}/nginx/conf.d/nginx-hybrid.conf
    install -m 0644 ${WORKDIR}/nginx-pqc.conf ${D}${sysconfdir}/nginx/conf.d/nginx-pqc.conf

    install -d ${D}${sysconfdir}/openvpn/server
    install -m 0644 ${WORKDIR}/openvpn-classical.conf ${D}${sysconfdir}/openvpn/server/openvpn-classical.conf
    install -m 0644 ${WORKDIR}/openvpn-hybrid.conf ${D}${sysconfdir}/openvpn/server/openvpn-hybrid.conf
    install -m 0644 ${WORKDIR}/openvpn-pqc.conf ${D}${sysconfdir}/openvpn/server/openvpn-pqc.conf

    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/device-certs.sh       ${D}${bindir}/device-certs
    install -m 0755 ${WORKDIR}/tls-handshake-bench.sh ${D}${bindir}/tls-handshake-bench

    install -d ${D}${sysconfdir}/mosquitto/conf.d
    install -m 0644 ${WORKDIR}/mosquitto-classical.conf  ${D}${sysconfdir}/mosquitto/conf.d
    install -m 0644 ${WORKDIR}/mosquitto-hybrid.conf     ${D}${sysconfdir}/mosquitto/conf.d
    install -m 0644 ${WORKDIR}/mosquitto-pqc.conf        ${D}${sysconfdir}/mosquitto/conf.d
}

SYSTEMD_SERVICE:${PN} = "device-certs.service"
SYSTEMD_PACKAGES = "${PN}"

do_install:append() {
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/device-certs.service ${D}${systemd_unitdir}/system/

	# Enable units template instances provided by openvpn
    install -d ${D}${sysconfdir}/systemd/system/multi-user.target.wants
	ln -sf ${systemd_unitdir}/system/openvpn-server@.service \
		${D}${sysconfdir}/systemd/system/multi-user.target.wants/openvpn-server@openvpn-classical.service
	ln -sf ${systemd_unitdir}/system/openvpn-server@.service \
		${D}${sysconfdir}/systemd/system/multi-user.target.wants/openvpn-server@openvpn-hybrid.service
	ln -sf ${systemd_unitdir}/system/openvpn-server@.service \
		${D}${sysconfdir}/systemd/system/multi-user.target.wants/openvpn-server@openvpn-pqc.service
}

FILES:${PN} = "/opt/oqs-demos ${bindir} \
              ${sysconfdir}/nginx/conf.d \
              ${sysconfdir}/openvpn/server \
              ${sysconfdir}/mosquitto/conf.d \
              ${sysconfdir}/systemd \
             "
