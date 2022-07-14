# WATH'S DONE IS DONE

## configure changes

```
# changes
SLIBNAME_WITH_MAJOR='$(SLIBPREF)$(FULLNAME)-$(LIBMAJOR)$(SLIBSUF)'
LIB_INSTALL_EXTRA_CMD='$$(RANLIB)"$(LIBDIR)/$(LIBNAME)"'
SLIB_INSTALL_NAME='$(SLIBNAME_WITH_MAJOR)'
SLIB_INSTALL_LINKS='$(SLIBNAME)'
```

output `*.so` files

## NOTICE

in the `*.sh` files , `target-os` have been changed to `android` .

## config your `.sh` file

* modify `NDK` path
* modify target cpu type

