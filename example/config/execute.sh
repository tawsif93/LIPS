#!/bin/sh

# This file is part of the ORBS distribution.
# See the file LICENSE.TXT for more information.

# The execution script.
#
# This script is called from ORBS to actually execute the system.
#
# This script will have to run the system with any input that belong
# to the criterion.  It also has to generate the trajectory which is
# usually just a print of the value of the criterion. Therefore it
# usually has to just filter the output with respect to a given marker
# as passed in parameter $1.

# Configure ORBS for this project.
source config/config.sh

cd work

# The actual execution. Capture the output and any error message.
LC_ALL="en_US" python glue.py 1 00 > test.log 2> execute.log
LC_ALL="en_US" python glue.py 10 00 >> test.log 2>> execute.log

# Usually, the execution will be much more complicated as it has to
# deal with timeouts and crashes.

# Extract projected trajectory from the captured output.
grep "$1" test.log

# all done.
