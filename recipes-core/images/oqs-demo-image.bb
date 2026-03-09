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
    liboqs-go-examples \
    python3-liboqs-examples \
    liboqs-rust \
    oqs-demos \
    "

IMAGE_INSTALL += "\
    leancrypto-apps \
    leancrypto-tests \
    "
