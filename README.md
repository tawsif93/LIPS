# LIPS
Language Independent Program Slicing Tool
Based on [ORBS](http://ieeexplore.ieee.org/abstract/document/7335396/) published in 2014 byt D.Binkley 


This language independent tool based on ORBS with slight modification. A conditional statement checking is introduced in this modification. It is a command java based line tool.

To use this source for have to be identified from command line. Command line parameter `-s` to give the source folder. It `-s` source is not present `example/orig` is taken by default.   

For example`-s original`

Output of the sliced programs are stored in `work` folder. To change the output folder `-o` command line parameter is used.

For example `-o output`

Source starting point and a list of source program argument have to be provided in command line argument with `-a` parameter. 

For example `-a glue.py 10 00`

An example of full command `-s toh -o work -a TowerOfHanoi.class 10 `

Statistics of slices are stored in `logs` folder 