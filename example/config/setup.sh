#!/bin/sh

# This file is part of the ORBS distribution.
# See the file LICENSE.TXT for more information.

# The setup script.
#
# This script is called from ORBS to actually setup the workspace.
#
# This script does not take any parameter.
#
# For complex systems, this will include configuring the system
# (e.g. invoking ./configure).

# Configure ORBS for this project.
source config/config.sh

# prepare the work space
rm -rf work
cp -r orig work

# instrument the project
sed -e '9 s/$/ System.out.println("ORBS: " + dots);/' < orig/checker.java > work/checker.java
