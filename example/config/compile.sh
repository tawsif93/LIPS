#!/bin/sh

# This file is part of the ORBS distribution.
# See the file LICENSE.TXT for more information.

# The compilation script.
#
# This script is called from ORBS to actually compile (build) the system.
#
# The script must return a generated signature if compilation was
# successful and must return "FAIL" if compilation was not
# successful. The signature should be, for example, an md5 hash over
# the generated binary.

# Configure ORBS for this project.
source config/config.sh

cd work

# cleanup from previous run (just to make sure)
rm -f reader checker.class

# The actual compilation.  This can be replaced by any mechanism to
# build the system, e.g. by invoking make.

gcc $CFLAGS -o reader reader.c 2> compile.log
javac checker.java 2>> compile.log

# check successful compilation and create signature
if [ -f reader -a -f checker.class ]; then
    # The signature must be created from anything that actually
    # influences the execution which is affected by the slicing
    # operation. Usually it is any executed part, binaries and
    # scripts.
    md5sum reader checker.class glue.py
else
    # In case compilation fails, "FAIL" must be returned.
    echo FAIL
fi

