DESCRIPTION = "PQC Demo Image"

inherit core-image

IMAGE_FEATURES += "ssh-server-openssh"

IMAGE_INSTALL += "\
    packagegroup-core-boot \
    packagegroup-core-full-cmdline \
    ${CORE_IMAGE_BASE_INSTALL} \
    strace \
    nginx \
    curl \
    openssl-bin  \
    liboqs \
    liboqs-tests \
    oqs-provider \
    botan \
    "
