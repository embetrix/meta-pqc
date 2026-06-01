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

SRCREV = "a55575abb72193e244c653e18ff2b9ef68cf9956"
PV = "v9.7-oqs-snapshot-2025-05"
LIC_FILES_CHKSUM = "file://LICENCE;md5=072979064e691d342002f43cd89c0394"

DEPENDS += "liboqs"

EXTRA_OECONF:append = " --with-liboqs-dir=${RECIPE_SYSROOT}/usr"

do_install:append() {

    install -d ${D}${sysconfdir}/ssh/sshd_config.d
    # Enable classical/hybrid key-exchange mechanisms
    echo "KexAlgorithms curve25519-sha256,mlkem768x25519-sha256,mlkem768-sha256"     > ${D}${sysconfdir}/ssh/sshd_config.d/kex.conf
    # Enable PQC type authentication
    echo "PubkeyAcceptedKeyTypes +ssh-mldsa65"   > ${D}${sysconfdir}/ssh/sshd_config.d/pubkey.conf
    echo "HostkeyAlgorithms      +ssh-mldsa65"  >> ${D}${sysconfdir}/ssh/sshd_config.d/pubkey.conf
    # Increase debug level
    echo "LogLevel DEBUG3"  > ${D}${sysconfdir}/ssh/sshd_config.d/debug.conf
}

FILES:${PN}-sshd += "${sysconfdir}/ssh/sshd_config.d"
