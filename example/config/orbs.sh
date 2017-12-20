#!/bin/sh

# This file is part of the ORBS distribution.
# See the file LICENSE.TXT for more information.

# Invoke ORBS.

# Configure ORBS for this project.
source config/config.sh

# Pass the arguments to ORBS together with the list of files to be sliced.
python  "$ORBS_SCRIPT" $* $ORBS_FILES
