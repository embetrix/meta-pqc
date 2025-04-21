FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Use openssh v9.7 from open quatum safe
SRC_URI = "git://github.com/open-quantum-safe/openssh.git;branch=OQS-v9;protocol=https \
           file://sshd_config \
           file://ssh_config \
           file://init \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${PAM_SRC_URI}', '', d)} \
           file://sshd.service \
           file://sshd.socket \
           file://sshd@.service \
           file://sshdgenkeys.service \
           file://volatiles.99_sshd \
           file://run-ptest \
           file://sshd_check_keys \
           "

SRCREV = "4afbf050df0ec060de9260a37e6648dd68ea847b"
S = "${WORKDIR}/git"
PV = "v9.7-oqs-snapshot-2024-08"

DEPENDS += "liboqs"

EXTRA_OECONF:append = " --with-liboqs-dir=${RECIPE_SYSROOT}/usr"

do_install:append() {

    install -d ${D}${sysconfdir}/ssh/sshd_config.d
    # Enable hybrid mlkem768x25519-sha256 as default key-exchange mechanism
    echo "KexAlgorithms mlkem768x25519-sha256"     > ${D}${sysconfdir}/ssh/sshd_config.d/kex.conf
    # Enable PQC type authentication
    echo "PubkeyAcceptedKeyTypes +ssh-mldsa65"   > ${D}${sysconfdir}/ssh/sshd_config.d/pubkey.conf
    echo "HostkeyAlgorithms      +ssh-mldsa65"  >> ${D}${sysconfdir}/ssh/sshd_config.d/pubkey.conf
    # Increase debug level
    echo "LogLevel DEBUG3"  > ${D}${sysconfdir}/ssh/sshd_config.d/debug.conf
}

FILES:${PN}-sshd += "${sysconfdir}/ssh/sshd_config.d"
