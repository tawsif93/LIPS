import commands
import sys
use_locale = True
currency = "?"
decimal = ","

if use_locale:
  currency = commands.getoutput('./work/reader.cout 0')
  decimal = commands.getoutput('./work/reader.cout 1')

cmd = ('java -cp work checker ' + currency
       + sys.argv[1] + decimal + sys.argv[2])
print commands.getoutput(cmd)
