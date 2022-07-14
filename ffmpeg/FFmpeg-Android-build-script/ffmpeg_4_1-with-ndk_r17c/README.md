# WATH'S DONE IS DONE

## NOTICE

in the `*.sh` files , `target-os` have been changed to `android` .

`r17c` is the last ndk version that `gcc` can work .

you can change the compile tools with 

```
CC=clang
function build_one
{
    ./configure \
    --prefix=$PREFIX \
    --cc=$CC
    --disable-shared \
    --enable-static \
    ...
}
```

but it seems need to config `SYSROOT` and so on , try it if you will .
