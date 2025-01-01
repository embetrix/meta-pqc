FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://0001-botan-test-set-absoulte-path-for-tests-data.patch"

# Add botan-test binary and tests data
do_install:append () {
    install -d ${D}${bindir}
    install -d ${D}${datadir}/${PN}/tests/data
    install -m 0755 ${B}/botan-test  ${D}${bindir}
    cp -R --no-dereference --preserve=mode,links -v ${B}/src/tests/data/*  ${D}${datadir}/${PN}/tests/data/
}

# Split botan-bin/test into separate packages
PACKAGES += "${PN}-test"
RDEPENDS:${PN}-bin  += "${PN}"
RDEPENDS:${PN}-test += "${PN}"
FILES:${PN}:remove   = "${bindir}/*"
FILES:${PN}-bin:remove   = "${bindir}/*"
FILES:${PN}-bin   = "${bindir}/botan"
FILES:${PN}-test = "${bindir}/botan-test  ${datadir}/${PN}/tests/data"

BBCLASSEXTEND = "native nativesdk"
