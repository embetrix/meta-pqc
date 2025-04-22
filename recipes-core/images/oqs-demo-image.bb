DESCRIPTION = "OQS Demo Image"

inherit core-image image-buildinfo

IMAGE_FEATURES += "package-management ssh-server-openssh"

IMAGE_INSTALL += "\
    packagegroup-core-boot \
    packagegroup-core-full-cmdline \
    ${CORE_IMAGE_BASE_INSTALL} \
    htop \
    tcpdump \
    hyperfine \
    gdbserver \
    strace \
    nginx \
    curl \
    openssl-bin  \
    "

IMAGE_INSTALL += "\
    liboqs-tests \
    liboqs-cpp-examples \
    python3-liboqs-examples \
    oqs-demos \
    "
