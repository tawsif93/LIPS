/* This file is part of the ORBS distribution.
 * See the file LICENSE.TXT for more information. 
 */
#include <stdio.h>
#include <stdlib.h>

int p(int j);
int q(int k);

int f1(int k);
int f2(int k);
int f3(int j);

int main(int argc, char *argv[])
{
    int j;
    int k;

    j = (int) strtol(argv[1], NULL, 10);
    k = (int) strtol(argv[2], NULL, 10);

    while (p(j))
    {
        if (q(k))
        {
            k = f1(k);
        }
        else
        {
            k = f2(k);
            j = f3(j);
        }
    }
    printf("%d\n", j);
}

int p(int j)
{
    if(j > 0)
    {
        return 1;
    }
    else
    {
        return 0;
    }
}

int q(int k)
{
    if(k < 0)
    {
        return 1;
    }

    return 0;
}

int f1(int k)
{
    return k + 10;
}

int f2(int k)
{
    return k - 2;
}

int f3(int j)
{
    return j - 15;
}

