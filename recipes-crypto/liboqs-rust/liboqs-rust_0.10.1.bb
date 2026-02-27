DESCRIPTION = "RUST bindings for liboqs library"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE-MIT;md5=bcfa72b8688da4e6e4726cce2e03bab9"

SRC_URI = "gitsm://github.com/open-quantum-safe/liboqs-rust.git;branch=main;protocol=https"
# SRCREV tagged 0.10.1
SRCREV = "d1fead1c5428fad3be89164297948a771c0d6752"

S = "${WORKDIR}/git"

inherit cargo cargo-update-recipe-crates

TOOLCHAIN = "clang"

CARGO_BUILD_FLAGS:remove = " --frozen"
CARGO_BUILD_FLAGS:append = " --offline"

require ${BPN}-crates.inc

DEPENDS = "openssl liboqs"

# bindgen uses libclang to parse C headers when cross-compiling we must point
# it at the target sysroot so that types like ssize_t resolve to the correct
# size for the target architecture.
export BINDGEN_EXTRA_CLANG_ARGS = "--sysroot=${STAGING_DIR_TARGET}"

# install rlib artifacts
do_install() {
    install -d ${D}${libdir}/rust
    install -m 0644 ${B}/target/${CARGO_TARGET_SUBDIR}/liboqs.rlib ${D}${libdir}/rust/
    install -m 0644 ${B}/target/${CARGO_TARGET_SUBDIR}/liboqs_sys.rlib ${D}${libdir}/rust/
}

FILES:${PN} = "${libdir}/rust"
