DESCRIPTION = "A command-line benchmarking tool"
HOMEPAGE = "https://github.com/sharkdp/hyperfine"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE-MIT;md5=50c79cd7360537975dfbbd15f8d01a34"

SRC_URI = "git://github.com/sharkdp/hyperfine.git;branch=master;protocol=https"
# SRCREV tagged 1.18.0
SRCREV = "24a0d5da1bff11567bbf307315d11cb0e10733ec"

inherit cargo cargo-update-recipe-crates

require ${BPN}-crates.inc

INSANE_SKIP:${PN} += "already-stripped"
