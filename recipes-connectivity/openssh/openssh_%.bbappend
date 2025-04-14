# include conditionally
require ${@bb.utils.contains('DISTRO_FEATURES', 'oqs', 'openssh-oqs.inc', '', d)}
