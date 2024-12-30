# meta-pqc

## Build

```sh
KAS_MACHINE=qemux86-64 kas build kas-pqc.yml
```


## Run in Qemu

```sh
KAS_MACHINE=qemux86-64 kas shell kas-pqc.yml -c 'runqemu kvm serialstdio nographic qemuparams="-m 1024"'
```
