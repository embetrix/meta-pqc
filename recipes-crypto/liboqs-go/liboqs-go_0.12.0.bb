DESCRIPTION = "GO bindings for liboqs library"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=f3a7c2d87a74d51c6273ba64bc7c47ee"

SRC_URI = "git://github.com/open-quantum-safe/liboqs-go.git;branch=main;protocol=https"
# SRCREV tagged 0.12.0
SRCREV = "eeb454f5d03ad474c6c353a11fab587c6029ac5c"

S = "${WORKDIR}/git"

inherit go-mod pkgconfig

GO_IMPORT = "github.com/open-quantum-safe/liboqs-go"

# Build only the example binaries (the oqs library package is compiled
# automatically as a dependency). Skip client_server_kem which needs
# a network setup with separate client/server processes.
GO_INSTALL = " \
    ${GO_IMPORT}/examples/kem \
    ${GO_IMPORT}/examples/sig \
    ${GO_IMPORT}/examples/rand \
"

DEPENDS = "liboqs"
RDEPENDS:${PN} += "liboqs"

# Point the bundled .pc file at the sysroot and drop the macOS Ldflags
do_configure:prepend() {
    sed -i \
        -e "s|LIBOQS_INCLUDE_DIR=.*|LIBOQS_INCLUDE_DIR=${STAGING_INCDIR}|" \
        -e "s|LIBOQS_LIB_DIR=.*|LIBOQS_LIB_DIR=${STAGING_LIBDIR}|" \
        -e "/Ldflags:/d" \
        ${S}/src/${GO_IMPORT}/.config/liboqs-go.pc
}

# Make liboqs-go.pc visible to pkg-config
export PKG_CONFIG_PATH:append = ":${S}/src/${GO_IMPORT}/.config"

do_install:append() {
    for f in kem sig rand; do
            mv ${D}${bindir}/${f} ${D}${bindir}/oqs_go_${f}
    done
}

# Split examples into a separate package
PACKAGES =+ "${PN}-examples"
FILES:${PN}:remove = "${bindir}/*"
FILES:${PN}-examples = "${bindir}"
RDEPENDS:${PN}-examples += "${PN}"

ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
