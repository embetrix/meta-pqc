RDEPENDS:${PN} += " ${@bb.utils.contains('DISTRO_FEATURES', 'pqc', 'oqs-provider', '', d)}"

do_install:append () {

    if ${@bb.utils.contains('DISTRO_FEATURES','pqc','true','false',d)}; then
        # Enable oqsprovider
        sed -i "s/default = default_sect/default = default_sect\noqsprovider = oqsprovider_sect/g" ${D}${sysconfdir}/ssl/openssl.cnf
        sed -i "s/\[default_sect\]/\[default_sect\]\nactivate = 1\n\[oqsprovider_sect\]\nactivate = 1\n/g" ${D}${sysconfdir}/ssl/openssl.cnf

        # Selecting TLS1.3 default groups
        sed -i "s/providers = provider_sect/providers = provider_sect\nssl_conf = ssl_sect\n\n\[ssl_sect\]\nsystem_default = system_default_sect\n\n\[system_default_sect\]\nGroups = \$ENV\:\:DEFAULT_GROUPS\n/g" ${D}${sysconfdir}/ssl/openssl.cnf
        sed -i "s/HOME\t\t\t= ./HOME           = .\nDEFAULT_GROUPS = kyber768/g" ${D}${sysconfdir}/ssl/openssl.cnf
    fi
}
