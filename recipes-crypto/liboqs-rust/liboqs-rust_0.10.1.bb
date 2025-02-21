DESCRIPTION = "RUST bindings for liboqs library"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE-MIT;md5=bcfa72b8688da4e6e4726cce2e03bab9"

SRC_URI = "git://github.com/open-quantum-safe/liboqs-rust.git;branch=main;protocol=https"
# SRCREV tagged 0.10.1
SRCREV = "d1fead1c5428fad3be89164297948a771c0d6752"

S = "${WORKDIR}/git"

inherit cargo cargo-update-recipe-crates

CARGO_BUILD_FLAGS:remove = " --frozen"
CARGO_BUILD_FLAGS:append = " --offline"

require ${BPN}-crates.inc

DEPENDS = "openssl liboqs"

