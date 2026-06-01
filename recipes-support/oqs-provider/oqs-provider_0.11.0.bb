SUMMARY = "OpenSSL 3.x provider containing post-quantum algorithms"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ab9b4308908ace39992d3080dd26824a"

SRC_URI = "git://github.com/open-quantum-safe/oqs-provider.git;branch=0.11.0;protocol=https"
SRCREV = "a635e341d6a4624d9bba36d158804762f316fe5e"

DEPENDS = "liboqs openssl"
RDEPENDS:${PN} += "liboqs openssl"

inherit pkgconfig cmake

EXTRA_OECMAKE = "-DOPENSSL_MODULES_PATH=${libdir}/ossl-modules"

FILES:${PN} += "${libdir}/ossl-modules/oqsprovider.so"

BBCLASSEXTEND = "native nativesdk"
