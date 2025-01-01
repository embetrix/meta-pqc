DESCRIPTION = "PQC Demo Image"

inherit core-image

IMAGE_FEATURES += "package-management ssh-server-openssh"

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
    liboqs-tests \
    liboqs-cpp-examples \
    python3-liboqs \
    "

IMAGE_INSTALL += "\
    botan \
    botan-bin \
    botan-test \
    "
