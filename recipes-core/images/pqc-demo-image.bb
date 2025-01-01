DESCRIPTION = "PQC Demo Image"

inherit core-image

IMAGE_FEATURES += "ssh-server-openssh"

IMAGE_INSTALL += "\
    packagegroup-core-boot \
    packagegroup-core-full-cmdline \
    ${CORE_IMAGE_BASE_INSTALL} \
    htop \
    tcpdump \
    gdbserver \
    strace \
    nginx \
    curl \
    openssl-bin  \
    "

IMAGE_INSTALL += "\
    liboqs \
    liboqs-tests \
    liboqs-cpp-examples \
    oqs-provider \
    python3-liboqs \
    "

IMAGE_INSTALL += "\
    botan \
    botan-bin \
    botan-test \
    "
