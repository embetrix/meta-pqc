SUMMARY = "OpenSSL 3.x provider containing post-quantum algorithms"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ab9b4308908ace39992d3080dd26824a"

SRC_URI = "git://github.com/open-quantum-safe/oqs-provider.git;branch=main;protocol=https"
SRCREV = "79f23c69b8ed1c788cdaa40d2b7687e1294babd6"

S = "${WORKDIR}/git"

DEPENDS = "liboqs"
RDEPENDS:${PN} += "liboqs"

inherit pkgconfig cmake

EXTRA_OECMAKE = "-DOPENSSL_MODULES_PATH=${libdir}/ossl-modules"

FILES:${PN} += "${libdir}/ossl-modules/oqsprovider.so"

BBCLASSEXTEND = "native nativesdk"
