SUMMARY = "OpenSSL 3.x provider containing post-quantum algorithms"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ab9b4308908ace39992d3080dd26824a"

SRC_URI = "git://github.com/open-quantum-safe/oqs-provider.git;branch=main;protocol=https \
           file://0001-Allow-overriding-OPENSSL_MODULES_PATH-from-the-comma.patch \
           "
# SRCREV tagged 0.8.0
SRCREV = "ec1e8431f92b52e5d437107a37dbe3408649e8c3"

S = "${WORKDIR}/git"

DEPENDS = "liboqs"
RDEPENDS:${PN} += "liboqs"

inherit pkgconfig cmake

EXTRA_OECMAKE = "-DOPENSSL_MODULES_PATH=${libdir}/ossl-modules"

FILES:${PN} += "${libdir}/ossl-modules/oqsprovider.so"

BBCLASSEXTEND = "native nativesdk"
